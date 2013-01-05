/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.collections

import basis.runtime._

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance
import scala.collection.TraversableOnce

/** A factory for buildable maps.
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.0
  * @group    Factories
  */
@implicitNotFound("No map factory available for ${CC}.")
trait MapFactory[+CC[_, _]] {
  type Product[A, T] = CC[A, T]
  
  implicit def Factory: this.type = this
  
  implicit def Builder[A, T](implicit A: TypeHint[A], T: TypeHint[T])
    : Builder[Any, (A, T)] { type State = CC[A, T] @uncheckedVariance }
  
  def empty[A, T](implicit A: TypeHint[A], T: TypeHint[T]): CC[A, T] =
    Builder(A, T).state
  
  def coerce[A, T](entries: Enumerator[(A, T)])(implicit A: TypeHint[A], T: TypeHint[T]): CC[A, T] =
    (Builder(A, T) ++= entries).state
  
  def coerce[A, T](entries: TraversableOnce[(A, T)])(implicit A: TypeHint[A], T: TypeHint[T]): CC[A, T] = {
    val builder = Builder(A, T)
    entries.foreach(new Accumulator.Append(builder))
    builder.state
  }
  
  def apply[A, T](entries: (A, T)*): CC[A, T] =
    macro MapFactory.apply[CC, A, T]
}

private[collections] object MapFactory {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  def apply[CC[_, _], A, T]
      (c: Context { type PrefixType <: MapFactory[CC] })
      (entries: c.Expr[(A, T)]*)
      (implicit CCTag: c.WeakTypeTag[CC[_, _]], ATag: c.WeakTypeTag[A], TTag: c.WeakTypeTag[T])
    : c.Expr[CC[A, T]] = {
    import c.{Expr, prefix, Tree, WeakTypeTag}
    import c.universe._
    var builder = TypeApply(Select(prefix.tree, "Builder"), TypeTree(ATag.tpe) :: TypeTree(TTag.tpe) :: Nil): Tree
    builder = Apply(Select(builder, "expect"), Literal(Constant(entries.length)) :: Nil)
    val xs = entries.iterator
    while (xs.hasNext) builder = Apply(Select(builder, "$plus$eq"), xs.next().tree :: Nil)
    Expr(Select(builder, "state"))(WeakTypeTag(appliedType(CCTag.tpe, ATag.tpe :: TTag.tpe :: Nil)))
  }
}
