/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

import basis.memory._
import basis.util.MurmurHash._

/** An approximation of a real number by a `Double` value.
  * 
  * @author Chris Sachs
  * 
  * @constructor  Creates a `Real` value from a `Double` value.
  * @param  value   the `Double` value.
  * 
  * @define Element   Real
  * @define element   `Real` value
  */
final class Real(private val value: Double)
  extends EuclideanVector[Real, Real]
    with OrderedRing[Real]
    with CompleteField[Real] {
  
  def + (that: Real): Real = new Real(value + that.value)
  
  def + (x: Double): Real = new Real(value + x)
  
  def unary_- : Real = new Real(-value)
  
  def - (that: Real): Real = new Real(value - that.value)
  
  def - (x: Double): Real = new Real(value - x)
  
  def * (that: Real): Real = new Real(value * that.value)
  
  def * (x: Double): Real = new Real(value * x)
  
  def reciprocal: Real = new Real(1.0 / value)
  
  def / (that: Real): Real = new Real(value / that.value)
  
  def / (x: Double): Real = new Real(value / x)
  
  def ⋅ (that: Real): Real = new Real(value * that.value)
  
  def norm: Real = this
  
  def pow(that: Real): Real = new Real(math.pow(value, that.value))
  
  def pow(x: Double): Real = new Real(math.pow(value, x))
  
  def sqrt: Real = new Real(math.sqrt(value))
  
  def abs: Real = if (value >= 0.0) this else -this
  
  def min(that: Real): Real = if (value <= that.value) this else that
  
  def max(that: Real): Real = if (value >= that.value) this else that
  
  def < (that: Real): Boolean = value < that.value
  
  def <= (that: Real): Boolean = value <= that.value
  
  def >= (that: Real): Boolean = value >= that.value
  
  def > (that: Real): Boolean = value > that.value
  
  override def equals(other: Any): Boolean = other match {
    case that: Real => value == that.value
    case _ => false
  }
  
  override def hashCode: Int = hash(value)
  
  override def toString: String = value.toString
  
  /** Converts this `Real` value to an `Int` value. */
  def toInt: Int = value.toInt
  
  /** Converts this `Real` value to a `Long` value. */
  def toLong: Long = value.toLong
  
  /** Converts this `Real` value to a `Float` value. */
  def toFloat: Float = value.toFloat
  
  /** Returns the `Double` value of this `Real` value. */
  def toDouble: Double = value
}

/** Contains factory methods and serves as a struct for `Real` values. */
object Real extends Struct1[Double, Real] {
  /** The additive identity of `Real`. */
  val Zero: Real = new Real(0.0)
  
  /** The multiplicative identity of `Real`. */
  val One: Real = new Real(1.0)
  
  def apply(value: Double): Real = new Real(value)
  
  def unapply(real: Real): Some[Double] = Some(real.value)
  
  def load(data: Data, address: Long): Real =
    new Real(data.loadDouble(address))
  
  def store(data: Data, address: Long, real: Real): Unit =
    data.storeDouble(address, real.value)
  
  implicit def struct: this.type = this
  
  override def toString: String = "Real"
}