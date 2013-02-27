/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.dispatch

import basis.containers._
import basis.control._

import scala.runtime.AbstractFunction0
import scala.runtime.AbstractFunction1

/** A pending computation relayed by the default async implementation.
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.1
  * 
  * @groupprio  Evaluating  1
  * @groupprio  Relaying    2
  * @groupprio  Composing   3
  * @groupprio  Recovering  4
  */
private[basis] class Thunk[A] extends Latch[A] with Relay[A] {
  import MetaThunk._
  
  @volatile private[dispatch] final var state: AnyRef = Batch.empty[Latch[A]]
  
  final override def isSet: Boolean = state.isInstanceOf[_ Else _]
  
  final override def set(result: Try[A]): Boolean = {
    if (result == null) throw new NullPointerException
    var s = null: AnyRef
    do s = state
    while (!s.isInstanceOf[_ Else _] && !Unsafe.compareAndSwapObject(this, StateOffset, s, result))
    !s.isInstanceOf[_ Else _] && { s.asInstanceOf[Batch[Latch[A]]] traverse new Thunk.Trigger(result); true }
  }
  
  final override def forward[B >: A](that: Latch[B]): Unit = defer(that)
  
  override def run[U](f: Try[A] => U): Unit = defer(new Thunk.Run(f))
  
  override def foreach[U](f: A => U): Unit = defer(new Thunk.Foreach(f))
  
  override def andThen[U](q: PartialFunction[Try[A], U]): Relay[A] = {
    val t = new Thunk[A]
    defer(new Thunk.AndThen(q, t))
    t
  }
  
  override def choose[B](q: PartialFunction[A, B]): Relay[B] = {
    val t = new Thunk[B]
    defer(new Thunk.Choose(q, t))
    t
  }
  
  override def map[B](f: A => B): Relay[B] = {
    val t = new Thunk[B]
    defer(new Thunk.Map(f, t))
    t
  }
  
  override def flatMap[B](f: A => Relay[B]): Relay[B] = {
    val t = new Thunk[B]
    defer(new Thunk.FlatMap(f, t))
    t
  }
  
  override def recover[B >: A](q: PartialFunction[Throwable, B]): Relay[B] = {
    val t = new Thunk[B]
    defer(new Thunk.Recover(q, t))
    t
  }
  
  override def recoverWith[B >: A](q: PartialFunction[Throwable, Relay[B]]): Relay[B] = {
    val t = new Thunk[B]
    defer(new Thunk.RecoverWith(q, t))
    t
  }
  
  override def filter(p: A => Boolean): Relay[A] = {
    val t = new Thunk[A]
    defer(new Thunk.Filter(p, t))
    t
  }
  
  override def withFilter(p: A => Boolean): Relay[A] = {
    val t = new Thunk[A]
    defer(new Thunk.Filter(p, t))
    t
  }
  
  override def zip[B](that: Relay[B]): Relay[(A, B)] = {
    val t = new Thunk.Zip[A, B]
    this forward new t.Set1
    that forward new t.Set2
    t
  }
  
  private[basis] final def defer(latch: Latch[A]) {
    if (latch == null) throw new NullPointerException
    var s = null: AnyRef
    do s = state
    while (!s.isInstanceOf[_ Else _] &&
           !Unsafe.compareAndSwapObject(this, StateOffset, state, state.asInstanceOf[Batch[Latch[A]]] :+ latch))
    if (s.isInstanceOf[_ Else _]) latch set s.asInstanceOf[Try[A]]
  }
}

private[basis] object Thunk {
  import MetaThunk._
  
  abstract class Join[A, B] extends Thunk[B] {
    /** Returns the number of results to expect; unbounded when negative. */
    def limit: Int
    
    /** Sets the number of results to expect. */
    def limit_=(value: Int): Unit
    
    /** Accumulates a result and sets this thunk when complete. */
    def put(result: Try[A]): Unit
    
    /** Negates `limit`, and sets this thunk if complete. */
    def commit(): Unit
  }
  
  class JoinAll[A] extends Join[A, Batch[A]] {
    import MetaJoinAll._
    
    @volatile private[dispatch] final var batch: Batch[A] = Batch.empty[A]
    
    @volatile final override var limit: Int = 0
    
    /** Submits a result, setting this thunk upon accumulating `limit` results,
      * or failing this thunk if any result traps. */
    final override def put(result: Try[A]) {
      if (result == null) throw new NullPointerException
      if (result.canTrap) set(result.asInstanceOf[Nothing Else Throwable])
      else {
        var b = null: Batch[A]
        var n = 0
        var k = 0
        do { b = batch; k = limit; n = b.length }
        while ((n < k || k < 0) && !Unsafe.compareAndSwapObject(this, BatchOffset, b, b :+ result.bind))
        k = limit // recheck limit
        if (n == k - 1) set(Bind(batch))
      }
    }
    
    /** Negates `limit`, and sets this thunk if complete. */
    final override def commit() {
      val k = -limit
      limit = k
      val b = batch // must read after updating limit
      if (b.length == k) set(Bind(b)) // safely races with last put
    }
  }
  
  class JoinAny[A] extends Join[A, A] {
    import MetaJoinAny._
    
    @volatile private[dispatch] final var count: Int = 0
    
    @volatile final override var limit: Int = 0
    
    /** Sets this thunk with a successful result, or upon receiving `limit` results. */
    final override def put(result: Try[A]) {
      if (result == null) throw new NullPointerException
      var n = 0
      var k = 0
      do { n = count; k = limit }
      while ((n < k || k < 0) && !Unsafe.compareAndSwapInt(this, CountOffset, n, n + 1))
      k = limit // recheck limit
      if (n == k - 1 || (n < k || k < 0) && result.canBind) set(result)
    }
    
    /** Negates `limit`, and traps this thunk if complete. */
    final override def commit() {
      val k = -limit
      limit = k
      val n = count // must read after updating limit
      if (n == k) set(Trap) // safely races with put
    }
  }
  
  class JoinFirst[A](p: A => Boolean) extends Join[A, A] {
    import MetaJoinFirst._
    
    @volatile private[dispatch] final var count: Int = 0
    
    @volatile final override var limit: Int = 0
    
    /** Sets this thunk with a successful result that satisfies the predicate,
      * or upon receiving `limit` results. */
    final override def put(result: Try[A]) {
      if (result == null) throw new NullPointerException
      var n = 0
      var k = 0
      do { n = count; k = limit }
      while ((n < k || k < 0) && !Unsafe.compareAndSwapInt(this, CountOffset, n, n + 1))
      k = limit // recheck limit
      if ((n < k || k < 0) && result.canBind && p(result.bind)) set(result)
      else if (n == k - 1) set(Trap)
    }
    
    /** Negates `limit`, and traps this thunk if complete. */
    final override def commit() {
      val k = -limit
      limit = k
      val n = count // must read after updating limit
      if (n == k) set(Trap) // safely races with put
    }
  }
  
  class Reduce[A](op: (A, A) => A) extends Join[A, A] {
    import MetaReduce._
    
    @volatile private[dispatch] final var ready: A = null.asInstanceOf[A]
    
    @volatile private[dispatch] final var count: Int = 0
    
    @volatile final override var limit: Int = 0
    
    final override def put(result: Try[A]) {
      if (result == null) throw new NullPointerException
      if (result.canTrap) set(result.asInstanceOf[Nothing Else Throwable])
      else {
        var r = null.asInstanceOf[A]
        do r = ready
        while (r != null && !Unsafe.compareAndSwapObject(this, ReadyOffset, r, null) ||
               r == null && !Unsafe.compareAndSwapObject(this, ReadyOffset, null, result.bind))
        var n = 0
        if (r != null) {
          do n = count
          while (!Unsafe.compareAndSwapInt(this, CountOffset, n, n - 1))
          async exec new Step(r, result.bind)
        }
        else {
          do n = count
          while (!Unsafe.compareAndSwapInt(this, CountOffset, n, n + 2))
          val l = limit
          if (n == l - 1) {
            set(result)
            ready = null.asInstanceOf[A]
          }
        }
      }
    }
    
    final def commit() {
      val k = -limit
      limit = k
      val n = count // must read after updating limit
      if (n == k) {
        set(Maybe(ready)) // safely races with put
        ready = null.asInstanceOf[A]
      }
    }
    
    final class Step(a: A, b: A) extends AbstractFunction0[Unit] {
      override def apply(): Unit =
        put(try Bind(op(a, b)) catch { case e: Throwable => Trap.NonFatal(e) })
    }
  }
  
  final class Eval[-A](expr: => A, t: Latch[A]) extends AbstractFunction0[Unit] {
    override def apply(): Unit =
      t set (try Bind(expr) catch { case e: Throwable => Trap.NonFatal(e) })
  }
  
  final class Exec[-A](thunk: () => A, t: Latch[A]) extends AbstractFunction0[Unit] {
    override def apply(): Unit =
      t set (try Bind(thunk()) catch { case e: Throwable => Trap.NonFatal(e) })
  }
  
  final class PutAll[-A](thunk: () => A, t: Join[A, _]) extends AbstractFunction0[Unit] {
    override def apply(): Unit =
      t put (try Bind(thunk()) catch { case e: Throwable => Trap.NonFatal(e) })
  }
  
  final class PutAny[-A](thunk: () => A, t: Join[A, _]) extends AbstractFunction0[Unit] {
    override def apply(): Unit =
      if (!t.isSet) t put (try Bind(thunk()) catch { case e: Throwable => Trap.NonFatal(e) })
  }
  
  abstract class When[-A] extends AbstractFunction0[Unit] with Latch[A] {
    @volatile protected[this] final var value: Try[A] = _
    final override def isSet: Boolean = value != null
    final override def set(result: Try[A]): Boolean = {
      if (result == null) throw new NullPointerException
      if (value == null) {
        value = result
        async exec this
        true
      }
      else false
    }
    final override def apply() {
      val r = value
      if (r == null) throw new NullPointerException
      apply(r)
    }
    def apply(result: Try[A]): Unit
  }
  
  final class Run[-A](f: Try[A] => _) extends When[A] {
    override def apply(r: Try[A]): Unit = f(r)
  }
  
  final class Foreach[-A](f: A => _) extends When[A] {
    override def apply(r: Try[A]): Unit = if (r.canBind) f(r.bind)
  }
  
  final class AndThen[-A](q: PartialFunction[Try[A], _], t: Latch[A]) extends When[A] {
    override def apply(r: Try[A]) {
      try if (q.isDefinedAt(r)) q(r)
      finally t set r
    }
  }
  
  final class Choose[-A, +B](q: PartialFunction[A, B], t: Latch[B]) extends When[A] {
    override def apply(r: Try[A]) {
      t set (try if (r.canBind) { if (q.isDefinedAt(r.bind)) Bind(q(r.bind)) else Trap }
                 else r.asInstanceOf[Nothing Else Throwable]
             catch { case e: Throwable => Trap.NonFatal(e) })
    }
  }
  
  final class Map[-A, +B](f: A => B, t: Latch[B]) extends When[A] {
    override def apply(r: Try[A]) {
      t set (try if (r.canBind) Bind(f(r.bind)) else r.asInstanceOf[Nothing Else Throwable]
             catch { case e: Throwable => Trap.NonFatal(e) })
    }
  }
  
  final class FlatMap[-A, +B](f: A => Relay[B], t: Latch[B]) extends When[A] {
    override def apply(r: Try[A]) {
      try if (r.canBind) f(r.bind) forward t else t set r.asInstanceOf[Nothing Else Throwable]
      catch { case e: Throwable => t set Trap.NonFatal(e) }
    }
  }
  
  final class Recover[-A](q: PartialFunction[Throwable, A], t: Latch[A]) extends When[A] {
    override def apply(r: Try[A]) {
      t set (try if (r.canSafelyTrap && q.isDefinedAt(r.trap)) Bind(q(r.trap)) else r
             catch { case e: Throwable => Trap.NonFatal(e) })
    }
  }
  
  final class RecoverWith[-A](q: PartialFunction[Throwable, Relay[A]], t: Latch[A]) extends When[A] {
    override def apply(r: Try[A]) {
      try if (r.canSafelyTrap && q.isDefinedAt(r.trap)) q(r.trap) forward t else t set r
      catch { case e: Throwable => t set Trap.NonFatal(e) }
    }
  }
  
  final class Filter[-A](p: A => Boolean, t: Latch[A]) extends When[A] {
    override def apply(r: Try[A]) {
      t set (try if (r.canTrap || p(r.bind)) r else Trap
             catch { case e: Throwable => Trap.NonFatal(e) })
    }
  }
  
  final class Zip[A, B] extends Thunk[(A, B)] {
    @volatile private[dispatch] final var _1: Try[A] = _
    @volatile private[dispatch] final var _2: Try[B] = _
    def set1(r1: Try[A]) {
      if (r1 == null) throw new NullPointerException
      if (_1 == null) {
        _1 = r1
        if (r1.canTrap) set(r1.asInstanceOf[Nothing Else Throwable])
        else {
          val r2 = _2
          if (r2 != null && r2.canBind) set(Bind((r1.bind, r2.bind)))
        }
      }
    }
    def set2(r2: Try[B]) {
      if (r2 == null) throw new NullPointerException
      if (_2 == null) {
        _2 = r2
        if (r2.canTrap) set(r2.asInstanceOf[Nothing Else Throwable])
        else {
          val r1 = _1
          if (r1 != null && r1.canBind) set(Bind((r1.bind, r2.bind)))
        }
      }
    }
    final class Set1 extends When[A] {
      override def apply(r: Try[A]): Unit = set1(r)
    }
    final class Set2 extends When[B] {
      override def apply(r: Try[B]): Unit = set2(r)
    }
  }
  
  final class Trigger[+A](r: Try[A]) extends AbstractFunction1[Latch[A], Unit] {
    override def apply(latch: Latch[A]): Unit = latch set r
  }
}