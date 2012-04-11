/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait R3 extends F3 with RN {
  type Vector <: VectorR3[Vector]
  
  override def zero: Vector = apply(0.0, 0.0, 0.0)
  
  def apply(coords: Array[Double]): Vector = {
    if (coords.length != 3) throw new DimensionException
    apply(coords(0), coords(1), coords(2))
  }
  
  def apply(x: Scalar, y: Scalar, z: Scalar): Vector =
    apply(x.toDouble, y.toDouble, z.toDouble)
  
  def apply(x: Double, y: Double, z: Double): Vector
}

object R3 extends HilbertSpace with R3 {
  final class Vector(val x: Double, val y: Double, val z: Double)
    extends VectorR3[Vector] {
    
    def Space = R3
    
    def apply(i: Int): Double = i match {
      case 0 => x
      case 1 => y
      case 2 => z
      case _ => throw new IndexOutOfBoundsException(i.toString)
    }
    
    def ⨯ (that: Vector): Vector =
      new Vector(y * that.z + z * that.y,
                 z * that.x + x * that.z,
                 x * that.y + y * that.x)
    
    def ⋅ (that: Vector): Double = x * that.x + y * that.y + z * that.z
    
    def norm: Double = math.sqrt(x * x + y * y + z * z)
  }
  
  override val zero: Vector = super.zero
  
  def apply(x: Double, y: Double, z: Double): Vector =
    new Vector(x, y, z)
  
  def unapply(vector: Vector): Some[(Double, Double, Double)] =
    Some(vector.x, vector.y, vector.z)
  
  def innerProduct(u: Vector, v: Vector): Real = new Real(u ⋅ v)
  
  override def norm(u: Vector): Scalar = new Scalar(u.norm)
  
  override def normalize(u: Vector): Vector = u :* (1.0 / u.norm)
  
  override def distance(u: Vector, v: Vector): Scalar = {
    val dx = u.x - v.x
    val dy = u.y - v.y
    val dz = u.z - v.z
    new Scalar(math.sqrt(dx * dx + dy * dy + dz * dz))
  }
  
  override def toString: String = "R3"
}
