//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.data

import basis.text._
import scala.reflect.macros._

final class LoaderOps(val __ : Loader) extends AnyVal {
  def load[T](address: Long)(implicit T: Struct[T]): T                                                    = macro LoaderMacros.load[T]
  def loadArray[T](address: Long, count: Int)(implicit T: Struct[T]): Array[T]                            = macro LoaderMacros.loadArray[T]
  def loadToArray[T](address: Long, array: Array[T], start: Int, count: Int)(implicit T: Struct[T]): Unit = macro LoaderMacros.loadToArray[T]

  def writeBase64(builder: StringBuilder): Unit = {
    def encodeDigit(digit: Int): Int = {
      if      (digit >=  0 && digit < 26) digit + ('A'     )
      else if (digit >= 26 && digit < 52) digit + ('a' - 26)
      else if (digit >= 52 && digit < 62) digit + ('0' - 52)
      else if (digit == 62) '+'
      else if (digit == 63) '/'
      else throw new MatchError(digit.toString)
    }
    var i = 0L
    val n = __.size
    while (i + 2L < n) {
      val x: Int = __.loadByte(i     ) & 0xFF
      val y: Int = __.loadByte(i + 1L) & 0xFF
      val z: Int = __.loadByte(i + 2L) & 0xFF
      builder.append(encodeDigit(x >>> 2))
      builder.append(encodeDigit(((x << 4) | (y >>> 4)) & 0x3F))
      builder.append(encodeDigit(((y << 2) | (z >>> 6)) & 0x3F))
      builder.append(encodeDigit(z & 0x3F))
      i += 3L
    }
    if (i + 1L < n) {
      val x = __.loadByte(i     ) & 0xFF
      val y = __.loadByte(i + 1L) & 0xFF
      builder.append(encodeDigit(x >>> 2))
      builder.append(encodeDigit(((x << 4) | (y >>> 4)) & 0x3F))
      builder.append(encodeDigit((y << 2) & 0x3F))
      builder.append('=')
      i += 2L
    }
    else if (i < n) {
      val x = __.loadByte(i) & 0xFF
      builder.append(encodeDigit(x >>> 2))
      builder.append(encodeDigit((x << 4) & 0x3F))
      builder.append('=')
      builder.append('=')
      i += 1L
    }
  }

  def toBase64: String = {
    val s = UString.Builder
    writeBase64(s)
    s.state.toString
  }
}

private[data] class LoaderMacros(val c: blackbox.Context { type PrefixType <: LoaderOps }) {
  import c.{ Expr, prefix, WeakTypeTag }
  import c.universe._

  def load[T](address: Expr[Long])(T: Expr[Struct[T]])(implicit TTag: WeakTypeTag[T]): Expr[T] = Expr[T](q"$T.load($prefix.__, $address)")

  def loadArray[T](address: Expr[Long], count: Expr[Int])(T: Expr[Struct[T]])(implicit TTag: WeakTypeTag[T]): Expr[Array[T]] = Expr[Array[T]](q"""{
    val data = $prefix.__
    val T = $T
    val xs = new Array[$TTag]($count)
    var p = $address
    var i = 0
    while (i < count) {
      xs(i) = T.load(data, p)
      p += T.size
      i += 1
    }
    xs
  }""")

  def loadToArray[T](address: Expr[Long], array: Expr[Array[T]], start: Expr[Int], count: Expr[Int])(T: Expr[Struct[T]]): Expr[Unit] = Expr[Unit](q"""{
    val data = $prefix.__
    val T = $T
    val xs = $array
    var p = $address
    var i = $start
    val n = i + $count
    while (i < n) {
      xs(i) = T.load(data, p)
      p += T.size
      i += 1
    }
  }""")

  implicit protected def ArrayTag[A](implicit A: WeakTypeTag[A]): WeakTypeTag[Array[A]] =
    WeakTypeTag(appliedType(definitions.ArrayClass.toTypeConstructor, A.tpe :: Nil))
}
