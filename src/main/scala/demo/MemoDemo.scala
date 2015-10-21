package demo

import scala.collection.mutable

/**
 * Created by kasonchan on 10/20/15.
 */
case class Memo[A, B](f: A => B) extends (A => B) {
  private val cache = mutable.Map.empty[A, B]

  def apply(x: A) = cache getOrElseUpdate(x, f(x))
}

case class Memo2[A, B](f: A => B) extends (A => B) {
  val cache = scala.collection.mutable.Map.empty[A, B]

  def apply(i: A): B = cache.get(i) match {
    case Some(l) => l
    case None =>
      val l = f(i)
      cache.put(i, l)
      l
  }
}

object MemoDemo extends App {

  val fib: Memo[Int, BigInt] = Memo {
    case 0 => 0
    case 1 => 1
    case n => fib(n - 1) + fib(n - 2)
  }

  println(fib(100))
  println(fib(300))

  val fib2: Memo2[Int, BigInt] = Memo2 {
    case 0 => 0
    case 1 => 1
    case n => fib(n - 1) + fib(n - 2)
  }

  println(fib2(100))
  println(fib2(300))

  val sum: Memo[Int, BigInt] = Memo {
    case 0 => 0
    case n => n + sum(n - 1)
  }

  println(sum(1000))

  val sum2: Memo2[Int, BigInt] = Memo2 {
    case 0 => 0
    case n => n + sum(n - 1)
  }

  println(sum2(1000))

}

