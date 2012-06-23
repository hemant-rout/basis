/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.memory

/** Big-endian `Byte` array backed data. */
final class ByteDataBE(val array: Array[Byte]) extends AnyVal with ArrayData[Byte] {
  @inline override def size: Long = array.length.toLong
  
  @inline override def unit: Int = 1
  
  @inline override def endian = Endianness.BigEndian
  
  override def copy(size: Long): ByteDataBE = {
    require(0L <= size && size <= Int.MaxValue.toLong)
    val array = new Array[Byte](size.toInt)
    Array.copy(this.array, 0, array, 0, math.min(this.array.length, array.length))
    new ByteDataBE(array)
  }
  
  @inline override def loadByte(address: Long): Byte =
    array(address.toInt)
  
  @inline override def storeByte(address: Long, value: Byte): Unit =
    array(address.toInt) = value
  
  override def loadUnalignedShort(address: Long): Short = {
    val i = address.toInt
    ((array(i)             << 8) |
     (array(i + 1) & 0xFF)).toShort
  }
  
  override def storeUnalignedShort(address: Long, value: Short) {
    val i = address.toInt
    array(i)     = (value >> 8).toByte
    array(i + 1) =  value.toByte
  }
  
  override def loadUnalignedInt(address: Long): Int = {
    val i = address.toInt
     (array(i)             << 24) |
    ((array(i + 1) & 0xFF) << 16) |
    ((array(i + 2) & 0xFF) <<  8) |
     (array(i + 3) & 0xFF)
  }
  
  override def storeUnalignedInt(address: Long, value: Int) {
    val i = address.toInt
    array(i)     = (value >> 24).toByte
    array(i + 1) = (value >> 16).toByte
    array(i + 2) = (value >>  8).toByte
    array(i + 3) =  value.toByte
  }
  
  override def loadUnalignedLong(address: Long): Long = {
    val i = address.toInt
     (array(i).toLong             << 56) |
    ((array(i + 1) & 0xFF).toLong << 48) |
    ((array(i + 2) & 0xFF).toLong << 40) |
    ((array(i + 3) & 0xFF).toLong << 32) |
    ((array(i + 4) & 0xFF).toLong << 24) |
    ((array(i + 5) & 0xFF).toLong << 16) |
    ((array(i + 6) & 0xFF).toLong <<  8) |
     (array(i + 7) & 0xFF).toLong
  }
  
  override def storeUnalignedLong(address: Long, value: Long) {
    val i = address.toInt
    array(i)     = (value >> 56).toByte
    array(i + 1) = (value >> 48).toByte
    array(i + 2) = (value >> 40).toByte
    array(i + 3) = (value >> 32).toByte
    array(i + 4) = (value >> 24).toByte
    array(i + 5) = (value >> 16).toByte
    array(i + 6) = (value >>  8).toByte
    array(i + 7) =  value.toByte
  }
  
  override def toString: String = "ByteDataBE"+"("+ size +")"
}

/** An allocator for big-endian data backed by a `Byte` array. */
object ByteDataBE extends ArrayAllocator[Byte] {
  override def MaxSize: Long = Int.MaxValue.toLong
  
  override def alloc[T](count: Long)(implicit unit: Struct[T]): ByteDataBE =
    apply(unit.size * count)
  
  override def apply(size: Long): ByteDataBE = {
    require(0L <= size && size <= MaxSize)
    val array = new Array[Byte](size.toInt)
    new ByteDataBE(array)
  }
  
  override def wrap(array: Array[Byte]): ByteDataBE = new ByteDataBE(array)
  
  def unapply(data: ByteDataBE): Some[Array[Byte]] = Some(data.array)
  
  override def toString: String = "ByteDataBE"
}