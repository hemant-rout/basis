//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis

/** Low-level memory model.
  *
  * @contentDiagram hideNodes "basis.data.ByteOrder" "basis.data.DataFactory"
  */
package object data {
  val BigEndian: BigEndian = new BigEndian
  val LittleEndian: LittleEndian = new LittleEndian

  type NativeEndian = NativeEndian.type

  /** The native byte order of the virtual machine. */
  implicit val NativeEndian: Endianness = {
    if (BigEndian.isNative) BigEndian
    else if (LittleEndian.isNative) LittleEndian
    else throw new AssertionError
  }

  implicit def DataFactoryToOps[Data](factory: DataFactory[Data]): DataFactoryOps[Data] = macro DataMacros.DataFactoryToOps[Data]
  implicit def AllocatorToOps[Data](allocator: Allocator[Data]): AllocatorOps[Data] = macro DataMacros.AllocatorToOps[Data]

  implicit def LoaderToOps(data: Loader): LoaderOps[data.Family] = macro DataMacros.LoaderToOps
  implicit def ReaderToOps(data: Reader): ReaderOps = macro DataMacros.ReaderToOps
  implicit def StorerToOps(data: Storer): StorerOps = macro DataMacros.StorerToOps
  implicit def WriterToOps(data: Writer): WriterOps = macro DataMacros.WriterToOps

  /** Returns an address aligned to a power-of-two alignment.
    *
    * @param  base        the address to align.
    * @param  alignment   the required alignment.
    * @return the aligned address.
    */
  def align(base: Long, alignment: Long): Long = (base + (alignment - 1L)) & ~(alignment - 1L)
}
