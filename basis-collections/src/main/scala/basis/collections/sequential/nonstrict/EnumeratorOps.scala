/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections
package sequential
package nonstrict

/** Non-strictly evaluated enumerator operations.
  * 
  * @groupprio  Traversing  -6
  * @groupprio  Reducing    -5
  * @groupprio  Querying    -4
  * @groupprio  Mapping     -3
  * @groupprio  Filtering   -2
  * @groupprio  Combining   -1
  * 
  * @define collection  enumerator
  */
trait EnumeratorOps[+A, +From] extends Any with general.EnumeratorOps[A, From] {
  protected[this] def self: Enumerator[A]
  
  /** Returns a view that applies a partial function to each element in this
    * $collection for which the function is defined.
    * 
    * @param  q   the partial function to lazily filter and map elements.
    * @return a non-strict view of the filtered and mapped elements.
    * @group  Mapping
    */
  def collect[B](q: PartialFunction[A, B]): Enumerator[B] =
    new EnumeratorView.Collect(self, q)
  
  /** Returns a view that applies a function to each element in this $collection.
    * 
    * @param  f   the function to lazily apply to each element.
    * @return a non-strict view of the mapped elements.
    * @group  Mapping
    */
  def map[B](f: A => B): Enumerator[B] =
    new EnumeratorView.Map(self, f)
  
  /** Returns a view of all elements in this $collection that satisfy a predicate.
    * 
    * @param  p   the predicate to lazily test elements against.
    * @return a non-strict view of the filtered elements.
    * @group  Filtering
    */
  def filter(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.Filter(self, p)
  
  /** Returns a view of all elements following the longest prefix of this
    * $collection for which each element satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return a non-strict view of the suffix of accumulated elements beginning
    *         with the first element to not satisfy `p`.
    * @group  Filtering
    */
  def dropWhile(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.DropWhile(self, p)
  
  /** Returns a view of the longest prefix of this $collection for which each
    * element satisfies a predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return a non-strict view of the longest prefix of elements preceding
    *         the first element to not satisfy `p`.
    * @group  Filtering
    */
  def takeWhile(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.TakeWhile(self, p)
  
  /** Returns a (prefix, suffix) pair of views with the prefix being the
    * longest one for which each element satisfies a predicate, and the suffix
    * beginning with the first element to not satisfy the predicate.
    * 
    * @param  p   the predicate to test elements against.
    * @return the (predix, suffix) pair of non-strict views.
    * @group  Filtering
    */
  def span(p: A => Boolean): (Enumerator[A], Enumerator[A]) =
    (takeWhile(p), dropWhile(p))
  
  /** Returns a view of all elements in this $collection following a prefix up to some length.
    * 
    * @param  lower   the length of the prefix to drop; also the inclusive
    *                 lower bound for indexes of included elements.
    * @return a non-strict view of all but the first `lower` elements.
    * @group  Filtering
    */
  def drop(lower: Int): Enumerator[A] =
    new EnumeratorView.Drop(self, lower)
  
  /** Returns a view of a prefix of this $collection up to some length.
    * 
    * @param  upper   the length of the prefix to take; also the exclusive
    *                 upper bound for indexes of included elements.
    * @return a non-strict view of up to the first `upper` elements.
    * @group  Filtering
    */
  def take(upper: Int): Enumerator[A] =
    new EnumeratorView.Take(self, upper)
  
  /** Returns an interval of elements in this $collection.
    * 
    * @param  lower     the inclusive lower bound for indexes of elements to keep.
    * @param  upper     the exclusive upper bound for indexes of elements to keep.
    * @param  builder   the accumulator for kept elements.
    * @return the accumulated elements with indexes greater than or equal to
    *         `lower` and less than `upper`.
    * @group  Filtering
    */
  
  /** Returns a view of an interval of elements in this $collection.
    * 
    * @param  lower   the inclusive lower bound for indexes of included elements.
    * @param  upper   the exclusive upper bound for indexes of included elements.
    * @return a non-strict view of the elements with indexes greater than or
    *         equal to `lower` and less than `upper`.
    * @group  Filtering
    */
  def slice(lower: Int, upper: Int): Enumerator[A] =
    new EnumeratorView.Slice(self, lower, upper)
}

class EnumeratorView[+A, +From](override val self: Enumerator[A])
  extends AnyVal with EnumeratorOps[A, From] {
  
  override def collect[B](q: PartialFunction[A, B]): Enumerator[B] =
    new EnumeratorView.Collect(self, q)
  
  override def map[B](f: A => B): Enumerator[B] =
    new EnumeratorView.Map(self, f)
  
  /** Returns a view concatenating all elements returned by a function
    * applied to each element in this $collection.
    * 
    * @param  f   the $collection-yielding function to apply to each element.
    * @return a non-strict view concatenating all elements produced by `f`.
    * @group  Mapping
    */
  def flatMap[B](f: A => Enumerator[B]): Enumerator[B] =
    new EnumeratorView.FlatMap(self, f)
  
  override def filter(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.Filter(self, p)
  
  override def dropWhile(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.DropWhile(self, p)
  
  override def takeWhile(p: A => Boolean): Enumerator[A] =
    new EnumeratorView.TakeWhile(self, p)
  
  override def span(p: A => Boolean): (Enumerator[A], Enumerator[A]) =
    (takeWhile(p), dropWhile(p))
  
  override def drop(lower: Int): Enumerator[A] =
    new EnumeratorView.Drop(self, lower)
  
  override def take(upper: Int): Enumerator[A] =
    new EnumeratorView.Take(self, upper)
  
  override def slice(lower: Int, upper: Int): Enumerator[A] =
    new EnumeratorView.Slice(self, lower, upper)
  
  /** Returns a view concatenating this and another $collection.
    * 
    * @param  those   the elements to append to these elements.
    * @return a non-strict view of the concatenated elements.
    * @group Combining
    */
  def ++ [B >: A](those: Enumerator[B]): Enumerator[B] =
    new EnumeratorView.++(self, those)
}

private[nonstrict] object EnumeratorView {
  import basis.util.IntOps
  
  class Collect[-A, +B](
      protected[this] val base: Enumerator[A],
      protected[this] val q: PartialFunction[A, B])
    extends Enumerator[B] {
    
    protected override def foreach[U](f: B => U) {
      traverse(base)(x => if (q.isDefinedAt(x)) f(q(x)))
    }
  }
  
  class Map[-A, +B](
      protected[this] val base: Enumerator[A],
      protected[this] val f: A => B)
    extends Enumerator[B] {
    
    protected override def foreach[U](g: B => U) {
      traverse(base)(f andThen g)
    }
  }
  
  class FlatMap[-A, +B](
      protected[this] val base: Enumerator[A],
      protected[this] val f: A => Enumerator[B])
    extends Enumerator[B] {
    
    protected override def foreach[U](g: B => U) {
      traverse(base)(x => traverse(f(x))(g))
    }
  }
  
  class Filter[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val p: A => Boolean)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      traverse(base)(x => if (p(x)) f(x))
    }
  }
  
  class DropWhile[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val p: A => Boolean)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      var split = false
      traverse(base)(x => if (split || (!p(x) && { split = true; true })) f(x))
    }
  }
  
  class TakeWhile[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val p: A => Boolean)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      label(traverse(base)(x => if (p(x)) f(x) else label.break()))
    }
  }
  
  class Drop[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val lower: Int)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      var i = 0
      traverse(base)(x => if (i >= lower) f(x) else i += 1)
    }
  }
  
  class Take[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val upper: Int)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      var i = 0
      label(traverse(base)(x => if (i < upper) { f(x); i += 1 } else label.break()))
    }
  }
  
  class Slice[+A](
      protected[this] val base: Enumerator[A],
      protected[this] val lower: Int,
      protected[this] val upper: Int)
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      var l = 0 max lower
      val u = l max upper
      var i = 0
      if (l < u) label(traverse(base)(x => if (i < u) { if (i >= l) f(x); i += 1 } else label.break()))
    }
  }
  
  class ++[+A](
      protected[this] val xs: Enumerator[A],
      protected[this] val ys: Enumerator[A])
    extends Enumerator[A] {
    
    protected override def foreach[U](f: A => U) {
      traverse(xs)(f)
      traverse(ys)(f)
    }
  }
}
