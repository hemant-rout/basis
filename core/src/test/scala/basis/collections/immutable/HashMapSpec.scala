//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.collections
package immutable

import org.scalatest._

class HashMapSpec extends FunSpec with SubMapBehaviors {
  override def suiteName = "HashMap specification"

  it should behave like GenericMap(HashMap)
  it should behave like GenericSubMap(HashMap)
}
