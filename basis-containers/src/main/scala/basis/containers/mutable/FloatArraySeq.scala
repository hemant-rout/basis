/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers
package mutable

import basis.collections._
import basis.util._

private[containers] final class FloatArraySeq(array: Array[Float]) extends ArraySeq[Float] {
  override def isEmpty: Boolean = array.length == 0
  
  override def length: Int = array.length
  
  override def apply(index: Int): Float = array(index)
  
  override def update(index: Int, value: Float): Unit = array(index) = value
  
  override def copyToArray(xs: Array[Float], start: Int, count: Int): Unit =
    java.lang.System.arraycopy(array, 0, xs, start, count min (xs.length - start) min length)
  
  override def copyToArray(xs: Array[Float], start: Int): Unit =
    java.lang.System.arraycopy(array, 0, xs, start, (xs.length - start) min length)
  
  override def copyToArray(xs: Array[Float]): Unit =
    java.lang.System.arraycopy(array, 0, xs, 0, xs.length min length)
  
  override def iterator: Iterator[Float] = new FloatArraySeqIterator(array)
}

private[containers] final class FloatArraySeqIterator
    (array: Array[Float], private[this] var i: Int, n: Int)
  extends Iterator[Float] {
  
  def this(array: Array[Float]) = this(array, 0, array.length)
  
  override def isEmpty: Boolean = i >= n
  
  override def head: Float = {
    if (i < n) array(i)
    else throw new NoSuchElementException("Head of empty iterator.")
  }
  
  override def step() {
    if (i < n) i += 1
    else throw new UnsupportedOperationException("Empty iterator step.")
  }
  
  override def dup: Iterator[Float] = new FloatArraySeqIterator(array, i, n)
}

private[containers] final class FloatArraySeqBuilder extends Builder[Any, Float, ArraySeq[Float]] {
  private[this] var array: Array[Float] = _
  
  private[this] var aliased: Boolean = true
  
  private[this] var length: Int = 0
  
  private[this] def expand(base: Int, size: Int): Int = {
    var n = (base max size) - 1
    n |= n >> 1; n |= n >> 2; n |= n >> 4; n |= n >> 8; n |= n >> 16
    n + 1
  }
  
  private[this] def resize(size: Int) {
    val newArray = new Array[Float](size)
    if (array != null) java.lang.System.arraycopy(array, 0, newArray, 0, array.length min size)
    array = newArray
  }
  
  private[this] def prepare(size: Int) {
    if (aliased || size > array.length) {
      resize(expand(16, size))
      aliased = false
    }
  }
  
  override def += (value: Float): this.type = {
    prepare(length + 1)
    array(length) = value
    length += 1
    this
  }
  
  override def ++= (xs: Enumerator[Float]): this.type = xs match {
    case xs: FloatArraySeq =>
      prepare(length + xs.length)
      xs.copyToArray(array, length)
      length += xs.length
      this
    case _ => super.++=(xs)
  }
  
  override def expect(count: Int): this.type = {
    if (array == null || length + count > array.length) {
      resize(length + count)
      aliased = false
    }
    this
  }
  
  override def state: ArraySeq[Float] = {
    if (array == null || length != array.length) resize(length)
    aliased = true
    new FloatArraySeq(array)
  }
  
  override def clear() {
    array = null
    aliased = true
    length = 0
  }
}
