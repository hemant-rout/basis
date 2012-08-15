/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.encoding
package utf16

trait Rope extends Any with Text {
  override type Kind <: Rope
  
  override def iterator: Text.Iterator = new Rope.Iterator(this, 0)
  
  override def size: Int
  
  def get(index: Int): Int
  
  override def apply(index: Int): Int = {
    val n = size
    if (index < 0 || index >= n) throw new IndexOutOfBoundsException(index.toString)
    val c1 = get(index)
    if (c1 <= 0xD7FF || c1 >= 0xE000) c1 // U+0000..U+D7FF | U+E000..U+FFFF
    else if (c1 <= 0xDBFF && index + 1 < n) { // c1 >= 0xD800
      val c2 = get(index + 1)
      if (c2 >= 0xDC00 && c2 <= 0xDFFF) // U+10000..U+10FFFF
        (((c1 & 0x3FF) << 10) | (c2 & 0x3FF)) + 0x10000
      else 0x4010FFFF
    }
    else 0x4010FFFF
  }
  
  override def advance(index: Int): Int = {
    val n = size
    if (index < 0 || index >= n) throw new IndexOutOfBoundsException(index.toString)
    val c1 = get(index)
    if (c1 <= 0xD7FF || c1 >= 0xE000)
      index + 1 // U+0000..U+D7FF | U+E000..U+FFFF
    else if (c1 <= 0xDBFF && index + 1 < n) { // c1 >= 0xD800
      val c2 = get(index + 1)
      if (c2 >= 0xDC00 && c2 <= 0xDFFF)
        index + 2 // U+10000..U+10FFFF
      else index + 1
    }
    else index + 1
  }
  
  def ++ (that: Rope): Rope = new Rope.Concat(this, that)
}

object Rope {
  private final class Iterator(rope: Rope, private[this] var index: Int) extends Text.Iterator {
    override def hasNext: Boolean =
      index >= 0 && index < rope.size
    
    override def next(): Int = {
      val c = rope.get(index)
      index = rope.advance(index)
      c
    }
  }
  
  private final class Concat(prefix: Rope, suffix: Rope) extends Rope {
    private[this] val prefixSize: Int = prefix.size
    private[this] val suffixSize: Int = suffix.size
    
    override def size: Int = prefixSize + suffixSize
    
    override def get(index: Int): Int = {
      if (index < prefixSize) prefix.get(index)
      else suffix.get(index - prefixSize)
    }
  }
}
