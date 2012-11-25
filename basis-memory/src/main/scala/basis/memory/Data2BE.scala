/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.memory

/** Big-endian data backed by a `Short` array. */
private[memory] final class Data2BE(override val words: Array[Short]) extends Data2 with DataBE {
  override def endian: BigEndian.type = BigEndian
  
  override def copy(size: Long): Data2BE = {
    Predef.require(0L <= size && size <= (Int.MaxValue.toLong << 1))
    val words = new Array[Short]((align(size, 2L) >> 1).toInt)
    java.lang.System.arraycopy(this.words, 0, words, 0, java.lang.Math.min(this.words.length, words.length))
    new Data2BE(words)
  }
  
  override def loadByte(address: Long): Byte = {
    val i = (address >> 1).toInt
    val j = ((address.toInt & 1) ^ 1) << 3
    (words(i) >>> j).toByte
  }
  
  override def storeByte(address: Long, value: Byte) {
    val i = (address >> 1).toInt
    val j = ((address.toInt & 1) ^ 1) << 3
    words(i) = ((words(i) & ~(0xFF << j)) | ((value & 0xFF) << j)).toShort
  }
  
  override def loadShort(address: Long): Short =
    words((address >> 1).toInt)
  
  override def storeShort(address: Long, value: Short): Unit =
    words((address >> 1).toInt) = value
  
  override def loadInt(address: Long): Int = {
    val i = (address >> 1).toInt & -2
    (words(i)               << 16) |
    (words(i + 1) & 0xFFFF)
  }
  
  override def storeInt(address: Long, value: Int) {
    val i = (address >> 1).toInt & -2
    words(i)     = (value >>> 16).toShort
    words(i + 1) =  value.toShort
  }
  
  override def loadLong(address: Long): Long = {
    val i = (address >> 1).toInt & -4
     (words(i).toLong               << 48) |
    ((words(i + 1) & 0xFFFF).toLong << 32) |
    ((words(i + 2) & 0xFFFF).toLong << 16) |
     (words(i + 3) & 0xFFFF).toLong
  }
  
  override def storeLong(address: Long, value: Long) {
    val i = (address >> 1).toInt & -4
    words(i)     = (value >>> 48).toShort
    words(i + 1) = (value >>> 32).toShort
    words(i + 2) = (value >>> 16).toShort
    words(i + 3) =  value.toShort
  }
  
  override def toString: String = "Data2BE"+"("+ size +")"
}

/** An allocator for big-endian data backed by a `Short` array. */
private[memory] object Data2BE extends Allocator with (Long => Data2BE) {
  override def MaxSize: Long = Int.MaxValue.toLong << 1
  
  override def alloc[T](count: Long)(implicit unit: ValType[T]): Data2BE =
    apply(unit.size * count)
  
  override def apply(size: Long): Data2BE = {
    Predef.require(0L <= size && size <= MaxSize)
    val words = new Array[Short]((align(size, 2L) >> 1).toInt)
    new Data2BE(words)
  }
  
  override def toString: String = "Data2BE"
}
