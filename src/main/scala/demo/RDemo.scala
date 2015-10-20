package demo

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
 * Created by kasonchan on 10/19/15.
 */
object RDemo {

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  def load(pathName: String): Either[String, Array[Array[String]]] = {
    Try {
      val src = Source.fromFile(pathName)
      val fields = src.getLines().map(_.split(CSV_DELIM)).toArray
      fields
    } match {
      case Success(f) => Right(f)
      case Failure(e) => Left(e.toString)
    }
  }

  def main(args: Array[String]) {
    val data = load(pathName)

    data match {
      case Left(e) => println("Failure: " + e)
      case Right(f) => f.foreach(c => println(c.mkString))
    }
  }

}
