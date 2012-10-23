/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections

/** Operations available to all collections.
  * 
  * @groupprio  Traversing    -3
  * @groupprio  Reducing      -2
  * @groupprio  Querying      -1
  */
class CollectionOps[+A](val __ : Collection[A]) extends AnyVal {
  /** Sequentially applies a function to each element of this collection.
    * 
    * @param  f   the function to apply to each element.
    * @group  Traversing
    */
  def foreach[U](f: A => U): Unit = Enumerator.traverse(__)(f)
  
  /** Returns the repeated application of an associative binary operator
    * between an identity value and all elements of this collection.
    * 
    * @param  z   the operator's identity element.
    * @param  op  the associative binary operator to apply.
    * @return the folded value.
    * @group  Reducing
    */
  def fold[B >: A](z: B)(op: (B, B) => B): B =
    new EnumeratorOps[B](__).fold[B](z)(op)
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this non-empty collection.
    * 
    * @param  op  the associative binary operator to apply.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduce[B >: A](op: (B, B) => B): B =
    new EnumeratorOps[B](__).reduce[B](op)
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this collection.
    * 
    * @param  op  the associative binary operator to apply.
    * @return some reduced value, or none if this collection is empty.
    * @group  Reducing
    */
  def reduceOption[B >: A](op: (B, B) => B): Option[B] =
    new EnumeratorOps[B](__).reduceOption[B](op)
  
  /** Returns the left-to-right application of a binary operator between a
    * start value and all elements of this collection.
    * 
    * @param  z   the starting value.
    * @param  op  the binary operator to apply right-recursively.
    * @return the folded value.
    * @group  Reducing
    */
  def foldLeft[B](z: B)(op: (B, A) => B): B =
    new EnumeratorOps[A](__).foldLeft[B](z)(op)
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this non-empty collection.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduceLeft[B >: A](op: (B, A) => B): B =
    new EnumeratorOps[B](__).reduceLeft[B](op.asInstanceOf[(B, B) => B]) // work around typer bug
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this collection.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return some reduced value, or none if this collection is empty.
    * @group  Reducing
    */
  def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] =
    new EnumeratorOps[B](__).reduceLeftOption[B](op.asInstanceOf[(B, B) => B]) // work around typer bug
  
  /** Returns the first element of this collection that satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return some found element, or none if no element satisfies `p`.
    * @group  Querying
    */
  def find(p: A => Boolean): Option[A] =
    new EnumeratorOps[A](__).find(p)
  
  /** Returns `true` if a predicate holds for all elements of this collection.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if all elements satisfy `p`, else `false`.
    * @group  Querying
    */
  def forall(p: A => Boolean): Boolean =
    new EnumeratorOps[A](__).forall(p)
  
  /** Returns `true` if a predicate holds for some element of this collection.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if any element satisfies `p`, else `false`.
    * @group  Querying
    */
  def exists(p: A => Boolean): Boolean =
    new EnumeratorOps[A](__).exists(p)
  
  /** Returns the number of elements in this collection that satisfy a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the number of elements satisfying `p`.
    * @group  Querying
    */
  def count(p: A => Boolean): Int =
    new EnumeratorOps[A](__).count(p)
  
  /** Returns the application of a partial function to the first element
    * of this collection for which the function is defined.
    * 
    * @param  q   the partial function to test elements against and to apply
    *             to the first found element.
    * @return some found and mapped element, or none if no element applies to `q`.
    * @group  Querying
    */
  def select[B](q: PartialFunction[A, B]): Option[B] =
    new EnumeratorOps[A](__).select[B](q)
}
