/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.sequential

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class ListSpec
  extends FunSpec
    with ShouldMatchers
    with SeqFactoryBehaviors
    with StackBehaviors
    with sequential.GeneralStackOpsBehaviors
    with sequential.StrictStackOpsBehaviors {
  
  override def suiteName = "List specification"
  
  it should behave like GenericSeqFactory(List)
  
  it should behave like TraversableCollection(List)
  it should behave like TraversableContainer(List)
  it should behave like TraversableSeq(List)
  it should behave like TraversableStack(List)
  
  it should behave like SequentialGeneralCollectionOps(List)
  it should behave like SequentialGeneralContainerOps(List)
  it should behave like SequentialGeneralSeqOps(List)
  it should behave like SequentialGeneralStackOps(List)
  
  it should behave like SequentialStrictCollectionOps(List)
  it should behave like SequentialStrictContainerOps(List)
  it should behave like SequentialStrictSeqOps(List)
  it should behave like SequentialStrictStackOps(List)
}