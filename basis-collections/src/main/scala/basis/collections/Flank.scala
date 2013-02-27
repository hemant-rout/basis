/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.collections

/** A two-sided sequence. A flank efficiently decomposes into its `head`
  * element and `tail` flank, and its `foot` element and `body` flank.
  * 
  * ==Extensions==
  * $Extensions
  * $SequentialOps
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.1
  * @group    Collections
  * 
  * @groupprio  Measuring     1
  * @groupprio  Decomposing   2
  * @groupprio  Traversing    3
  * @groupprio  Classifying   4
  * 
  * @define collection  flank
  */
trait Flank[@specialized(Int, Long, Float, Double, Boolean) +A]
  extends Any with Equals with Family[Flank[_]] with Side[A] {
  
  override def head: A
  
  override def tail: Flank[A]
  
  /** Returns all elements except the last of this non-empty $collection.
    * @group Decomposing */
  def body: Flank[A]
  
  /** Returns the last element of this non-empty $collection.
    * @group Decomposing */
  def foot: A
}