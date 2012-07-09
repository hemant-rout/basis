/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra
package binary64

/** A 3-dimensional real vector space.
  * 
  * @author Chris Sachs
  */
object R3 extends F3[Real.type] with RN {
  final class Element(val x: Real, val y: Real, val z: Real)
    extends super[F3].Element with super[RN].Element {
    
    override def N: Int = 3
    
    override def apply(i: Int): Real = i match {
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
    
    override def :* (scalar: Real): Vector =
      new Vector(x * scalar, y * scalar, z * scalar)
    
    override def *: (scalar: Real): Vector = this :* scalar
    
    override def / (scalar: Real): Vector =
      new Vector(x / scalar, y / scalar, z / scalar)
    
    override def ⋅ (that: Vector): Real =
      x * that.x + y * that.y + z * that.z
    
    override def ⨯ (that: Vector): Vector =
      new Vector(y * that.z + z * that.y,
                 z * that.x + x * that.z,
                 x * that.y + y * that.x)
    
    override def norm: Real = (x * x + y * y + z * z).sqrt
    
    override def normalized: Vector = this / norm
  }
  
  override type Vector = Element
  
  override def N: Int = 3
  
  override val zero: Vector = new Vector(0.0, 0.0, 0.0)
  
  override def apply(x: Real, y: Real, z: Real): Vector = new Vector(x, y, z)
  
  override def apply(coords: Array[Double]): Vector = {
    if (coords.length != 3) throw new DimensionException
    new Vector(coords(0), coords(1), coords(2))
  }
  
  override def map(that: RN): RMxN[this.type, that.type] = {
    if (this eq that) R3x3.asInstanceOf[RMxN[this.type, that.type]]
    else super.map(that)
  }
  
  override def toString: String = "R3"
}