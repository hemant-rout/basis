/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collection

trait Iterating[+Scope, +A] extends Any with Traversing[Scope, A] with Iterable[A] {
  override def iterator: Iterator[A]
  
  override def map[B](f: A => B)(implicit builder: Collector[Scope, B]): builder.Product = {
    val iter = iterator
    while (iter.hasNext) builder += f(iter.next())
    builder.result
  }
  
  override def flatMap[B](f: A => Incremental[B])(implicit builder: Collector[Scope, B]): builder.Product = {
    val iter = iterator
    while (iter.hasNext) builder ++= f(iter.next())
    builder.result
  }
  
  override def filter(p: A => Boolean)(implicit builder: Collector[Scope, A]): builder.Product = {
    val iter = iterator
    while (iter.hasNext) {
      val item = iter.next()
      if (p(item)) builder += item
    }
    builder.result
  }
  
  override def collect[B](q: PartialFunction[A, B])(implicit builder: Collector[Scope, B]): builder.Product = {
    val iter = iterator
    while (iter.hasNext) {
      val item = iter.next()
      if (q.isDefinedAt(item)) builder += q(item)
    }
    builder.result
  }
  
  override def drop(lower: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    val iter = iterator
    var i = 0
    while (i < lower && iter.hasNext) { iter.next(); i += 1 }
    if (i >= lower) while (iter.hasNext) builder += iter.next()
    builder.result
  }
  
  override def take(upper: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    val iter = iterator
    var i = 0
    while (i < upper && iter.hasNext) { builder += iter.next(); i += 1 }
    builder.result
  }
  
  override def slice(lower: Int, upper: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    val iter = iterator
    var i = 0
    while (i < lower && iter.hasNext) { iter.next(); i += 1 }
    while (i < upper && iter.hasNext) { builder += iter.next(); i += 1 }
    builder.result
  }
  
  override def eagerly: Iterating[Scope, A] = this
}

object Iterating {
  abstract class Abstractly[+Scope, +A] extends Iterable.Abstractly[A] with Iterating[Scope, A]
  
  final class Projecting[+Scope, +A](self: Iterable[A]) extends Abstractly[Scope, A] {
    override def iterator: Iterator[A] = self.iterator
  }
}
