package demo

import helperClass.MX

/**
 * Created by kasonchan on 10/9/15.
 */
object Demo {

  def main(args: Array[String]) {
    val inputs = List(MX(Some(1.5), Some(3)), MX(Some(4), Some(2.5)))
    val outputs = List(MX(Some(5), None))

    println(solve(inputs, outputs))
  }

  def multiplySum(inputs: List[MX]): Double = {
    inputs.foldLeft(0.0) {
      (m, n) => m + n.M.getOrElse(0.0) * n.X.getOrElse(0.0)
    }
  }

  def group(puts: List[MX]): Either[Double, (MX, Double)] = {
    val spanned = puts span {
      mx => !mx.M.isDefined || !mx.X.isDefined
    }

    spanned match {
      case (List(), t) => Left(multiplySum(t))
      case (h, t) => Right(h.head, multiplySum(t))
    }
  }

  def solve(inputs: List[MX], outputs: List[MX]): Double = {
    val i = group(inputs)
    val o = group(outputs)

    o match {
      case Left(l) => 0.0
      case Right(r) => r match {
        case (mx, sum) =>
          (mx.M, mx.X) match {
            case (Some(x), None) => (i.left.get - sum) / x
            case (None, Some(x)) => (i.left.get - sum) / x
            case _ => 0.0
          }
        case _ => 0.0
      }
    }
  }

}
