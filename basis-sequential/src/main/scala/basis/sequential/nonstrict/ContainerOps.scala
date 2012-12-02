/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.sequential
package nonstrict

import basis.collections._
import basis.collections.traversable._

/** Non-strictly evaluated container operations.
  * 
  * @groupprio  Mapping     -3
  * @groupprio  Filtering   -2
  * @groupprio  Combining   -1
  */
final class ContainerOps[+A](val these: Container[A]) extends AnyVal {
  /** Returns a view that applies a partial function to each element in this
    * container for which the function is defined.
    * 
    * @param  q   the partial function to lazily filter and map elements.
    * @return a non-strict view of the filtered and mapped elements.
    * @group  Mapping
    */
  def collect[B](q: PartialFunction[A, B]): Container[B] =
    new ContainerOps.Collect(these, q)
  
  /** Returns a view that applies a function to each element in this container.
    * 
    * @param  f   the function to lazily apply to each element.
    * @return a non-strict view of the mapped elements.
    * @group  Mapping
    */
  def map[B](f: A => B): Container[B] =
    new ContainerOps.Map(these, f)
  
  /** Returns a view concatenating all elements returned by a function
    * applied to each element in this container.
    * 
    * @param  f   the container-yielding function to apply to each element.
    * @return a non-strict view concatenating all elements produced by `f`.
    * @group  Mapping
    */
  def flatMap[B](f: A => Container[B]): Container[B] =
    new ContainerOps.FlatMap(these, f)
  
  /** Returns a view of all elements in this container that satisfy a predicate.
    * 
    * @param  p   the predicate to lazily test elements against.
    * @return a non-strict view of the filtered elements.
    * @group  Filtering
    */
  def filter(p: A => Boolean): Container[A] =
    new ContainerOps.Filter(these, p)
  
  /** Returns a view of all elements following the longest prefix of this
    * container for which each element satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return a non-strict view of the suffix of accumulated elements beginning
    *         with the first element to not satisfy `p`.
    * @group  Filtering
    */
  def dropWhile(p: A => Boolean): Container[A] =
    new ContainerOps.DropWhile(these, p)
  
  /** Returns a view of the longest prefix of this container for which each
    * element satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return a non-strict view of the longest prefix of elements preceding
    *         the first element to not satisfy `p`.
    * @group  Filtering
    */
  def takeWhile(p: A => Boolean): Container[A] =
    new ContainerOps.TakeWhile(these, p)
  
  /** Returns a (prefix, suffix) pair of views with the prefix being the
    * longest one for which each element satisfies a predicate, and the suffix
    * beginning with the first element to not satisfy the predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the (predix, suffix) pair of non-strict views.
    * @group  Filtering
    */
  def span(p: A => Boolean): (Container[A], Container[A]) =
    (takeWhile(p), dropWhile(p))
  
  /** Returns a view of all elements in this container following a prefix
    * up to some length.
    * 
    * @param  lower   the length of the prefix to drop; also the inclusive
    *                 lower bound for indexes of included elements.
    * @return a non-strict view of all but the first `lower` elements.
    * @group  Filtering
    */
  def drop(lower: Int): Container[A] =
    new ContainerOps.Drop(these, lower)
  
  /** Returns a view of a prefix of this container up to some length.
    * 
    * @param  upper   the length of the prefix to take; also the exclusive
    *                 upper bound for indexes of included elements.
    * @return a non-strict view of up to the first `upper` elements.
    * @group  Filtering
    */
  def take(upper: Int): Container[A] =
    new ContainerOps.Take(these, upper)
  
  /** Returns a view of an interval of elements in this container.
    * 
    * @param  lower   the inclusive lower bound for indexes of included elements.
    * @param  upper   the exclusive upper bound for indexes of included elements.
    * @return a non-strict view of the elements with indexes greater than or
    *         equal to `lower` and less than `upper`.
    * @group  Filtering
    */
  def slice(lower: Int, upper: Int): Container[A] =
    new ContainerOps.Slice(these, lower, upper)
  
  /** Returns a view of pairs of elemnts from this and another container.
    * 
    * @param  those   the container whose elements to lazily pair with these elements.
    * @return a non-strict view of the pairs of corresponding elements.
    * @group  Combining
    */
  def zip[B](those: Container[B]): Container[(A, B)] =
    new ContainerOps.Zip(these, those)
  
  /** Returns a view concatenating this and another container.
    * 
    * @param  those   the elements to append to these elements.
    * @return a non-strict view of the concatenated elements.
    * @group Combining
    */
  def ++ [B >: A](those: Container[B]): Container[B] =
    new ContainerOps.++(these, those)
}

private[nonstrict] object ContainerOps {
  class Collect[-A, +B](
      protected[this] override val base: Container[A],
      protected[this] override val q: PartialFunction[A, B])
    extends CollectionOps.Collect[A, B](base, q) with Container[B] {
    
    override def iterator: Iterator[B] =
      new IteratorOps.Collect(base.iterator, q)
  }
  
  class Map[-A, +B](
      protected[this] override val base: Container[A],
      protected[this] override val f: A => B)
    extends CollectionOps.Map[A, B](base, f) with Container[B] {
    
    override def iterator: Iterator[B] =
      new IteratorOps.Map(base.iterator, f)
  }
  
  class FlatMap[-A, +B](
      protected[this] override val base: Container[A],
      protected[this] override val f: A => Container[B])
    extends CollectionOps.FlatMap[A, B](base, f) with Container[B] {
    
    override def iterator: Iterator[B] =
      new IteratorOps.FlatMapContainer(base.iterator, f)
  }
  
  class Filter[+A](
      protected[this] override val base: Container[A],
      protected[this] override val p: A => Boolean)
    extends CollectionOps.Filter[A](base, p) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.Filter(base.iterator, p)
  }
  
  class DropWhile[+A](
      protected[this] override val base: Container[A],
      protected[this] override val p: A => Boolean)
    extends CollectionOps.DropWhile[A](base, p) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.DropWhile(base.iterator, p)
  }
  
  class TakeWhile[+A](
      protected[this] override val base: Container[A],
      protected[this] override val p: A => Boolean)
    extends CollectionOps.TakeWhile[A](base, p) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.TakeWhile(base.iterator, p)
  }
  
  class Drop[+A](
      protected[this] override val base: Container[A],
      protected[this] override val lower: Int)
    extends CollectionOps.Drop[A](base, lower) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.Drop(base.iterator, lower)
  }
  
  class Take[+A](
      protected[this] override val base: Container[A],
      protected[this] override val upper: Int)
    extends CollectionOps.Take[A](base, upper) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.Take(base.iterator, upper)
  }
  
  class Slice[+A](
      protected[this] override val base: Container[A],
      protected[this] override val lower: Int,
      protected[this] override val upper: Int)
    extends CollectionOps.Slice[A](base, lower, upper) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.Slice(base.iterator, lower, upper)
  }
  
  class Zip[+A, +B](
      protected[this] val xs: Container[A],
      protected[this] val ys: Container[B])
    extends Container[(A, B)] {
    
    override def iterator: Iterator[(A, B)] =
      new IteratorOps.Zip(xs.iterator, ys.iterator)
  }
  
  class ++[+A](
      protected[this] override val xs: Container[A],
      protected[this] override val ys: Container[A])
    extends CollectionOps.++[A](xs, ys) with Container[A] {
    
    override def iterator: Iterator[A] =
      new IteratorOps.++(xs.iterator, ys.iterator)
  }
}
