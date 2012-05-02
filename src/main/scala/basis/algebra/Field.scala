/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait Field extends Any with Ring {
  override type Vector
  
  override def + (that: Vector): Vector
  
  override def unary_- : Vector
  
  override def - (that: Vector): Vector
  
  override def * (that: Vector): Vector
  
  def inverse: Vector
  
  def / (that: Vector): Vector
}

object Field {
  trait Space extends Ring.Space { self =>
    override type Vector <: Field {
      type Vector = self.Vector
    }
    
    override def zero: Vector
    
    override def unit: Vector
  }
  
  trait Scalar extends Ring.Scalar { self =>
    override type Scalar <: Field {
      type Vector = self.Scalar
    }
    
    override def Scalar: Field.Space {
      type Vector = self.Scalar
    }
  }
}
