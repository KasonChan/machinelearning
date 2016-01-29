package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by kasonchan on 10/19/15.
  * Implemented with Akka streams.
  */
object RADemo {

  implicit val as = ActorSystem("actorSystem")
  implicit val am = ActorMaterializer()

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  def load(pathName: String): Iterator[String] = scala.io.Source.fromFile(pathName).getLines()

  case class Info(site: String,
                  tag: String,
                  spcode: String,
                  date: String,
                  pdPSI: Option[Double],
                  mdPSI: Option[Double],
                  year: Int)

  def main(args: Array[String]) {
    val lines = Source(() => load(pathName))
    val rowFlow = Flow[String].drop(1).map(line => line.split(",").toList)

    val printlnSink = Sink.foreach(println)

    //    Uncomment to print data
    //    (lines via rowFlow).runForeach(println)
    //    (lines via rowFlow).runWith(printlnSink)

    val transformFlow = Flow[List[String]].collect {
      case List(site, tag, spcode, date, pdPSI, mdPSI, year) =>
        (pdPSI, mdPSI) match {
          case ("NA", "NA") => Info(site, tag, spcode, date, None, None, year.toInt)
          case ("NA", m: String) => Info(site, tag, spcode, date, None, Some(m.toDouble), year.toInt)
          case (p: String, "NA") => Info(site, tag, spcode, date, Some(p.toDouble), None, year.toInt)
          case (p: String, m: String) => Info(site, tag, spcode, date, Some(p.toDouble), Some(m.toDouble), year.toInt)
        }
    }

    //    Uncomment to print transformed data
    //    (lines via rowFlow via transformFlow).runForeach(println)
    //    (lines via rowFlow via transformFlow).runWith(printlnSink)

    /**
      * Q1 How many different species are recorded in these data?
      * 5 species
      */
    val speciesFlow = Flow[Info].groupBy(x => x.spcode)
    val speciesCountFlow = Flow[Info].groupBy(x => x.spcode).fold(0)((x, i) => x + 1)

    (lines via rowFlow via transformFlow via speciesFlow)
      .runFold(0)((x, i) => x + 1)
      .foreach(println)

    (lines via rowFlow via transformFlow via speciesCountFlow)
      .runWith(printlnSink)

    /**
      * Q2 Mid day water potential should always be at least as negative as pre-dawn
      * water potential. Are there any days and plants for which mid-day water
      * potential is higher than pre-dawn?
      * "08/25/12" "05/22/13" "04/10/11" "05/24/13" "05/23/13" "07/21/11"
      */
    val dateFlow = Flow[Info].filter(x =>
      x.mdPSI.getOrElse(0.0) > x.pdPSI.getOrElse(0.0))
      .groupBy(y => y.date)
      .map(z => z._1)

    (lines via rowFlow via transformFlow via dateFlow)
      .runFold("")((x, i) => i + " " + x).foreach(println)

    /**
      * Q3 What is the lowest (most negative) mid-day water potential in this data set?
      * When and for which species was this value recorded?
      */
    val lowestMdPSIFlow = Flow[Info].fold(Info("", "", "", "", None, None, 0))((m, i) =>
      if (i.mdPSI.getOrElse(0.0) < m.mdPSI.getOrElse(0.0)) i else m).map(x => (x.spcode, x.mdPSI, x.date))

    (lines via rowFlow via transformFlow via lowestMdPSIFlow)
      .runForeach(r =>
        println("The lowest mid-day water potential in this data set is " +
          r._2.getOrElse(0.0) +
          ", it is recorded on " +
          r._3 +
          " for " +
          r._1 +
          " sp."))

    /**
      * Q4 For which year was the average mid day water potential lowest (most negative)?
      */
    val lowestMdPSIAverageYear = Flow[Info].groupBy(_.year)
      .map(i => i._2.filter(x => x.mdPSI.isDefined))

    (lines via rowFlow via transformFlow via lowestMdPSIAverageYear).runForeach(println)

    as awaitTermination (2 minutes)
  }

}
