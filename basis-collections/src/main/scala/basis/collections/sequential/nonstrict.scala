/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections
package sequential

/** Implicit conversions to extend collections with nonstrictly evaluated operations. */
object nonstrict {
  implicit def BasicEnumeratorOps[A](self: Enumerator[A]): BasicEnumeratorOps[A] =
    new BasicEnumeratorOps[A](self)
  
  implicit def BasicCollectionOps[A](self: Collection[A]): BasicCollectionOps[A] =
    new BasicCollectionOps[A](self)
  
  implicit def BasicIteratorOps[A](self: Iterator[A]): BasicIteratorOps[A] =
    throw new java.lang.UnsupportedOperationException("Can't instantiate macro interface at runtime.")
  
  implicit def BasicContainerOps[A](self: Container[A]): BasicContainerOps[A] =
    throw new java.lang.UnsupportedOperationException("Can't instantiate macro interface at runtime.")
  
  implicit def BasicSeqOps[A](self: Seq[A]): BasicSeqOps[A] =
    throw new java.lang.UnsupportedOperationException("Can't instantiate macro interface at runtime.")
  
  implicit def BasicSetOps[A](self: Set[A]): BasicSetOps[A] =
    throw new java.lang.UnsupportedOperationException("Can't instantiate macro interface at runtime.")
  
  implicit def BasicMapOps[A, T](self: Map[A, T]): BasicMapOps[A, T] =
    throw new java.lang.UnsupportedOperationException("Can't instantiate macro interface at runtime.")
  
  implicit def LazyEnumeratorOps[A](self: Enumerator[A]): LazyEnumeratorOps[A] =
    new LazyEnumeratorOps[A](self)
  
  implicit def LazyCollectionOps[A](self: Collection[A]): LazyCollectionOps[A] =
    new LazyCollectionOps[A](self)
  
  implicit def LazyIteratorOps[A](self: Iterator[A]): LazyIteratorOps[A] =
    new LazyIteratorOps[A](self)
  
  implicit def LazyContainerOps[A](self: Container[A]): LazyContainerOps[A] =
    new LazyContainerOps[A](self)
  
  implicit def LazySeqOps[A](self: Seq[A]): LazySeqOps[A] =
    new LazySeqOps[A](self)
  
  implicit def LazySetOps[A](self: Set[A]): LazySetOps[A] =
    new LazySetOps[A](self)
  
  implicit def LazyMapOps[A, T](self: Map[A, T]): LazyMapOps[A, T] =
    new LazyMapOps[A, T](self)
}