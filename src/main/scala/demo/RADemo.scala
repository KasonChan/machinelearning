package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by kasonchan on 10/19/15.
 */
object RADemo {

  implicit val as = ActorSystem("actorSystem")
  implicit val am = ActorMaterializer()

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  def load(pathName: String): Iterator[String] = io.Source.fromFile(pathName).getLines()

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

    (lines via rowFlow).runForeach(println)

    val transformFlow = Flow[List[String]].collect {
      case List(site, tag, spcode, date, pdPSI, mdPSI, year) =>
        (pdPSI, mdPSI) match {
          case ("NA", "NA") => Info(site, tag, spcode, date, None, None, year.toInt)
          case ("NA", m: String) => Info(site, tag, spcode, date, None, Some(m.toDouble), year.toInt)
          case (p: String, "NA") => Info(site, tag, spcode, date, Some(p.toDouble), None, year.toInt)
          case (p: String, m: String) => Info(site, tag, spcode, date, Some(p.toDouble), Some(m.toDouble), year.toInt)
        }
    }

    (lines via rowFlow via transformFlow).runForeach(println)

    val speciesFlow = Flow[Info].groupBy(x => x.spcode)
    val speciesCountFlow = Flow[Info].groupBy(x => x.spcode).fold(0)((x, i) => x + 1)
    val speciesCountSink = Sink.foreach(println)

    /**
     * Q1 How many different species are recorded in these data?
     * 5 species
     */
    (lines via rowFlow via transformFlow via speciesFlow).runFold(0)((x, i) => x + 1).foreach(println)
    (lines via rowFlow via transformFlow via speciesCountFlow).runWith(speciesCountSink)

    as awaitTermination (60 seconds)
  }

}
