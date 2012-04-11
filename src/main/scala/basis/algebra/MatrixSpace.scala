/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait MatrixSpace extends VectorSpace with MatrixModule { self =>
  def Transpose: MatrixSpace {
    type Matrix = self.Transpose
    type Transpose = self.Matrix
    type ColumnVector = self.RowVector
    type RowVector = self.ColumnVector
    type Scalar = self.Scalar
  }
  
  def Column: CoordinateSpace {
    type Vector = self.ColumnVector
    type Scalar = self.Scalar
  }
  
  def Row: CoordinateSpace {
    type Vector = self.RowVector
    type Scalar = self.Scalar
  }
}
