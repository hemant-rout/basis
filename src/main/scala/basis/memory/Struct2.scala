/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.memory

import scala.math.max

/** A composite value type with two fields. The value types of the columns of
  * the structure parameterize the class, along with the modeled instance type.
  * The class constructor computes the structure's memory layout and makes
  * available to subclasses the calculated alignment, size, field offsets, and
  * field projections. Subclasses use this information to load and store values.
  * 
  * @author Chris Sachs
  * 
  * @example {{{
  * class Vector2(val x: Double, val y: Double)
  * 
  * // a basic struct implementation.
  * object Vector2 extends Struct2[PaddedDouble, PaddedDouble, Vector2] {
  *   override def load(data: Data, address: Long): Vector2 = {
  *     // specialized loads from field projections.
  *     val x = field1.load(data, address)
  *     val y = field2.load(data, address)
  *     new Vector2(x, y)
  *   }
  *   
  *   override def store(data: Data, address: Long, vector: Vector2) {
  *     // specialized stores to field projections.
  *     field1.store(data, address, vector.x)
  *     field2.store(data, address, vector.y)
  *   }
  * }
  * 
  * // a more advanced struct implementation.
  * class Vector2Struct(frameOffset: Int, frameSize: Int, frameAlignment: Int)
  *   extends Struct2[PaddedDouble, PaddedDouble, Vector2](
  *                   frameOffset, frameSize, frameAlignment)
  *     with Framed[Vector2Struct] {
  *   
  *   def this() = this(0, 0, 0) // initializes a minimal frame.
  *   
  *   // name and expose the field projections.
  *   def x: Field1 = field1
  *   def y: Field2 = field2
  *   
  *   override def load(data: Data, address: Long): Vector2 = {
  *     val x = data.loadDouble(address + offset1)
  *     val y = data.loadDouble(address + offset2)
  *     new Vector2(x, y)
  *   }
  *   
  *   override def store(data: Data, address: Long, vector: Vector2) {
  *     data.storeDouble(address + offset1, vector.x)
  *     data.storeDouble(address + offset2, vector.y)
  *   }
  *   
  *   // this type acts as its own frame, making its use as a member
  *   // of another structure more efficient.
  *   override def framed(offset: Int, size: Int, alignment: Int): Frame =
  *     new Vector2Struct(offset, size, alignment)
  * }
  * }}}
  * 
  * @tparam F1  the value type of the first column.
  * @tparam F2  the value type of the second column.
  * @tparam T   the modeled instance type.
  */
abstract class Struct2[F1 <: Framed[F1], F2 <: Framed[F2], T] private (
    column1: F1, column2: F2,
    frameOffset: Int, frameSize: Int, frameAlignment: Int)
  extends ValType[T] {
  
  /** Constructs a value type with a specified frame.
    * 
    * @param  frameOffset     the preferred offset of the first column in the type's frame.
    * @param  frameSize       the preferred size of the type's frame.
    * @param  frameAlignment  the preferred alignment of the type's frame.
    * @param  column1         the implicit first column type.
    * @param  column2         the implicit second column type.
    */
  def this(frameOffset: Int, frameSize: Int, frameAlignment: Int)
          (implicit column1: F1, column2: F2) =
    this(column1, column2, frameOffset, frameSize, frameAlignment)
  
  /** Constructs a value type with a minimal frame.
    * 
    * @param  column1   the implicit first column type.
    * @param  column2   the implicit second column type.
    */
  def this()(implicit column1: F1, column2: F2) =
    this(column1, column2, 0, 0, 0)
  
  /** The frame type of the first column. */
  protected type Field1 = F1
  
  /** The frame type of the second column. */
  protected type Field2 = F2
  
  /** Returns the offset of the first column in this type's frame. */
  protected val offset1: Int = align(column1.alignment)(frameOffset)
  
  /** Returns the offset of the second column in this type's frame. */
  protected val offset2: Int = align(column2.alignment)(offset1 + column1.size)
  
  override def offset: Int = offset1
  
  override val alignment: Int = {
    val minimalAlignment = max(column1.alignment, column2.alignment)
    align(minimalAlignment)(max(minimalAlignment, frameAlignment))
  }
  
  override val size: Int = {
    val minimalSize = align(alignment)(offset2 + column2.size)
    align(alignment)(max(minimalSize, frameSize))
  }
  
  /** Returns the projection of the first column into this type's frame. */
  protected val field1: Field1 = column1.framed(offset1, size, alignment)
  
  /** Returns the projection of the second column into this type's frame. */
  protected val field2: Field2 = column2.framed(offset2, size, alignment)
}
