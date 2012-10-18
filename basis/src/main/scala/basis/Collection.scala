/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis

/** A traversable collection of elements. Collection declares only a protected
  * `foreach` method; it has no public methods. Import
  * [[basis.collection.CollectionOps]] to extend this interface with a full
  * suite of collection operations.
  * 
  * @author Chris Sachs
  * 
  * @define collection  collection
  */
trait Collection[+A] extends Any with Enumerator[A] {
  override type Self <: Collection[A]
  
  protected override def foreach[U](f: A => U): Unit
}

/** `Collection` type class implementations. */
object Collection {
  /* implicit */ def Show[A : Show]: Show[Collection[A]] =
    new CollectionShow[A]("Collection")
}

private[basis] class CollectionShow[-A]
    (stringPrefix: String)(implicit A: Show[A])
  extends Show[Collection[A]] {
  
  override def show(xs: Collection[A])(buffer: CharBuffer) {
    buffer.append(stringPrefix)
    buffer += '('
    Enumerator.traverse(xs)(new ShowEach[A](", "))
    buffer += ')'
  }
}

private[basis] final class ShowEach[-A]
    (separator: String)
    (implicit A: Show[A], buffer: CharBuffer)
  extends scala.runtime.AbstractFunction1[A, Unit] {
  
  private[this] var empty: Boolean = true
  
  override def apply(x: A) {
    if (!empty) buffer.append(separator) else empty = false
    A.show(x)(buffer)
  }
}
