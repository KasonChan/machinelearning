package demo

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by kasonchan on 10/19/15.
  * Implemented with Scala transformation.
  */
object RDemo {

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  case class Info(site: String,
                  tag: String,
                  spcode: String,
                  date: String,
                  pdPSI: Option[Double],
                  mdPSI: Option[Double],
                  year: Int)

  def load(pathName: String): Either[String, Array[Array[String]]] = {
    Try {
      val src = Source.fromFile(pathName)
      val fields = src.getLines().map(_.split(CSV_DELIM)).toArray
      val rows = fields.drop(1)
      src.close()
      rows
    } match {
      case Success(f) => Right(f)
      case Failure(e) => Left(e.toString)
    }
  }

  /**
    * Transform the array of data into array of info
    *
    * @param data the array of array of String
    */
  def transformToInfo(data: Array[Array[String]]): Array[Info] = {
    data.collect {
      case Array(site, tag, spcode, date, pdPSI, mdPSI, year) =>
        (pdPSI, mdPSI) match {
          case ("NA", "NA") => Info(site, tag, spcode, date, None, None, year.toInt)
          case ("NA", m: String) => Info(site, tag, spcode, date, None, Some(m.toDouble), year.toInt)
          case (p: String, "NA") => Info(site, tag, spcode, date, Some(p.toDouble), None, year.toInt)
          case (p: String, m: String) => Info(site, tag, spcode, date, Some(p.toDouble), Some(m.toDouble), year.toInt)
        }
    }
  }

  /**
    * How many different species are recorded in these data?
    * Returns the different species count
    *
    * @param i the array of info
    */
  def speciesCount(i: Option[Array[Info]]): Option[Int] = {
    i match {
      case None => None
      case Some(a: Array[Info]) => Some(a.map(c => c.spcode).distinct.length)
    }
  }

  /**
    * Returns the different species
    *
    * @param i the array of info
    */
  def species(i: Option[Array[Info]]): Option[Array[String]] = {
    i match {
      case None => None
      case Some(a: Array[Info]) => Some(a.map(c => c.spcode).distinct)
    }
  }

  /**
    * Mid day water potential should always be at least as negative as pre-dawn
    * water potential. Are there any days and plants for which mid-day water
    * potential is higher than pre-dawn?
    * Returns the different species and dates
    *
    * @param i the array of info
    */
  def dates(i: Option[Array[Info]]): Option[Array[String]] = {
    i match {
      case None => None
      case Some(a: Array[Info]) => Some(a.filter(x => x.mdPSI.getOrElse(0.0) > x.pdPSI.getOrElse(0.0))
        .map(y => y.date).distinct)
    }
  }

  /**
    * What is the lowest (most negative) mid-day water potential in this data set?
    * When and for which species was this value recorded?
    *
    * @param i the array of info
    */
  def lowestMdPSI(i: Option[Array[Info]]): Option[Array[(String, Option[Double], String)]] = {
    i match {
      case None => None
      case Some(a: Array[Info]) =>
        Some(a.filter(y =>
          y.mdPSI.getOrElse(0.0) == a.map(x => x.mdPSI.getOrElse(0.0)).min)
          .map(h => (h.spcode, h.mdPSI, h.date)))
    }
  }

  /**
    * For which year was the average mid day water potential lowest (most negative)?
    *
    * @param i the array of info
    */
  def lowestMdPSIAverageYear(i: Option[Array[Info]]) = {
    i match {
      case None => None
      case Some(a: Array[Info]) => a.groupBy(_.year)
        .map(i => (i._1, i._2.map(x => x.mdPSI)))
        .map(i => (i._1, i._2.flatten))
        .map(j => (j._1, j._2.sum / j._2.length))
        .minBy(_._2)._1
    }
  }

  def main(args: Array[String]) {
    val data = load(pathName)

    val information = data match {
      case Left(e) => println("Failure: " + e)
        None
      case Right(f) => f.foreach(c => println(c.mkString))
        Some(transformToInfo(f))
    }

    information match {
      case None =>
      case Some(a: Array[Info]) => a.foreach(println)
    }

    // Q1
    // 5 species
    val q1 = speciesCount(information).getOrElse(0)
    println(q1)

    // Q2
    // "07/21/11", "05/23/13", "05/24/13", "04/10/11", "05/22/13", "08/25/12"
    val q2 = dates(information).getOrElse(Array(""))
    println(q2.mkString("", ", ", ""))

    // Q3
    // The lowest mid-day water potential in this data set is -6.75, it is
    // recorded on "04/10/11" for "QUGR3 " sp.
    val q3 = lowestMdPSI(information).getOrElse(Array(("", None, "")))
    q3.foreach(r =>
      println("The lowest mid-day water potential in this data set is " +
        r._2.getOrElse(0.0) +
        ", it is recorded on " +
        r._3 + " for " + r._1 + " sp."))

    // Q4
    // 11
    val q4 = lowestMdPSIAverageYear(information)
    println(q4)
  }

}
