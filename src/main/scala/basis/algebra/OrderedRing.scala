/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait OrderedRing[G <: OrderedRing[G]] extends Ring[G] {
  def abs: G
  
  def min(that: G): G
  
  def max(that: G): G
  
  def < (that: G): Boolean
  
  def <= (that: G): Boolean
  
  def >= (that: G): Boolean
  
  def > (that: G): Boolean
}
