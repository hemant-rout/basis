/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.sequential

import basis.control._

/** General array operations.
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.1
  * @group    General
  * 
  * @groupprio  Quantifying   1
  * @groupprio  Traversing    2
  * @groupprio  Reducing      3
  * @groupprio  Querying      4
  * @groupprio  Transforming  5
  * 
  * @define collection  array
  */
final class GeneralArrayOps[A](these: Array[A]) {
  /** Returns `true` if this $collection doesn't contain any elements.
    * @group Quantifying */
  def isEmpty: Boolean =
    macro GeneralArrayOps.isEmpty[A]
  
  /** Sequentially applies a function to each element of this $collection.
    * 
    * @param  f   the function to apply to each element.
    * @group  Traversing
    */
  def foreach[U](f: A => U): Unit =
    macro GeneralArrayOps.foreach[A, U]
  
  /** Returns the repeated application of an associative binary operator
    * between an identity value and all elements of this $collection.
    * 
    * @param  z   the operator's identity element.
    * @param  op  the associative binary operator to apply.
    * @return the folded value.
    * @group  Reducing
    */
  def fold[B >: A](z: B)(op: (B, B) => B): B =
    macro GeneralArrayOps.foldLeft[A, B]
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this non-empty $collection.
    * 
    * @param  op  the associative binary operator to apply.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduce[B >: A](op: (B, B) => B): B =
    macro GeneralArrayOps.reduceLeft[A, B]
  
  /** Returns the repeated application of an associative binary operator
    * between all elements of this $collection.
    * 
    * @param  op  the associative binary operator to apply.
    * @return the free reduced value, or a trap if this $collection is empty.
    * @group  Reducing
    */
  def mayReduce[B >: A](op: (B, B) => B): Maybe[B] =
    macro GeneralArrayOps.mayReduceLeft[A, B]
  
  /** Returns the left-to-right application of a binary operator between a
    * start value and all elements of this $collection.
    * 
    * @param  z   the starting value.
    * @param  op  the binary operator to apply right-recursively.
    * @return the folded value.
    * @group  Reducing
    */
  def foldLeft[B](z: B)(op: (B, A) => B): B =
    macro GeneralArrayOps.foldLeft[A, B]
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this non-empty $collection.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduceLeft[B >: A](op: (B, A) => B): B =
    macro GeneralArrayOps.reduceLeft[A, B]
  
  /** Returns the left-to-right application of a binary operator between
    * all elements of this $collection.
    * 
    * @param  op  the binary operator to apply right-recursively.
    * @return the free reduced value, or a trap if this $collection is empty.
    * @group  Reducing
    */
  def mayReduceLeft[B >: A](op: (B, A) => B): Maybe[B] =
    macro GeneralArrayOps.mayReduceLeft[A, B]
  
  /** Returns the right-to-left application of a binary operator between a
    * start value and all elements in this $collection.
    * 
    * @param  z   the starting value.
    * @param  op  the binary operator to apply left-recursively.
    * @return the folded value.
    * @group  Reducing
    */
  def foldRight[B](z: B)(op: (A, B) => B): B =
    macro GeneralArrayOps.foldRight[A, B]
  
  /** Returns the right-to-left application of a binary operator between
    * all elements in this non-empty $collection.
    * 
    * @param  op  the binary operator to apply left-recursively.
    * @return the reduced value.
    * @group  Reducing
    */
  def reduceRight[B >: A](op: (A, B) => B): B =
    macro GeneralArrayOps.reduceRight[A, B]
  
  /** Returns the right-to-left application of a binary operator between
    * all elements in this $collection.
    * 
    * @param  op  the binary operator to apply left-recursively.
    * @return the free reduced value, or a trap if this $collection is empty.
    * @group  Reducing
    */
  def mayReduceRight[B >: A](op: (A, B) => B): Maybe[B] =
    macro GeneralArrayOps.mayReduceRight[A, B]
  
  /** Returns the first element of this $collection that satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the free found element, or a trap if no element satisfies `p`.
    * @group  Querying
    */
  def find(p: A => Boolean): Maybe[A] =
    macro GeneralArrayOps.find[A]
  
  /** Returns `true` if a predicate holds for all elements of this $collection.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if all elements satisfy `p`, otherwise `false`.
    * @group  Querying
    */
  def forall(p: A => Boolean): Boolean =
    macro GeneralArrayOps.forall[A]
  
  /** Returns `true` if a predicate holds for some element of this $collection.
    * 
    * @param  p   the predicate to test elements against.
    * @return `true` if any element satisfies `p`, otherwise `false`.
    * @group  Querying
    */
  def exists(p: A => Boolean): Boolean =
    macro GeneralArrayOps.exists[A]
  
  /** Returns the number of elements in this $collection that satisfy a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the number of elements satisfying `p`.
    * @group  Querying
    */
  def count(p: A => Boolean): Int =
    macro GeneralArrayOps.count[A]
  
  /** Returns the application of a partial function to the first element
    * of this $collection for which the function is defined.
    * 
    * @param  q   the partial function to test elements against and to apply
    *             to the first found element.
    * @return the free found and mapped element, or a trap if no element applies to `q`.
    * @group  Querying
    */
  def choose[B](q: PartialFunction[A, B]): Maybe[B] =
    macro GeneralArrayOps.choose[A, B]
  
  /** Returns a strict operations interface to this $collection.
    * @group Transforming */
  def eagerly: StrictArrayOps[A, Array[A]] =
    macro GeneralArrayOps.eagerly[A]
  
  /** Returns a non-strict operations interface to this $collection.
    * @group Transforming */
  def lazily: NonStrictArrayOps[A] =
    macro GeneralArrayOps.lazily[A]
}

private[sequential] object GeneralArrayOps {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  private def unApply[A : c.WeakTypeTag](c: Context): c.Expr[Array[A]] = {
    import c.{Expr, mirror, prefix, typeCheck, weakTypeOf, WeakTypeTag}
    import c.universe._
    val Apply(_, array :: Nil) = prefix.tree
    val ArrayType = appliedType(mirror.staticClass("scala.Array").toType, weakTypeOf[A] :: Nil)
    Expr(typeCheck(array, ArrayType))(WeakTypeTag(ArrayType))
  }
  
  def isEmpty[A : c.WeakTypeTag](c: Context): c.Expr[Boolean] =
    new ArrayMacros[c.type](c).isEmpty[A](unApply[A](c))
  
  def foreach[A : c.WeakTypeTag, U : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => U])
    : c.Expr[Unit] =
    new ArrayMacros[c.type](c).foreach[A, U](unApply[A](c))(f)
  
  def foldLeft[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (z: c.Expr[B])
      (op: c.Expr[(B, A) => B])
    : c.Expr[B] =
    new ArrayMacros[c.type](c).foldLeft[A, B](unApply[A](c))(z)(op)
  
  def reduceLeft[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(B, A) => B])
    : c.Expr[B] =
    new ArrayMacros[c.type](c).reduceLeft[A, B](unApply[A](c))(op)
  
  def mayReduceLeft[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(B, A) => B])
    : c.Expr[Maybe[B]] =
    new ArrayMacros[c.type](c).mayReduceLeft[A, B](unApply[A](c))(op)
  
  def foldRight[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (z: c.Expr[B])
      (op: c.Expr[(A, B) => B])
    : c.Expr[B] =
    new ArrayMacros[c.type](c).foldRight[A, B](unApply[A](c))(z)(op)
  
  def reduceRight[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(A, B) => B])
    : c.Expr[B] =
    new ArrayMacros[c.type](c).reduceRight[A, B](unApply[A](c))(op)
  
  def mayReduceRight[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(A, B) => B])
    : c.Expr[Maybe[B]] =
    new ArrayMacros[c.type](c).mayReduceRight[A, B](unApply[A](c))(op)
  
  def find[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Maybe[A]] =
    new ArrayMacros[c.type](c).find[A](unApply[A](c))(p)
  
  def forall[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Boolean] =
    new ArrayMacros[c.type](c).forall[A](unApply[A](c))(p)
  
  def exists[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Boolean] =
    new ArrayMacros[c.type](c).exists[A](unApply[A](c))(p)
  
  def count[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Int] =
    new ArrayMacros[c.type](c).count[A](unApply[A](c))(p)
  
  def choose[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (q: c.Expr[PartialFunction[A, B]])
    : c.Expr[Maybe[B]] =
    new ArrayMacros[c.type](c).choose[A, B](unApply[A](c))(q)
  
  def eagerly[A : c.WeakTypeTag](c: Context): c.Expr[StrictArrayOps[A, Array[A]]] =
    Strict.StrictArrayOps[A](c)(unApply[A](c))
  
  def lazily[A : c.WeakTypeTag](c: Context): c.Expr[NonStrictArrayOps[A]] =
    NonStrict.NonStrictArrayOps[A](c)(unApply[A](c))
}