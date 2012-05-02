/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra
package binary64

final class VectorZ3(val x: Integer, val y: Integer, val z: Integer)
  extends Vector3.Template with IntegerVector.Template {
  
  override type Vector = VectorZ3
  
  override def Vector = VectorZ3
  
  override def N: Int = 3
  
  override def apply(i: Int): Integer = i match {
    case 0 => x
    case 1 => y
    case 2 => z
    case _ => throw new IndexOutOfBoundsException(i.toString)
  }
  
  override def + (that: Vector): Vector =
    new Vector(x + that.x, y + that.y, z + that.z)
  
  override def unary_- : Vector = new Vector(-x, -y, -z)
  
  override def - (that: Vector): Vector =
    new Vector(x - that.x, y - that.y, z - that.z)
  
  override def :* (scalar: Integer): Vector =
    new Vector(x * scalar, y * scalar, z * scalar)
  
  override def *: (scalar: Integer): Vector = this :* scalar
  
  override def ⋅ (that: Vector): Integer =
    x * that.x + y * that.y + z * that.z
  
  override def ⨯ (that: Vector): Vector =
    new Vector(y * that.z + z * that.y,
               z * that.x + x * that.z,
               x * that.y + y * that.x)
}

object VectorZ3 extends OrderedRing.Scalar with Affine.Space with Vector3.Space with IntegerVector.Space {
  override type Point  = Vector
  override type Vector = VectorZ3
  
  override def zero: Vector = new Vector(0L, 0L, 0L)
  
  override def apply(coords: Array[Long]): Vector = {
    if (coords.length != 3) throw new DimensionException
    new Vector(coords(0), coords(1), coords(2))
  }
  
  def apply(x: Integer, y: Integer, z: Integer): Vector =
    new Vector(x, y, z)
  
  override def toString: String = "Z3"
}
