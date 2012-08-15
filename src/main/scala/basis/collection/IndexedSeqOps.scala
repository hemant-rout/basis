/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collection

final class IndexedSeqOps[Kind, A](val __ : IndexedSeq[A]) extends AnyVal {
  import __.{length, apply}
  
  @inline def select[B](q: PartialFunction[A, B]): Option[B] = {
    var i = 0
    val until = length
    while (i < until) {
      val x = apply(i)
      if (q.isDefinedAt(x)) return Some(q(x))
      i += 1
    }
    None
  }
  
  @inline def fold[B >: A](z: B)(op: (B, B) => B): B = {
    var result = z
    var i = 0
    val until = length
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    result
  }
  
  @inline def reduce[B >: A](op: (B, B) => B): B = {
    val until = length
    if (until == 0) throw new UnsupportedOperationException
    var result: B = apply(0)
    var i = 1
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    result
  }
  
  @inline def reduceOption[B >: A](op: (B, B) => B): Option[B] = {
    val until = length
    if (until == 0) return None
    var result: B = apply(0)
    var i = 1
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    Some(result)
  }
  
  @inline def foldLeft[B](z: B)(op: (B, A) => B): B = {
    var result = z
    var i = 0
    val until = length
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    result
  }
  
  @inline def reduceLeft[B >: A](op: (B, A) => B): B = {
    val until = length
    if (until == 0) throw new UnsupportedOperationException
    var result: B = apply(0)
    var i = 1
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    result
  }
  
  @inline def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] = {
    val until = length
    if (until == 0) return None
    var result: B = apply(0)
    var i = 1
    while (i < until) {
      result = op(result, apply(i))
      i += 1
    }
    Some(result)
  }
  
  @inline def foldRight[B](z: B)(op: (A, B) => B): B = {
    var result = z
    var i = length - 1
    while (i >= 0) {
      result = op(apply(i), result)
      i -= 1
    }
    result
  }
  
  @inline def reduceRight[B >: A](op: (A, B) => B): B = {
    var i = length - 1
    if (i < 0) throw new UnsupportedOperationException
    var result: B = apply(i)
    i -= 1
    while (i >= 0) {
      result = op(apply(i), result)
      i -= 1
    }
    result
  }
  
  @inline def reduceRightOption[B >: A](op: (A, B) => B): Option[B] = {
    var i = length - 1
    if (i < 0) return None
    var result: B = apply(i)
    i -= 1
    while (i >= 0) {
      result = op(apply(i), result)
      i -= 1
    }
    Some(result)
  }
  
  @inline def find(p: A => Boolean): Option[A] = {
    var i = 0
    val until = length
    while (i < until) {
      val x = apply(i)
      if (p(x)) return Some(x)
      i += 1
    }
    None
  }
  
  @inline def forall(p: A => Boolean): Boolean = {
    var i = 0
    val until = length
    while (i < until) {
      if (!p(apply(i))) return false
      i += 1
    }
    true
  }
  
  @inline def exists(p: A => Boolean): Boolean = {
    var i = 0
    val until = length
    while (i < until) {
      if (p(apply(i))) return true
      i += 1
    }
    false
  }
  
  @inline def count(p: A => Boolean): Int = {
    var total = 0
    var i = 0
    val until = length
    while (i < until) {
      if (p(apply(i))) total += 1
      i += 1
    }
    total
  }
  
  @inline def map[B](f: A => B)(implicit builder: Builder[Kind, B]): builder.Result = {
    var i = 0
    val until = length
    builder.expect(until)
    while (i < until) {
      builder += f(apply(i))
      i += 1
    }
    builder.result
  }
  
  @inline def flatMap[B](f: A => IndexedSeq[B])(implicit builder: Builder[Kind, B]): builder.Result = {
    var i = 0
    val until = length
    while (i < until) {
      val these = f(apply(i))
      var j = 0
      val limit = these.length
      while (j < limit) {
        builder += these.apply(j)
        j += 1
      }
      i += 1
    }
    builder.result
  }
  
  @inline def filter(p: A => Boolean)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = 0
    val until = length
    while (i < until) {
      val x = apply(i)
      if (p(x)) builder += x
      i += 1
    }
    builder.result
  }
  
  @inline def collect[B](q: PartialFunction[A, B])(implicit builder: Builder[Kind, B]): builder.Result = {
    var i = 0
    val until = length
    while (i < until) {
      val x = apply(i)
      if (q.isDefinedAt(x)) builder += q(x)
      i += 1
    }
    builder.result
  }
  
  @inline def dropWhile(p: A => Boolean)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = 0
    var x = null.asInstanceOf[A]
    val until = length
    while (i < until && { x = apply(i); p(x) }) i += 1
    if (i < until) { builder += x; i += 1 }
    while (i < until) { builder += apply(i); i += 1 }
    builder.result
  }
  
  @inline def takeWhile(p: A => Boolean)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = 0
    var x = null.asInstanceOf[A]
    val until = length
    while (i < until && { x = apply(i); p(x) }) { builder += x; i += 1 }
    builder.result
  }
  
  @inline def span(p: A => Boolean)(
      implicit builderA: Builder[Kind, A],
               builderB: Builder[Kind, A]): (builderA.Result, builderB.Result) = {
    var i = 0
    var x = null.asInstanceOf[A]
    val until = length
    while (i < until && { x = apply(i); p(x) }) { builderA += x; i += 1 }
    if (i < until) { builderB += x; i += 1 }
    while (i < until) { builderB += apply(i); i += 1 }
    (builderA.result, builderB.result)
  }
  
  def drop(lower: Int)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = math.max(0, lower)
    val until = length
    while (i < until) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  def take(upper: Int)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = 0
    val until = math.min(upper, length)
    while (i < until) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  def slice(lower: Int, upper: Int)(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = math.max(0, lower)
    val until = math.min(upper, length)
    while (i < until) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  def reverse(implicit builder: Builder[Kind, A]): builder.Result = {
    var i = length - 1
    while (i >= 0) {
      builder += apply(i)
      i -= 1
    }
    builder.result
  }
}
