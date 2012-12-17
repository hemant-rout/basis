/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.util._

import scala.reflect.ClassTag

private[containers] class RefArrayBuffer[A] private (
    private[this] var buffer: Array[AnyRef],
    private[this] var size: Int,
    private[this] var aliased: Boolean)
  extends ArrayBuffer[A] {
  
  def this() = this(null, 0, true)
  
  final override def isEmpty: Boolean = size == 0
  
  final override def length: Int = size
  
  final override def apply(index: Int): A = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index).asInstanceOf[A]
  }
  
  final override def update(index: Int, elem: A) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index) = elem.asInstanceOf[AnyRef]
  }
  
  final override def append(elem: A) {
    var array = buffer
    if (aliased || size + 1 > array.length) {
      array = new Array[AnyRef](expand(16, size + 1))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
      aliased = false
    }
    array(size) = elem.asInstanceOf[AnyRef]
    size += 1
  }
  
  final override def appendAll(elems: Enumerator[A]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[A]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[AnyRef](expand(16, size + n))
        if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
        buffer = array
        aliased = false
      }
      xs.copyToArray(array.asInstanceOf[Array[Any]], size)
      size += n
    }
    else super.appendAll(elems)
  }
  
  final override def prepend(elem: A) {
    var array = buffer
    if (aliased || size + 1 > array.length)
      array = new Array[AnyRef](expand(16, size + 1))
    if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 1, size)
    array(0) = elem.asInstanceOf[AnyRef]
    buffer = array
    size += 1
    aliased = false
  }
  
  final override def prependAll(elems: Enumerator[A]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[A]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length)
        array = new Array[AnyRef](expand(16, size + n))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, n, size)
      xs.copyToArray(array.asInstanceOf[Array[Any]], 0)
      buffer = array
      size += n
      aliased = false
    }
    else super.prependAll(elems)
  }
  
  final override def insert(index: Int, elem: A) {
    if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
    if (index == size) append(elem)
    else if (index == 0) prepend(elem)
    else {
      var array = buffer
      if (aliased || size + 1 > array.length) {
        array = new Array[AnyRef](expand(16, size + 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index, array, index + 1, size - index)
      array(index) = elem.asInstanceOf[AnyRef]
      buffer = array
      size += 1
      aliased = false
    }
  }
  
  final override def insertAll(index: Int, elems: Enumerator[A]) {
    if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
    if (index == size) appendAll(elems)
    else if (index == 0) prependAll(elems)
    else if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[A]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[AnyRef](expand(16, size + n))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index, array, index + n, size - index)
      xs.copyToArray(array.asInstanceOf[Array[Any]], index)
      buffer = array
      size += n
      aliased = false
    }
    else super.insertAll(index, elems)
  }
  
  final override def remove(index: Int): A = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    var array = buffer
    val x = array(index).asInstanceOf[A]
    if (size == 1) clear()
    else {
      if (aliased) {
        array = new Array[AnyRef](expand(16, size - 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + 1, array, index, size - index - 1)
      if (buffer eq array) array(size - 1) = null
      size -= 1
      buffer = array
      aliased = false
    }
    x
  }
  
  final override def remove(index: Int, count: Int) {
    if (count < 0) throw new IllegalArgumentException("negative count")
    if (index < 0) throw new IndexOutOfBoundsException(index.toString)
    if (index + count > size) throw new IndexOutOfBoundsException((index + count).toString)
    if (size == count) clear()
    else {
      var array = buffer
      if (aliased) {
        array = new Array[AnyRef](expand(16, size - count))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + count, array, index, size - index - count)
      if (buffer eq array) java.util.Arrays.fill(array, size - count, size, null)
      size -= count
      buffer = array
      aliased = false
    }
  }
  
  final override def clear() {
    aliased = true
    size = 0
    buffer = null
  }
  
  final override def copyToArray[B >: A](xs: Array[B], start: Int, count: Int) {
    if (xs.isInstanceOf[Array[AnyRef]])
      java.lang.System.arraycopy(buffer, 0, xs, start, count min (xs.length - start) min size)
    else super.copyToArray(xs, start, count)
  }
  
  final override def copyToArray[B >: A](xs: Array[B], start: Int) {
    if (xs.isInstanceOf[Array[AnyRef]])
      java.lang.System.arraycopy(buffer, 0, xs, start, (xs.length - start) min size)
    else super.copyToArray(xs, start)
  }
  
  final override def copyToArray[B >: A](xs: Array[B]) {
    if (xs.isInstanceOf[Array[AnyRef]])
      java.lang.System.arraycopy(buffer, 0, xs, 0, xs.length min size)
    else super.copyToArray(xs)
  }
  
  final override def toArray[B >: A](implicit B: ClassTag[B]): Array[B] = {
    val xs = B.newArray(size)
    if (xs.isInstanceOf[Array[AnyRef]])
      java.lang.System.arraycopy(buffer, 0, xs, 0, size)
    else super.copyToArray(xs)
    xs
  }
  
  final override def toArraySeq: ArraySeq[A] = {
    if (buffer == null || buffer.length != size) {
      var array = new Array[AnyRef](size)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    aliased = true
    new RefArraySeq(buffer)
  }
  
  private[containers] final def copy: ArrayBuffer[A] = {
    aliased = true
    new RefArrayBuffer(buffer, size, aliased)
  }
  
  final def expect(count: Int): this.type = {
    if (buffer == null || size + count > buffer.length) {
      var array = new Array[AnyRef](size + count)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    this
  }
  
  final override def iterator: Iterator[A] = new RefArrayBufferIterator(this)
  
  protected override def stringPrefix: String = "ArrayBuffer"
  
  private[this] def expand(base: Int, size: Int): Int = {
    var n = (base max size) - 1
    n |= n >> 1; n |= n >> 2; n |= n >> 4; n |= n >> 8; n |= n >> 16
    n + 1
  }
}

private[containers] final class RefArrayBufferIterator[+A] private (
    private[this] val b: RefArrayBuffer[A],
    private[this] var i: Int,
    private[this] var n: Int,
    private[this] var x: A)
  extends Iterator[A] {
  
  def this(b: RefArrayBuffer[A]) = this(b, 0, b.length, if (!b.isEmpty) b(0) else null.asInstanceOf[A])
  
  override def isEmpty: Boolean = i >= n
  
  override def head: A = {
    if (i >= n) throw new NoSuchElementException("Head of empty iterator.")
    x
  }
  
  override def step() {
    if (i >= n) throw new UnsupportedOperationException("Empty iterator step.")
    i += 1
    n = b.length
    x = if (i < n) b(i) else null.asInstanceOf[A]
  }
  
  override def dup: Iterator[A] = new RefArrayBufferIterator(b, i, n, x)
}

private[containers] final class RefArrayBufferBuilder[A]
  extends RefArrayBuffer[A] with Builder[Any, A] {
  
  override type State = ArrayBuffer[A]
  
  override def state: ArrayBuffer[A] = copy
  
  protected override def stringPrefix: String = "ArrayBuffer.Builder"
}
