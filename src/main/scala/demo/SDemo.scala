package demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.{Failure, Success, Try}

/**
  * Created by kasonchan on 1/29/16.
  * Implemented with Spark.
  */
object SDemo {

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"
  private val conf = new SparkConf().setAppName("Read").setMaster("local[*]")
  private val sc = new SparkContext(conf)

  case class Info(site: String,
                  tag: String,
                  spcode: String,
                  date: String,
                  pdPSI: Option[Double],
                  mdPSI: Option[Double],
                  year: Int)

  def load(pathName: String): Either[String, Array[Info]] = {
    Try {
      val productsLines: RDD[String] = sc.textFile(pathName)
      productsLines
        .collect()
        .drop(1)
        .map(l => l.split(CSV_DELIM))
        .collect {
          case Array(site, tag, spcode, date, pdPSI, mdPSI, year) =>
            (pdPSI, mdPSI) match {
              case ("NA", "NA") => Info(site, tag, spcode, date, None, None, year.toInt)
              case ("NA", m: String) => Info(site, tag, spcode, date, None, Some(m.toDouble), year.toInt)
              case (p: String, "NA") => Info(site, tag, spcode, date, Some(p.toDouble), None, year.toInt)
              case (p: String, m: String) => Info(site, tag, spcode, date, Some(p.toDouble), Some(m.toDouble), year.toInt)
            }
        }
    } match {
      case Success(s) => Right(s)
      case Failure(f) => Left(f.toString)
    }
  }

  def main(args: Array[String]) {

    val lines = load(pathName)

    lines match {
      case Right(r) => r.foreach(println)
      case Left(l) => println(l)
    }

  }

}
