/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.sequential

import basis.collections._

/** General sequence operations.
  * 
  * @groupprio  Traversing    -4
  * @groupprio  Reducing      -3
  * @groupprio  Querying      -2
  * @groupprio  Transforming  -1
  */
final class GeneralSeqOps[+A](these: Seq[A]) {
  /** Sequentially applies a function to each element of this sequence.
    * 
    * @param  f   the function to apply to each element.
    * @group  Traversing
    */
  def foreach[U](f: A => U): Unit =
    macro GeneralContainerOps.foreach[A, U]
  
  /** Returns the repeated application of an associative binary operator
    * between an identity value and all elements of this sequence.
    * 
    * @param  z   the operator's identity element.
    * @param  op  the associative binary operator to apply.
    * @return the folded value.
    * @group  Reducing
    */
  def fold[B >: A](z: B)(op: (B, B) => B): B =
    macro GeneralContainerOps.foldLeft[A, B]
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this non-empty sequence.
    * 
    * @param  op  the associative binary operator to apply.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduce[B >: A](op: (B, B) => B): B =
    macro GeneralContainerOps.reduceLeft[A, B]
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this sequence.
    * 
    * @param  op  the associative binary operator to apply.
    * @return some reduced value, or none if this sequence is empty.
    * @group  Reducing
    */
  def reduceOption[B >: A](op: (B, B) => B): Option[B] =
    macro GeneralContainerOps.reduceLeftOption[A, B]
  
  /** Returns the left-to-right application of a binary operator between a
    * start value and all elements of this sequence.
    * 
    * @param  z   the starting value.
    * @param  op  the binary operator to apply right-recursively.
    * @return the folded value.
    * @group  Reducing
    */
  def foldLeft[B](z: B)(op: (B, A) => B): B =
    macro GeneralContainerOps.foldLeft[A, B]
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this non-empty sequence.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduceLeft[B >: A](op: (B, A) => B): B =
    macro GeneralContainerOps.reduceLeft[A, B]
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this sequence.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return some reduced value, or none if this sequence is empty.
    * @group  Reducing
    */
  def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] =
    macro GeneralContainerOps.reduceLeftOption[A, B]
  
  /** Returns the first element of this sequence that satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return some found element, or none if no element satisfies `p`.
    * @group  Querying
    */
  def find(p: A => Boolean): Option[A] =
    macro GeneralContainerOps.find[A]
  
  /** Returns `true` if a predicate holds for all elements of this sequence.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if all elements satisfy `p`, else `false`.
    * @group  Querying
    */
  def forall(p: A => Boolean): Boolean =
    macro GeneralContainerOps.forall[A]
  
  /** Returns `true` if a predicate holds for some element of this sequence.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if any element satisfies `p`, else `false`.
    * @group  Querying
    */
  def exists(p: A => Boolean): Boolean =
    macro GeneralContainerOps.exists[A]
  
  /** Returns the number of elements in this sequence that satisfy a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the number of elements satisfying `p`.
    * @group  Querying
    */
  def count(p: A => Boolean): Int =
    macro GeneralContainerOps.count[A]
  
  /** Returns the application of a partial function to the first element
    * of this sequence for which the function is defined.
    * 
    * @param  q   the partial function to test elements against and to apply
    *             to the first found element.
    * @return some found and mapped element, or none if no element applies to `q`.
    * @group  Querying
    */
  def choose[B](q: PartialFunction[A, B]): Option[B] =
    macro GeneralContainerOps.choose[A, B]
  
  /** Returns a strict operations interface to this sequence.
    * @group Transforming */
  def eagerly: StrictSeqOps[A, Seq[A]] =
    macro GeneralSeqOps.eagerly[A]
  
  /** Returns a non-strict operations interface to this sequence.
    * @group Transforming */
  def lazily: NonStrictSeqOps[A] =
    macro GeneralSeqOps.lazily[A]
}

private[sequential] object GeneralSeqOps {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  private def unApply[A : c.WeakTypeTag](c: Context): c.Expr[Seq[A]] = {
    import c.{Expr, mirror, prefix, typeCheck, weakTypeOf, WeakTypeTag}
    import c.universe._
    val Apply(_, sequence :: Nil) = prefix.tree
    val SeqType =
      appliedType(
        mirror.staticClass("basis.collections.Seq").toType,
        weakTypeOf[A] :: Nil)
    Expr(typeCheck(sequence, SeqType))(WeakTypeTag(SeqType))
  }
  
  def eagerly[A : c.WeakTypeTag](c: Context): c.Expr[StrictSeqOps[A, Seq[A]]] =
    Strict.StrictSeqOps[A](c)(unApply[A](c))
  
  def lazily[A : c.WeakTypeTag](c: Context): c.Expr[NonStrictSeqOps[A]] =
    NonStrict.NonStrictSeqOps[A](c)(unApply[A](c))
}