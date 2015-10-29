package demo

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
 * Created by kasonchan on 10/19/15.
 */
object RDemo {

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  case class Info(site: String,
                  tag: String,
                  spcode: String,
                  date: String,
                  pdPSI: String,
                  mdPSI: String,
                  year: String)

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
   * @param data the array of array of String
   */
  def transformToInfo(data: Array[Array[String]]): Array[Info] = {
    data.collect {
      case Array(site, tag, spcode, date, pdPSI, mdPSI, year) => Info(site, tag, spcode, date, pdPSI, mdPSI, year)
    }
  }

  /**
   * How many different species are recorded in these data?
   * Returns the different species count
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
   * @param i the array of info
   */
  def species(i: Option[Array[Info]]): Option[Array[String]] = {
    i match {
      case None => None
      case Some(a: Array[Info]) => Some(a.map(c => c.spcode).distinct)
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
  }

}
