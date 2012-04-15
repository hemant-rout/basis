/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait Field extends Ring { self =>
  override type Space <: ScalarSpace with Singleton {
    type Scalar = self.Scalar
  }
  
  override type Scalar >: self.type <: Field {
    type Scalar = self.Scalar
  }
  
  def + (that: Scalar): Scalar
  
  def unary_- : Scalar
  
  def - (that: Scalar): Scalar
  
  def * (that: Scalar): Scalar
  
  def inverse: Scalar
  
  def / (that: Scalar): Scalar
}
