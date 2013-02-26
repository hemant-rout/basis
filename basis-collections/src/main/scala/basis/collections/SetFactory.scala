/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.collections

import scala.annotation.implicitNotFound

/** A factory for buildable sets.
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.0
  * @group    Factories
  */
@implicitNotFound("No set factory available for ${CC}.")
trait SetFactory[+CC[_], -Hint[_]] extends BuilderFactory[CC, Hint]
