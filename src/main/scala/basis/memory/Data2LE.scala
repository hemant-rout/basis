/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.memory

/** Little-endian `Short` array backed data. */
final class Data2LE(final val words: Array[Short]) extends AnyVal with DataLE {
  import java.lang.Float.{floatToRawIntBits, intBitsToFloat}
  import java.lang.Double.{doubleToRawLongBits, longBitsToDouble}
  
  @inline final override def size: Long = words.length.toLong << 1
  
  final override def unit: Int = 2
  
  final override def endian: LittleEndian.type = LittleEndian
  
  final override def copy(size: Long = this.size): Data2LE = {
    require(0L <= size && size <= (Int.MaxValue.toLong << 1))
    val words = new Array[Short]((align(size, 2L) >> 1).toInt)
    System.arraycopy(this.words, 0, words, 0, math.min(this.words.length, words.length))
    new Data2LE(words)
  }
  
  final override def loadByte(address: Long): Byte = {
    val i = (address >> 1).toInt
    val j = (address.toInt & 1) << 3
    (words(i) >>> j).toByte
  }
  
  final override def storeByte(address: Long, value: Byte) {
    val i = (address >> 1).toInt
    val j = (address.toInt & 1) << 3
    words(i) = ((words(i) & ~(0xFF << j)) | ((value & 0xFF) << j)).toShort
  }
  
  final override def loadShort(address: Long): Short =
    words((address >> 1).toInt)
  
  final override def storeShort(address: Long, value: Short): Unit =
    words((address >> 1).toInt) = value
  
  final override def loadInt(address: Long): Int = {
    val i = (address >> 1).toInt & -2
    (words(i)     & 0xFFFF)        |
    (words(i + 1)           << 16)
  }
  
  final override def storeInt(address: Long, value: Int) {
    val i = (address >> 1).toInt & -2
    words(i)     =  value.toShort
    words(i + 1) = (value >>> 16).toShort
  }
  
  final override def loadLong(address: Long): Long = {
    val i = (address >> 1).toInt & -4
     (words(i)     & 0xFFFF).toLong        |
    ((words(i + 1) & 0xFFFF).toLong << 16) |
    ((words(i + 2) & 0xFFFF).toLong << 32) |
     (words(i + 3).toLong           << 48)
  }
  
  final override def storeLong(address: Long, value: Long) {
    val i = (address >> 1).toInt & -4
    words(i)     =  value.toShort
    words(i + 1) = (value >>> 16).toShort
    words(i + 2) = (value >>> 32).toShort
    words(i + 3) = (value >>> 48).toShort
  }
  
  final override def loadChar(address: Long): Char =
    loadShort(address).toChar
  
  final override def storeChar(address: Long, value: Char): Unit =
    storeShort(address, value.toShort)
  
  final override def loadFloat(address: Long): Float =
    intBitsToFloat(loadInt(address))
  
  final override def storeFloat(address: Long, value: Float): Unit =
    storeInt(address, floatToRawIntBits(value))
  
  final override def loadDouble(address: Long): Double =
    longBitsToDouble(loadLong(address))
  
  final override def storeDouble(address: Long, value: Double): Unit =
    storeLong(address, doubleToRawLongBits(value))
  
  final override def loadUnalignedShort(address: Long): Short = {
    ((loadByte(address)      & 0xFF)       |
     (loadByte(address + 1L)         << 8)).toShort
  }
  
  final override def storeUnalignedShort(address: Long, value: Short) {
    storeByte(address,       value.toByte)
    storeByte(address + 1L, (value >> 8).toByte)
  }
  
  final override def loadUnalignedInt(address: Long): Int = {
     (loadByte(address)      & 0xFF)        |
    ((loadByte(address + 1L) & 0xFF) <<  8) |
    ((loadByte(address + 2L) & 0xFF) << 16) |
     (loadByte(address + 3L)         << 24)
  }
  
  final override def storeUnalignedInt(address: Long, value: Int) {
    storeByte(address,       value.toByte)
    storeByte(address + 1L, (value >>  8).toByte)
    storeByte(address + 2L, (value >> 16).toByte)
    storeByte(address + 3L, (value >> 24).toByte)
  }
  
  final override def loadUnalignedLong(address: Long): Long = {
     (loadByte(address)      & 0xFF).toLong        |
    ((loadByte(address + 1L) & 0xFF).toLong <<  8) |
    ((loadByte(address + 2L) & 0xFF).toLong << 16) |
    ((loadByte(address + 3L) & 0xFF).toLong << 24) |
    ((loadByte(address + 4L) & 0xFF).toLong << 32) |
    ((loadByte(address + 5L) & 0xFF).toLong << 40) |
    ((loadByte(address + 6L) & 0xFF).toLong << 48) |
     (loadByte(address + 7L).toLong         << 56)
  }
  
  final override def storeUnalignedLong(address: Long, value: Long) {
    storeByte(address,       value.toByte)
    storeByte(address + 1L, (value >>  8).toByte)
    storeByte(address + 2L, (value >> 16).toByte)
    storeByte(address + 3L, (value >> 24).toByte)
    storeByte(address + 4L, (value >> 32).toByte)
    storeByte(address + 5L, (value >> 40).toByte)
    storeByte(address + 6L, (value >> 48).toByte)
    storeByte(address + 7L, (value >> 56).toByte)
  }
  
  final override def loadUnalignedChar(address: Long): Char =
    loadUnalignedShort(address).toChar
  
  final override def storeUnalignedChar(address: Long, value: Char): Unit =
    storeUnalignedShort(address, value.toShort)
  
  final override def loadUnalignedFloat(address: Long): Float =
    intBitsToFloat(loadUnalignedInt(address))
  
  final override def storeUnalignedFloat(address: Long, value: Float): Unit =
    storeUnalignedInt(address, floatToRawIntBits(value))
  
  final override def loadUnalignedDouble(address: Long): Double =
    longBitsToDouble(loadUnalignedLong(address))
  
  final override def storeUnalignedDouble(address: Long, value: Double): Unit =
    storeUnalignedLong(address, doubleToRawLongBits(value))
  
  final override def move(fromAddress: Long, toAddress: Long, size: Long) {
    val fromLimit = fromAddress + size
    val toLimit = toAddress + size
    if (fromAddress == toAddress) ()
    else if ((size & 1L) == 0L && (fromAddress & 1L) == 0L && (toAddress & 1L) == 0L)
      System.arraycopy(words, (fromAddress >> 1).toInt,
                       words, (toAddress   >> 1).toInt, (size >> 1).toInt)
    else if (fromAddress >= toAddress || fromLimit <= toAddress) {
      var p = fromAddress
      var q = toAddress
      while (q < toLimit) {
        storeByte(q, loadByte(p))
        p += 1L
        q += 1L
      }
    }
    else {
      var p = fromLimit - 1L
      var q = toLimit - 1L
      while (q >= toAddress) {
        storeByte(q, loadByte(p))
        p -= 1L
        q -= 1L
      }
    }
  }
  
  final override def clear(fromAddress: Long, untilAddress: Long) {
    if (fromAddress > untilAddress)
      throw new IllegalArgumentException("fromAddress > untilAddress")
    else if (fromAddress == untilAddress) ()
    else if ((fromAddress & 1L) != 0L) {
      storeByte(fromAddress, 0.toByte)
      clear(fromAddress + 1L, untilAddress)
    }
    else {
      java.util.Arrays.fill(words, (fromAddress  >> 1).toInt,
                                   (untilAddress >> 1).toInt, 0.toShort)
      clear(untilAddress & -2L, untilAddress)
    }
  }
  
  @inline final def toBE: Data2BE = new Data2BE(words)
  
  override def toString: String = "Data2LE"+"("+ size +")"
}

/** An allocator for little-endian data backed by a `Short` array. */
object Data2LE extends Allocator with (Long => Data2LE) {
  override def MaxSize: Long = Int.MaxValue.toLong << 1
  
  override def alloc[T](count: Long)(implicit unit: ValType[T]): Data2LE =
    apply(unit.size * count)
  
  override def apply(size: Long): Data2LE = {
    require(0L <= size && size <= MaxSize)
    val words = new Array[Short]((align(size, 2L) >> 1).toInt)
    new Data2LE(words)
  }
  
  def unapply(data: Data2LE): Some[Array[Short]] = Some(data.words)
  
  override def toString: String = "Data2LE"
}
