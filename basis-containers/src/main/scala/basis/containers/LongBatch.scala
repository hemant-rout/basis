/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.runtime._

import scala.annotation.{switch, tailrec}

private[containers] final class LongBatch1(_1: Long) extends Batch[Long] with Reified {
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 1
  
  override def apply(index: Int): Long = {
    if (index == 0) _1
    else throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) {
      if (index == 0) new LongBatch1(elem.asInstanceOf[Long])
      else throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _1
  
  override def init: Batch[Long] = Batch.Empty
  
  override def tail: Batch[Long] = Batch.Empty
  
  override def drop(lower: Int): Batch[Long] = if (lower <= 0) this else Batch.Empty
  
  override def take(upper: Int): Batch[Long] = if (upper <= 0) Batch.Empty else this
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch2(_1, elem.asInstanceOf[Long])
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch2(elem.asInstanceOf[Long], _1)
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch1(_1)
}

private[containers] final class LongBatch2(_1: Long, _2: Long) extends Batch[Long] with Reified {
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 2
  
  override def apply(index: Int): Long = {
    if (index == 0) _1
    else if (index == 1) _2
    else throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) {
      if (index == 0) new LongBatch2(elem.asInstanceOf[Long], _2)
      else if (index == 1) new LongBatch2(_1, elem.asInstanceOf[Long])
      else throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _2
  
  override def init: Batch[Long] = new LongBatch1(_1)
  
  override def tail: Batch[Long] = new LongBatch1(_2)
  
  override def drop(lower: Int): Batch[Long] = {
    if (lower <= 0) this
    else if (lower == 1) new LongBatch1(_2)
    else Batch.Empty
  }
  
  override def take(upper: Int): Batch[Long] = {
    if (upper <= 0) Batch.Empty
    else if (upper == 1) new LongBatch1(_1)
    else this
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch3(_1, _2, elem.asInstanceOf[Long])
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch3(elem.asInstanceOf[Long], _1, _2)
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch2(_1, _2)
}

private[containers] final class LongBatch3(_1: Long, _2: Long, _3: Long) extends Batch[Long] with Reified {
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 3
  
  override def apply(index: Int): Long = (index: @switch) match {
    case 0 => _1
    case 1 => _2
    case 2 => _3
    case _ => throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) (index: @switch) match {
      case 0 => new LongBatch3(elem.asInstanceOf[Long], _2, _3)
      case 1 => new LongBatch3(_1, elem.asInstanceOf[Long], _3)
      case 2 => new LongBatch3(_1, _2, elem.asInstanceOf[Long])
      case _ => throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _3
  
  override def init: Batch[Long] = new LongBatch2(_1, _2)
  
  override def tail: Batch[Long] = new LongBatch2(_2, _3)
  
  @tailrec override def drop(lower: Int): Batch[Long] = (lower: @switch) match {
    case 0 => this
    case 1 => new LongBatch2(_2, _3)
    case 2 => new LongBatch1(_3)
    case 3 => Batch.Empty
    case _ => if (lower < 0) drop(0) else drop(3)
  }
  
  @tailrec override def take(upper: Int): Batch[Long] = (upper: @switch) match {
    case 0 => Batch.Empty
    case 1 => new LongBatch1(_1)
    case 2 => new LongBatch2(_1, _2)
    case 3 => this
    case _ => if (upper < 0) take(0) else take(3)
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch4(_1, _2, _3, elem.asInstanceOf[Long])
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch4(elem.asInstanceOf[Long], _1, _2, _3)
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch3(_1, _2, _3)
}

private[containers] final class LongBatch4
    (_1: Long, _2: Long, _3: Long, _4: Long)
  extends Batch[Long] with Reified {
  
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 4
  
  override def apply(index: Int): Long = (index: @switch) match {
    case 0 => _1
    case 1 => _2
    case 2 => _3
    case 3 => _4
    case _ => throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) (index: @switch) match {
      case 0 => new LongBatch4(elem.asInstanceOf[Long], _2, _3, _4)
      case 1 => new LongBatch4(_1, elem.asInstanceOf[Long], _3, _4)
      case 2 => new LongBatch4(_1, _2, elem.asInstanceOf[Long], _4)
      case 3 => new LongBatch4(_1, _2, _3, elem.asInstanceOf[Long])
      case _ => throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _4
  
  override def init: Batch[Long] = new LongBatch3(_1, _2, _3)
  
  override def tail: Batch[Long] = new LongBatch3(_2, _3, _4)
  
  @tailrec override def drop(lower: Int): Batch[Long] = (lower: @switch) match {
    case 0 => this
    case 1 => new LongBatch3(_2, _3, _4)
    case 2 => new LongBatch2(_3, _4)
    case 3 => new LongBatch1(_4)
    case 4 => Batch.Empty
    case _ => if (lower < 0) drop(0) else drop(4)
  }
  
  @tailrec override def take(upper: Int): Batch[Long] = (upper: @switch) match {
    case 0 => Batch.Empty
    case 1 => new LongBatch1(_1)
    case 2 => new LongBatch2(_1, _2)
    case 3 => new LongBatch3(_1, _2, _3)
    case 4 => this
    case _ => if (upper < 0) take(0) else take(4)
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch5(_1, _2, _3, _4, elem.asInstanceOf[Long])
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch5(elem.asInstanceOf[Long], _1, _2, _3, _4)
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch4(_1, _2, _3, _4)
}

private[containers] final class LongBatch5
    (_1: Long, _2: Long, _3: Long, _4: Long, _5: Long)
  extends Batch[Long] with Reified {
  
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 5
  
  override def apply(index: Int): Long = (index: @switch) match {
    case 0 => _1
    case 1 => _2
    case 2 => _3
    case 3 => _4
    case 4 => _5
    case _ => throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) (index: @switch) match {
      case 0 => new LongBatch5(elem.asInstanceOf[Long], _2, _3, _4, _5)
      case 1 => new LongBatch5(_1, elem.asInstanceOf[Long], _3, _4, _5)
      case 2 => new LongBatch5(_1, _2, elem.asInstanceOf[Long], _4, _5)
      case 3 => new LongBatch5(_1, _2, _3, elem.asInstanceOf[Long], _5)
      case 4 => new LongBatch5(_1, _2, _3, _4, elem.asInstanceOf[Long])
      case _ => throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _5
  
  override def init: Batch[Long] = new LongBatch4(_1, _2, _3, _4)
  
  override def tail: Batch[Long] = new LongBatch4(_2, _3, _4, _5)
  
  @tailrec override def drop(lower: Int): Batch[Long] = (lower: @switch) match {
    case 0 => this
    case 1 => new LongBatch4(_2, _3, _4, _5)
    case 2 => new LongBatch3(_3, _4, _5)
    case 3 => new LongBatch2(_4, _5)
    case 4 => new LongBatch1(_5)
    case 5 => Batch.Empty
    case _ => if (lower < 0) drop(0) else drop(5)
  }
  
  @tailrec override def take(upper: Int): Batch[Long] = (upper: @switch) match {
    case 0 => Batch.Empty
    case 1 => new LongBatch1(_1)
    case 2 => new LongBatch2(_1, _2)
    case 3 => new LongBatch3(_1, _2, _3)
    case 4 => new LongBatch4(_1, _2, _3, _4)
    case 5 => this
    case _ => if (upper < 0) take(0) else take(5)
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch6(_1, _2, _3, _4, _5, elem.asInstanceOf[Long])
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) new LongBatch6(elem.asInstanceOf[Long], _1, _2, _3, _4, _5)
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch5(_1, _2, _3, _4, _5)
}

private[containers] final class LongBatch6
    (_1: Long, _2: Long, _3: Long, _4: Long, _5: Long, _6: Long)
  extends Batch[Long] with Reified {
  
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def length: Int = 6
  
  override def apply(index: Int): Long = (index: @switch) match {
    case 0 => _1
    case 1 => _2
    case 2 => _3
    case 3 => _4
    case 4 => _5
    case 5 => _6
    case _ => throw new IndexOutOfBoundsException(index.toString)
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) (index: @switch) match {
      case 0 => new LongBatch6(elem.asInstanceOf[Long], _2, _3, _4, _5, _6)
      case 1 => new LongBatch6(_1, elem.asInstanceOf[Long], _3, _4, _5, _6)
      case 2 => new LongBatch6(_1, _2, elem.asInstanceOf[Long], _4, _5, _6)
      case 3 => new LongBatch6(_1, _2, _3, elem.asInstanceOf[Long], _5, _6)
      case 4 => new LongBatch6(_1, _2, _3, _4, elem.asInstanceOf[Long], _6)
      case 5 => new LongBatch6(_1, _2, _3, _4, _5, elem.asInstanceOf[Long])
      case _ => throw new IndexOutOfBoundsException(index.toString)
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = _1
  
  override def last: Long = _6
  
  override def init: Batch[Long] = new LongBatch5(_1, _2, _3, _4, _5)
  
  override def tail: Batch[Long] = new LongBatch5(_2, _3, _4, _5, _6)
  
  @tailrec override def drop(lower: Int): Batch[Long] = (lower: @switch) match {
    case 0 => this
    case 1 => new LongBatch5(_2, _3, _4, _5, _6)
    case 2 => new LongBatch4(_3, _4, _5, _6)
    case 3 => new LongBatch3(_4, _5, _6)
    case 4 => new LongBatch2(_5, _6)
    case 5 => new LongBatch1(_6)
    case 6 => Batch.Empty
    case _ => if (lower < 0) drop(0) else drop(6)
  }
  
  @tailrec override def take(upper: Int): Batch[Long] = (upper: @switch) match {
    case 0 => Batch.Empty
    case 1 => new LongBatch1(_1)
    case 2 => new LongBatch2(_1, _2)
    case 3 => new LongBatch3(_1, _2, _3)
    case 4 => new LongBatch4(_1, _2, _3, _4)
    case 5 => new LongBatch5(_1, _2, _3, _4, _5)
    case 6 => this
    case _ => if (upper < 0) take(0) else take(6)
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long])
      new LongBatchN(7, new LongBatch4(_1, _2, _3, _4), Batch.Empty, new LongBatch3(_5, _6, elem.asInstanceOf[Long]))
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long])
      new LongBatchN(7, new LongBatch3(elem.asInstanceOf[Long], _1, _2), Batch.Empty, new LongBatch4(_3, _4, _5, _6))
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatch6(_1, _2, _3, _4, _5, _6)
}

private[containers] final class LongBatchN
    (override val length: Int, prefix: Batch[Long], tree: Batch[Batch[Long]], suffix: Batch[Long])
  extends Batch[Long] with Reified {
  
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def isEmpty: Boolean = false
  
  override def apply(index: Int): Long = {
    val n = index - prefix.length
    if (n < 0) prefix(index)
    else {
      val k = n - (tree.length << 2)
      if (k < 0) tree(n >> 2)(n & 3)
      else suffix(k)
    }
  }
  
  override def update[B >: Long](index: Int, elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) {
      val n = index - prefix.length
      if (n < 0) new LongBatchN(length, prefix.update(index, elem).asInstanceOf[Batch[Long]], tree, suffix)
      else {
        val k = n - (tree.length << 2)
        if (k < 0)
          new LongBatchN(
            length,
            prefix,
            tree.update(n >> 2, tree(n >> 2).update(n & 3, elem).asInstanceOf[Batch[Long]]),
            suffix)
        else new LongBatchN(length, prefix, tree, suffix.update(index, elem).asInstanceOf[Batch[Long]])
      }
    }
    else lift.update(index, elem)
  }
  
  override def head: Long = prefix.head
  
  override def last: Long = suffix.last
  
  override def init: Batch[Long] = {
    if (suffix.length == 1) {
      if (tree.isEmpty) prefix
      else new LongBatchN(length - 1, prefix, tree.init, tree.last)
    }
    else new LongBatchN(length - 1, prefix, tree, suffix.init)
  }
  
  override def tail: Batch[Long] = {
    if (prefix.length == 1) {
      if (tree.isEmpty) suffix
      else new LongBatchN(length - 1, tree.head, tree.tail, suffix)
    }
    else new LongBatchN(length - 1, prefix.tail, tree, suffix)
  }
  
  override def drop(lower: Int): Batch[Long] = {
    val n = lower - prefix.length
    if (lower <= 0) this
    else if (n < 0) new LongBatchN(length - lower, prefix.drop(lower), tree, suffix)
    else {
      val k = n - (tree.length << 2)
      if (k < 0) {
        val split = tree.drop(n >> 2)
        new LongBatchN(length - lower, split.head.drop(n & 3), split.tail, suffix)
      }
      else suffix.drop(k)
    }
  }
  
  override def take(upper: Int): Batch[Long] = {
    val n = upper - prefix.length
    if (upper == length) this
    else if (n <= 0) prefix.take(upper)
    else {
      val k = n - (tree.length << 2)
      if (k <= 0) {
        val split = tree.take(((n + 3) & ~3) >> 2)
        new LongBatchN(upper, prefix, split.init, split.last.take(((((n & 3) ^ 3) + 1) & 4) | (n & 3)))
      }
      else new LongBatchN(upper, prefix, tree, suffix.take(k))
    }
  }
  
  override def append[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) {
      if (suffix.length == 6)
        new LongBatchN(
          length + 1,
          prefix,
          tree :+ new LongBatch4(suffix(0), suffix(1), suffix(2), suffix(3)),
          new LongBatch3(suffix(4), suffix(5), elem.asInstanceOf[Long]))
      else new LongBatchN(length + 1, prefix, tree, (suffix :+ elem).asInstanceOf[Batch[Long]])
    }
    else lift.append(elem)
  }
  
  override def prepend[B >: Long](elem: B): Batch[B] = {
    if (elem.isInstanceOf[Long]) {
      if (prefix.length == 6)
        new LongBatchN(
          length + 1,
          new LongBatch3(elem.asInstanceOf[Long], prefix(0), prefix(1)),
          new LongBatch4(prefix(2), prefix(3), prefix(4), prefix(5)) +: tree,
          suffix)
      else new LongBatchN(length + 1, (elem +: prefix).asInstanceOf[Batch[Long]], tree, suffix)
    }
    else lift.prepend(elem)
  }
  
  private[this] def lift: Batch[Long] = new RefBatchN(length, prefix, tree, suffix)
}