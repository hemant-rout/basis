/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.sequential

import basis.collections._

/** Implicit conversions that add general operations to collections.
  * 
  * @author   Chris Sachs
  * @version  0.0
  * @since    0.0
  * @group    General
  * 
  * @groupname  General   General collection extensions
  * @groupprio  General   1
  */
class General {
  /** Implicitly provides general operations for enumerators.
    * @group General */
  implicit def GeneralEnumeratorOps[A](these: Enumerator[A]): GeneralEnumeratorOps[A] =
    macro General.GeneralEnumeratorOps[A]
  
  /** Implicitly provides general operations for iterators.
    * @group General */
  implicit def GeneralIteratorOps[A](these: Iterator[A]): GeneralIteratorOps[A] =
    macro General.GeneralIteratorOps[A]
  
  /** Implicitly provides general operations for collections.
    * @group General */
  implicit def GeneralCollectionOps[A](these: Collection[A]): GeneralCollectionOps[A] =
    macro General.GeneralCollectionOps[A]
  
  /** Implicitly provides general operations for containers.
    * @group General */
  implicit def GeneralContainerOps[A](these: Container[A]): GeneralContainerOps[A] =
    macro General.GeneralContainerOps[A]
  
  /** Implicitly provides general operations for sequences.
    * @group General */
  implicit def GeneralSeqOps[A](these: Seq[A]): GeneralSeqOps[A] =
    macro General.GeneralSeqOps[A]
  
  /** Implicitly provides general operations for indexes.
    * @group General */
  implicit def GeneralIndexOps[A](these: Index[A]): GeneralIndexOps[A] =
    macro General.GeneralIndexOps[A]
  
  /** Implicitly provides general operations for stacks.
    * @group General */
  implicit def GeneralStackOps[A](these: Stack[A]): GeneralStackOps[A] =
    macro General.GeneralStackOps[A]
  
  /** Implicitly provides general operations for sets.
    * @group General */
  implicit def GeneralSetOps[A](these: Set[A]): GeneralSetOps[A] =
    macro General.GeneralSetOps[A]
  
  /** Implicitly provides general operations for maps.
    * @group General */
  implicit def GeneralMapOps[A, T](these: Map[A, T]): GeneralMapOps[A, T] =
    macro General.GeneralMapOps[A, T]
}

private[sequential] object General {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  
  def GeneralEnumeratorOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Enumerator[A]])
    : c.Expr[GeneralEnumeratorOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralEnumeratorOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralEnumeratorOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralEnumeratorOpsType, these.tree))(WeakTypeTag(GeneralEnumeratorOpsType))
  }
  
  def GeneralIteratorOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Iterator[A]])
    : c.Expr[GeneralIteratorOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralIteratorOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralIteratorOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralIteratorOpsType, these.tree))(WeakTypeTag(GeneralIteratorOpsType))
  }
  
  def GeneralCollectionOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Collection[A]])
    : c.Expr[GeneralCollectionOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralCollectionOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralCollectionOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralCollectionOpsType, these.tree))(WeakTypeTag(GeneralCollectionOpsType))
  }
  
  def GeneralContainerOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Container[A]])
    : c.Expr[GeneralContainerOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralContainerOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralContainerOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralContainerOpsType, these.tree))(WeakTypeTag(GeneralContainerOpsType))
  }
  
  def GeneralSeqOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Seq[A]])
    : c.Expr[GeneralSeqOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralSeqOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralSeqOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralSeqOpsType, these.tree))(WeakTypeTag(GeneralSeqOpsType))
  }
  
  def GeneralIndexOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Index[A]])
    : c.Expr[GeneralIndexOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralIndexOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralIndexOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralIndexOpsType, these.tree))(WeakTypeTag(GeneralIndexOpsType))
  }
  
  def GeneralStackOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Stack[A]])
    : c.Expr[GeneralStackOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralStackOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralStackOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralStackOpsType, these.tree))(WeakTypeTag(GeneralStackOpsType))
  }
  
  def GeneralSetOps[A : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Set[A]])
    : c.Expr[GeneralSetOps[A]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralSetOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralSetOps").toType,
        weakTypeOf[A] :: Nil)
    Expr(New(GeneralSetOpsType, these.tree))(WeakTypeTag(GeneralSetOpsType))
  }
  
  def GeneralMapOps[A : c.WeakTypeTag, T : c.WeakTypeTag]
      (c: Context)
      (these: c.Expr[Map[A, T]])
    : c.Expr[GeneralMapOps[A, T]] = {
    import c.{Expr, mirror, weakTypeOf, WeakTypeTag}
    import c.universe._
    val GeneralMapOpsType =
      appliedType(
        mirror.staticClass("basis.sequential.GeneralMapOps").toType,
        weakTypeOf[A] :: weakTypeOf[T] :: Nil)
    Expr(New(GeneralMapOpsType, these.tree))(WeakTypeTag(GeneralMapOpsType))
  }
}