/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis

/** An equality relation with an associated hash function. */
@scala.annotation.implicitNotFound("${T} has no implicit Hash implementation.")
trait Hash[-T] extends Equal[T] {
  /** Returns the hash code of the given value. */
  def hash(x: T): Int
}

/** Hash function utilities. Implements Austin Appleby's MurmurHash 3 algorithm,
  * specifically MurmurHash3_x86_32 revision 136.
  * 
  * @example {{{
  * import Hash._
  * mash(mix(mix(mix(seed, x.##), y.##), z.##))
  * }}}
  * 
  * @see    [[http://code.google.com/p/smhasher/]]
  */
object Hash {
  import java.lang.Integer.{ rotateLeft => rotl }
  
  /** Returns the given type's implicit `Hash` implementation. */
  def apply[T](implicit T: Hash[T]): T.type = T
  
  /** Returns the hash code mixed with the new hash value. */
  def mix(code: Int, value: Int): Int = {
    var h = code
    var k = value
    
    k *= 0xcc9e2d51
    k = rotl(k, 15)
    k *= 0x1b873593
    
    h ^= k
    
    h = rotl(h, 13)
    h = h * 5 + 0xe6546b64
    
    h
  }
  
  /** Returns the finalized hash code. */
  def mash(code: Int): Int = {
    var h = code
    
    h ^= h >>> 16
    h *= 0x85ebca6b
    h ^= h >>> 13
    h *= 0xc2b2ae35
    h ^= h >>> 16
    
    h
  }
}
