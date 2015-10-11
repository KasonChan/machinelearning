package primitivetypes

/**
 * Created by kasonchan on 10/9/15.
 */
trait PrimitivesTypes {

  type XY = (Double, Double)

  type XYTSeries = Array[(Double, Double)]

  type DMatrix[T] = Array[Array[T]]

  type DVector[T] = Array[T]

  type DblMatrix = DMatrix[Double]

  type DblVector = Array[Double]

}
