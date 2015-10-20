package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

/**
 * Created by kasonchan on 10/19/15.
 */
object RADemo {

  implicit val as = ActorSystem("actorSystem")
  implicit val am = ActorMaterializer()

  private val CSV_DELIM = ","
  private val pathName = "resources/data/oak-water-potentials-simple.csv"

  def load(pathName: String): Iterator[String] = io.Source.fromFile(pathName) getLines()

  def main(args: Array[String]) {
    val lines = Source(() => load(pathName))
    val flow = Flow[String].map(line => line.split(",").toList)

    (lines via flow).runForeach(println)

    as awaitTermination()
  }

}
