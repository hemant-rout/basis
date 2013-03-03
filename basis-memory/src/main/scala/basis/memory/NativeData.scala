/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.memory

private[memory] abstract class NativeData(
		private[memory] final val base: Long,
		final override val size: Long)
	extends Data {
	
	@native override def unit: Int
	
	override def endian: Endianness = NativeEndian
	
	override def isCoherent: Boolean = true
	
	@native override def loadByte(address: Long): Byte
	
	@native override def storeByte(address: Long, value: Byte): Unit
	
	@native override def loadShort(address: Long): Short
	
	@native override def storeShort(address: Long, value: Short): Unit
	
	@native override def loadInt(address: Long): Int
	
	@native override def storeInt(address: Long, value: Int): Unit
	
	@native override def loadLong(address: Long): Long
	
	@native override def storeLong(address: Long, value: Long): Unit
	
	@native override def loadFloat(address: Long): Float
	
	@native override def storeFloat(address: Long, value: Float): Unit
	
	@native override def loadDouble(address: Long): Double
	
	@native override def storeDouble(address: Long, value: Double): Unit
}