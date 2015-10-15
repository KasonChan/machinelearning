package scala

import flow.Flow.solve
import helperClass.MX
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by kasonchan on 10/14/15.
 */
class Flow extends FlatSpec with Matchers {

  "(1.5 * 3) + (4 * 2.5) = (5 * X)" should "X = 2.9" in {
    val inputs = List(MX(Some(1.5), Some(3)), MX(Some(4), Some(2.5)))
    val outputs = List(MX(Some(5), None))
    solve(inputs, outputs) equals 2.9
  }

  "(1.5 * 3) + (4 * 2.5) = (M * 5)" should "M = 2.9" in {
    val inputs = List(MX(Some(1.5), Some(3)), MX(Some(4), Some(2.5)))
    val outputs = List(MX(None, Some(5)))
    solve(inputs, outputs) equals 2.9
  }

  "(10 * 20) + (5 * 40) = (M * 15)" should "M = 26.7" in {
    val inputs = List(MX(Some(10), Some(20)), MX(Some(5), Some(40)))
    val outputs = List(MX(None, Some(15)))
    solve(inputs, outputs) equals 26.7
  }

  "(10 * 20) + (5 * 40) = (15 * X)" should "M = 26.7" in {
    val inputs = List(MX(Some(10), Some(20)), MX(Some(5), Some(40)))
    val outputs = List(MX(Some(15), None))
    solve(inputs, outputs) equals 26.7
  }

}