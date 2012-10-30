/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.data._
import basis.util._

/** An indexed sequence of elements.
  * 
  * @author Chris Sachs
  * 
  * @define collection  array
  */
trait Array[+A] extends Any with IndexedSeq[A] {
  override type Self <: Array[A]
  
  override def equals(other: Any): Boolean = other match {
    case that: Array[A] =>
      var i = 0
      val n = length
      var e = n == that.length
      while (e && i < n) {
        e = this(i) == that(i)
        i += 1
      }
      e
    case _ => false
  }
  
  override def hashCode: Int = {
    import MurmurHash3._
    var h = 63537721
    var i = 0
    val n = length
    while (i < n) {
      h = mix(h, this(i).##)
      i += 1
    }
    mash(h)
  }
  
  override def toString: String = {
    val s = new java.lang.StringBuilder(stringPrefix)
    s.append('(')
    if (!isEmpty) {
      s.append(this(0))
      var i = 1
      val n = length
      while (i < length) {
        s.append(", ").append(this(i))
        i += 1
      }
    }
    s.append(')')
    s.toString
  }
  
  protected def stringPrefix: String = "Array"
}

object Array extends AllArrayBuilders with SeqFactory[Array] {
  def Builder[A](implicit typeA: MemType[A]): Buffer[Any, A] { type State = Array[A] } = {
    import ValType._
    (typeA match {
      case typeA: RefType[A]           => new RefArray.Builder[A]
      case PackedByte                  => new ByteArray.Builder
      case PackedShort  | PaddedShort  => new ShortArray.Builder
      case PackedInt    | PaddedInt    => new IntArray.Builder
      case PackedLong   | PaddedLong   => new LongArray.Builder
      case PackedFloat  | PaddedFloat  => new FloatArray.Builder
      case PackedDouble | PaddedDouble => new DoubleArray.Builder
      case PackedBoolean               => new BitArray.Builder
      case typeA: ValType[A]           => new ValArray.Builder[A]()(typeA)
    }).asInstanceOf[Buffer[Any, A] { type State = Array[A] }]
  }
}

private[containers] class RefArrayBuilders {
  implicit def RefBuffer[A]: RefArray.Builder[A] = new RefArray.Builder[A]
}

private[containers] class ValArrayBuilders extends RefArrayBuilders {
  implicit def ValBuffer[A : ValType]: ValArray.Builder[A] = new ValArray.Builder[A]
}

private[containers] class AllArrayBuilders extends ValArrayBuilders {
  implicit def ByteBuffer: ByteArray.Builder = new ByteArray.Builder
  implicit def ShortBuffer: ShortArray.Builder = new ShortArray.Builder
  implicit def IntBuffer: IntArray.Builder = new IntArray.Builder
  implicit def LongBuffer: LongArray.Builder = new LongArray.Builder
  implicit def FloatBuffer: FloatArray.Builder = new FloatArray.Builder
  implicit def DoubleBuffer: DoubleArray.Builder = new DoubleArray.Builder
  implicit def BitBuffer: BitArray.Builder = new BitArray.Builder
}
