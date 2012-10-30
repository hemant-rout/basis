/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections
package sequential

/** Strictly evaluated container operations.
  * 
  * @groupprio  Mapping     -3
  * @groupprio  Filtering   -2
  * @groupprio  Combining   -1
  */
abstract class EagerContainerOps[+Self, +A] private[sequential] {
  /** Returns the applications of a partial function to each element in this
    * container for which the function is defined.
    * 
    * @param  q       the partial function to filter elements against and to
    *                 apply to applicable elements.
    * @param  buffer  the implicit accumulator for mapped elements.
    * @return the accumulated elements filtered and mapped by `q`.
    * @group  Mapping
    */
  def collect[B](q: PartialFunction[A, B])(implicit buffer: Buffer[Self, B]): buffer.State =
    macro EagerContainerOps.collect[A, B]
  
  /** Returns the applications of a function to each element in this container.
    * 
    * @param  f       the function to apply to each element.
    * @param  buffer  the implicit accumulator for mapped elements.
    * @return the accumulated elements mapped by `f`.
    * @group  Mapping
    */
  def map[B](f: A => B)(implicit buffer: Buffer[Self, B]): buffer.State =
    macro EagerContainerOps.map[A, B]
  
  /** Returns the concatenation of all elements returned by a function applied
    * to each element in this container.
    * 
    * @param  f       the enumerator-yielding function to apply to each element.
    * @param  buffer  the implicit accumulator for flattened elements.
    * @return the concatenation of all accumulated elements produced by `f`.
    * @group  Mapping
    */
  def flatMap[B](f: A => Enumerator[B])(implicit buffer: Buffer[Self, B]): buffer.State =
    macro EagerContainerOps.flatMap[A, B]
  
  /** Returns all elements in this container that satisfy a predicate.
    * 
    * @param  p       the predicate to test elements against.
    * @param  buffer  the implicit accumulator for filtered elements.
    * @return the accumulated elements filtered by `p`.
    * @group  Filtering
    */
  def filter(p: A => Boolean)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.filter[A]
  
  /** Returns all elements following the longest prefix of this container
    * for which each element satisfies a predicate.
    * 
    * @param  p       the predicate to test elements against.
    * @param  buffer  the implicit accumulator for non-dropped elements.
    * @return the suffix of accumulated elements beginning with the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def dropWhile(p: A => Boolean)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.dropWhile[A]
  
  /** Returns the longest prefix of this container for which each element
    * satisfies a predicate.
    * 
    * @param  p       the predicate to test elements against.
    * @param  buffer  the implicit accumulator for taken elements.
    * @return the longest prefix of accumulated elements preceding the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def takeWhile(p: A => Boolean)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.takeWhile[A]
  
  /** Returns a (prefix, suffix) pair with the prefix being the longest one for
    * which each element satisfies a predicate, and the suffix beginning with
    * the first element to not satisfy the predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  bufferA   the implicit accumulator for prefix elements.
    * @param  bufferB   the implicit accumilator for suffix elements.
    * @return the pair of accumulated prefix and suffix elements.
    * @group  Filtering
    */
  //FIXME: SI-6447
  //def span(p: A => Boolean)(
  //    implicit builderA: Buffer[Self, A],
  //             builderB: Buffer[Self, A])
  //  : (builderA.State, builderB.State) =
  //  macro EagerContainerOps.span[A]
  
  /** Returns all elements in this container following a prefix up to some length.
    * 
    * @param  lower   the length of the prefix to drop;
    *                 also the inclusive lower bound for indexes of elements to keep.
    * @param  buffer  the accumulator for non-dropped elements.
    * @return all but the first `lower` accumulated elements.
    * @group  Filtering
    */
  def drop(lower: Int)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.drop[A]
  
  /** Returns a prefix of this container up to some length.
    * 
    * @param  upper   the length of the prefix to take;
    *                 also the exclusive upper bound for indexes of elements to keep.
    * @param  buffer  the accumulator for taken elements.
    * @return up to the first `upper` accumulated elements.
    * @group  Filtering
    */
  def take(upper: Int)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.take[A]
  
  /** Returns an interval of elements in this container.
    * 
    * @param  lower   the inclusive lower bound for indexes of elements to keep.
    * @param  upper   the exclusive upper bound for indexes of elements to keep.
    * @param  buffer  the accumulator for kept elements.
    * @return the accumulated elements with indexes greater than or equal to
    *         `lower` and less than `upper`.
    * @group  Filtering
    */
  def slice(lower: Int, upper: Int)(implicit buffer: Buffer[Self, A]): buffer.State =
    macro EagerContainerOps.slice[A]
  
  /** Returns pairs of elements from this and another container.
    * 
    * @param  that    the container whose elements to pair with these elements.
    * @param  buffer  the accumulator for paired elements.
    * @return the accumulated pairs of corresponding elements.
    * @group  Combining
    */
  def zip[B](that: Container[B])(implicit buffer: Buffer[Self, (A, B)]): buffer.State =
    macro EagerContainerOps.zip[A, B]
  
  /** Returns the concatenation of this and another container.
    * 
    * @param  that    the container to append to this container.
    * @param  buffer  the implicit accumulator for concatenated elements.
    * @return the accumulated elements of both containers.
    * @group  Combining
    */
  def ++ [B >: A](that: Container[B])(implicit buffer: Buffer[Self, B]): buffer.State =
    macro EagerContainerOps.++[A, B]
}

private[sequential] object EagerContainerOps {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  private def unApplyIterator[A : c.WeakTypeTag](c: Context): c.Expr[Iterator[A]] = {
    import c.universe._
    val Apply(_, container :: Nil) = c.prefix.tree
    c.Expr(Select(container, "iterator"))(IteratorTag[A](c))
  }
  
  def collect[A : c.WeakTypeTag, B]
      (c: Context)
      (q: c.Expr[PartialFunction[A, B]])
      (buffer: c.Expr[Buffer[_, B]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).collect[A, B](unApplyIterator(c))(q)(buffer)
  
  def map[A : c.WeakTypeTag, B]
      (c: Context)
      (f: c.Expr[A => B])
      (buffer: c.Expr[Buffer[_, B]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).map[A, B](unApplyIterator(c))(f)(buffer)
  
  def flatMap[A : c.WeakTypeTag, B]
      (c: Context)
      (f: c.Expr[A => Enumerator[B]])
      (buffer: c.Expr[Buffer[_, B]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).flatMap[A, B](unApplyIterator(c))(f)(buffer)
  
  def filter[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).filter[A](unApplyIterator(c))(p)(buffer)
  
  def dropWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).dropWhile[A](unApplyIterator(c))(p)(buffer)
  
  def takeWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).takeWhile[A](unApplyIterator(c))(p)(buffer)
  
  def span[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (bufferA: c.Expr[Buffer[_, A]], bufferB: c.Expr[Buffer[_, A]])
    : c.Expr[(bufferA.value.State, bufferB.value.State)] =
    new IteratorMacros[c.type](c).span[A](unApplyIterator(c))(p)(bufferA, bufferB)
  
  def drop[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).drop[A](unApplyIterator(c))(lower)(buffer)
  
  def take[A : c.WeakTypeTag]
      (c: Context)
      (upper: c.Expr[Int])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).take[A](unApplyIterator(c))(upper)(buffer)
  
  def slice[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int], upper: c.Expr[Int])
      (buffer: c.Expr[Buffer[_, A]])
    : c.Expr[buffer.value.State] =
    new IteratorMacros[c.type](c).slice[A](unApplyIterator(c))(lower, upper)(buffer)
  
  def zip[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (that: c.Expr[Container[B]])
      (buffer: c.Expr[Buffer[_, (A, B)]])
    : c.Expr[buffer.value.State] = {
    val these = unApplyIterator[A](c)
    val those = c.Expr(c.universe.Select(that.tree, "iterator"))(IteratorTag[B](c))
    new IteratorMacros[c.type](c).zip[A, B](these, those)(buffer)
  }
  
  def ++ [A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (that: c.Expr[Container[B]])
      (buffer: c.Expr[Buffer[_, B]])
    : c.Expr[buffer.value.State] = {
    val these = unApplyIterator[A](c)
    val those = c.Expr(c.universe.Select(that.tree, "iterator"))(IteratorTag[B](c))
    new IteratorMacros[c.type](c).++[B](these, those)(buffer)
  }
  
  private def IteratorTag[A : c.WeakTypeTag](c: Context): c.WeakTypeTag[Iterator[A]] = {
    import c.universe._
    c.WeakTypeTag(
      appliedType(
        c.mirror.staticClass("basis.collections.Iterator").toType,
        weakTypeOf[A] :: Nil))
  }
}
