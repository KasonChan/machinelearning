package operators

import primitivetypes.PrimitivesTypes

/**
 * Created by kasonchan on 10/12/15.
 */
trait Operator extends PrimitivesTypes {

  def Op[T <% Double](v: DVector[T], w: DblVector, op: (T, Double) =>
    Double): DblVector = v.zipWithIndex.map(x => op(x._1, w(x._2)))

  implicit def /(v: DblVector, n: Int): DblVector = v.map(x => x / n)

  implicit def /(m: DblMatrix, col: Int, z: Double): Unit = {
    (0 until m(col).size).foreach(i => m(col)(i) / z)
  }

}
