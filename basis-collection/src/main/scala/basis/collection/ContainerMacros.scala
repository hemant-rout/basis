/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collection

import basis._

private[basis] object ContainerMacros {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  private def deconstruct[A : c.WeakTypeTag](c: Context): c.Expr[Container[A]] = {
    import c.universe._
    val Apply(_, self :: Nil) = c.prefix.tree
    c.Expr[Container[A]](self)
  }
  
  def foreach[A : c.WeakTypeTag, U : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => U])
    : c.Expr[Unit] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      while (!iter.isEmpty) {
        f.splice(iter.head)
        iter.step()
      }
    }
  }
  
  def foldLeft[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (z: c.Expr[B])
      (op: c.Expr[(B, A) => B])
    : c.Expr[B] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var r = z.splice
      while (!iter.isEmpty) {
        r = op.splice(r, iter.head)
        iter.step()
      }
      r
    }
  }
  
  def reduceLeft[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(B, A) => B])
    : c.Expr[B] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      if (iter.isEmpty) throw new java.lang.UnsupportedOperationException
      var r = iter.head: B
      iter.step()
      while (!iter.isEmpty) {
        r = op.splice(r, iter.head)
        iter.step()
      }
      r
    }
  }
  
  def reduceLeftOption[A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (op: c.Expr[(B, A) => B])
    : c.Expr[Option[B]] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      if (iter.isEmpty) None
      else {
        var r = iter.head: B
        iter.step()
        while (!iter.isEmpty) {
          r = op.splice(r, iter.head)
          iter.step()
        }
        Some(r)
      }
    }
  }
  
  def find[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Option[A]] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var r = None: Option[A]
      while (r.isEmpty && !iter.isEmpty) {
        val x = iter.head
        if (p.splice(x)) r = Some(x)
        else iter.step()
      }
      r
    }
  }
  
  def forall[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Boolean] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var r = true
      while (r && !iter.isEmpty) {
        if (!p.splice(iter.head)) r = false
        else iter.step()
      }
      r
    }
  }
  
  def exists[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Boolean] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var r = false
      while (!r && !iter.isEmpty) {
        if (p.splice(iter.head)) r = true
        else iter.step()
      }
      r
    }
  }
  
  def count[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
    : c.Expr[Int] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var t = 0
      while (!iter.isEmpty) {
        if (p.splice(iter.head)) t += 1
        iter.step()
      }
      t
    }
  }
  
  def select[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (q: c.Expr[scala.PartialFunction[A, B]])
    : c.Expr[Option[B]] = {
    val self = deconstruct[A](c)
    c.universe.reify {
      val iter = self.splice.iterator
      var r = None: Option[B]
      while (r.isEmpty && !iter.isEmpty) {
        val x = iter.head
        if (q.splice.isDefinedAt(x)) r = Some(q.splice(x))
        else iter.step()
      }
      r
    }
  }
  
  def collect[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (q: c.Expr[scala.PartialFunction[A, B]])
      (buffer: c.Expr[Buffer[Nothing, B]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, B]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty) {
        val x = iter.head
        if (q.splice.isDefinedAt(x)) b += q.splice(x)
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def map[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => B])
      (buffer: c.Expr[Buffer[Nothing, B]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, B]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty) {
        b += f.splice(iter.head)
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def flatMap[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => Container[B]])
      (buffer: c.Expr[Buffer[Nothing, B]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, B]#State] {
      val b = buffer.splice
      val outer = self.splice.iterator
      while (!outer.isEmpty) {
        val inner = f.splice(outer.head).iterator
        while (!inner.isEmpty) {
          b += inner.head
          inner.step()
        }
        outer.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def filter[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty) {
        val x = iter.head
        if (p.splice(x)) b += x
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def dropWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty && {
        val x = iter.head
        iter.step()
        p.splice(x) || { b += x; false }
      }) ()
      while (!iter.isEmpty) {
        b += iter.head
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def takeWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty && {
        val x = iter.head
        iter.step()
        p.splice(x) && { b += x; true }
      }) ()
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def span[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (builderA: c.Expr[Buffer[Nothing, A]], builderB: c.Expr[Buffer[Nothing, A]])
    : c.Expr[(builderA.value.State, builderB.value.State)] = {
    val self = deconstruct[A](c)
    c.universe.reify[(Buffer[Nothing, A]#State, Buffer[Nothing, A]#State)] {
      val a = builderA.splice
      val b = builderB.splice
      val iter = self.splice.iterator
      while (!iter.isEmpty && {
        val x = iter.head
        iter.step()
        p.splice(x) && { a += x; true } || { b += x; false }
      }) ()
      while (!iter.isEmpty) {
        b += iter.head
        iter.step()
      }
      (a.check, b.check)
    }.asInstanceOf[c.Expr[(builderA.value.State, builderB.value.State)]]
  }
  
  def drop[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      var i = 0
      val n = lower.splice
      while (i < n && !iter.isEmpty) {
        i += 1
        iter.step()
      }
      while (!iter.isEmpty) {
        b += iter.head
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def take[A : c.WeakTypeTag]
      (c: Context)
      (upper: c.Expr[Int])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      var i = 0
      val n = upper.splice
      while (i < n && !iter.isEmpty) {
        i += 1
        b += iter.head
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def slice[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int], upper: c.Expr[Int])
      (buffer: c.Expr[Buffer[Nothing, A]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, A]#State] {
      val b = buffer.splice
      val iter = self.splice.iterator
      var i = 0
      var n = lower.splice
      while (i < n && !iter.isEmpty) {
        i += 1
        iter.step()
      }
      n = upper.splice
      while (i < n && !iter.isEmpty) {
        i += 1
        b += iter.head
        iter.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def zip[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (that: c.Expr[Container[B]])
      (buffer: c.Expr[Buffer[Nothing, (A, B)]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, (A, B)]#State] {
      val b = buffer.splice
      val xs = self.splice.iterator
      val ys = that.splice.iterator
      while (!xs.isEmpty && !ys.isEmpty) {
        b += ((xs.head, ys.head))
        xs.step()
        ys.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
  
  def ++ [A : c.WeakTypeTag, B >: A : c.WeakTypeTag]
      (c: Context)
      (that: c.Expr[Container[B]])
      (buffer: c.Expr[Buffer[Nothing, B]])
    : c.Expr[buffer.value.State] = {
    val self = deconstruct[A](c)
    c.universe.reify[Buffer[Nothing, B]#State] {
      val b = buffer.splice
      val xs = self.splice.iterator
      while (!xs.isEmpty) {
        b += xs.head
        xs.step()
      }
      val ys = that.splice.iterator
      while (!ys.isEmpty) {
        b += ys.head
        ys.step()
      }
      b.check
    }.asInstanceOf[c.Expr[buffer.value.State]]
  }
}
