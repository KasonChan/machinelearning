package typeconversions

import primitivetypes.PrimitivesTypes

/**
 * Created by kasonchan on 10/10/15.
 */
trait TypeConversions extends PrimitivesTypes {

  implicit def int2Double(n: Int): Double = n.toDouble

  implicit def vectorT2DblVector[T <% Double](vt: DVector[T]) =
    vt.map { t => t.toDouble }

  implicit def double2DblVector(x: Double): DblVector = Array[Double](x)

  implicit def dblPair2DbLVector(x: (Double, Double)): DblVector =
    Array[Double](x._1, x._2)

  implicit def dblPairs2DblRows(x: (Double, Double)): DblMatrix =
    Array[Array[Double]](Array[Double](x._1, x._2))

}
