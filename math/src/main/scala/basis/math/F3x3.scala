//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.math

/** An asbtract 3 by 3 matrix space over a field.
  *
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.0
  * @group    MatrixSpaces
  */
trait F3x3 extends Ring with FMxN { F3x3 =>
  /** The type of elements in this $space; equivalent to the type of matrices. */
  override type Element = Matrix

  override type Matrix <: MatrixF3x3

  override val Transpose: F3x3 {
    val Row: F3x3.Col.type
    val Col: F3x3.Row.type
    val Scalar: F3x3.Scalar.type
  }

  override val Row: F3 { val Scalar: F3x3.Scalar.type }
  override val Col: F3 { val Scalar: F3x3.Scalar.type }

  override val Scalar: Field

  override def dim: Int = 9

  override def zero: Matrix = {
    val z = Scalar.zero
    apply(z, z, z,  z, z, z,  z, z, z)
  }

  override def unit: Matrix = {
    val z = Scalar.zero
    val u = Scalar.unit
    apply(u, z, z,  z, u, z,  z, z, u)
  }

  def apply(
      _1_1: Scalar, _1_2: Scalar, _1_3: Scalar,
      _2_1: Scalar, _2_2: Scalar, _2_3: Scalar,
      _3_1: Scalar, _3_2: Scalar, _3_3: Scalar): Matrix

  override def apply(entries: Array[Scalar]): Matrix = {
    if (entries.length != 9) throw new DimensionException
    apply(entries(0), entries(1), entries(2),
          entries(3), entries(4), entries(5),
          entries(6), entries(7), entries(8))
  }

  def rows(row1: Row, row2: Row, row3: Row): Matrix =
    apply(row1.x, row1.y, row1.z,
          row2.x, row2.y, row2.z,
          row3.x, row3.y, row3.z)

  override def rows(rows: Row*): Matrix = {
    if (rows.length != 3) throw new DimensionException
    this.rows(rows(0), rows(1), rows(2))
  }

  def cols(col1: Col, col2: Col, col3: Col): Matrix =
    apply(col1.x, col2.x, col3.x,
          col1.y, col2.y, col3.y,
          col1.z, col2.z, col3.z)

  override def cols(cols: Col*): Matrix = {
    if (cols.length != 3) throw new DimensionException
    this.cols(cols(0), cols(1), cols(2))
  }

  trait MatrixF3x3 extends Any with RingElement with MatrixFMxN {
    override def Row: F3x3.Row.type = F3x3.Row
    override def Col: F3x3.Col.type = F3x3.Col

    def _1_1: Scalar
    def _1_2: Scalar
    def _1_3: Scalar
    def _2_1: Scalar
    def _2_2: Scalar
    def _2_3: Scalar
    def _3_1: Scalar
    def _3_2: Scalar
    def _3_3: Scalar

    override def apply(k: Int): Scalar = k match {
      case 0 => _1_1
      case 1 => _1_2
      case 2 => _1_3
      case 3 => _2_1
      case 4 => _2_2
      case 5 => _2_3
      case 6 => _3_1
      case 7 => _3_2
      case 8 => _3_3
      case _ => throw new IndexOutOfBoundsException(k.toString)
    }

    override def apply(i: Int, j: Int): Scalar = {
      if (i < 0 || i >= 3 || j < 0 || j >= 3)
        throw new IndexOutOfBoundsException("row "+ i +", "+"col "+ j)
      apply(3 * i + j)
    }

    def row1: Row = Row(_1_1, _1_2, _1_3)
    def row2: Row = Row(_2_1, _2_2, _2_3)
    def row3: Row = Row(_3_1, _3_2, _3_3)

    override def row(i: Int): Row = i match {
      case 0 => row1
      case 1 => row2
      case 2 => row3
      case _ => throw new IndexOutOfBoundsException("row "+ i)
    }

    def col1: Col = Col(_1_1, _2_1, _3_1)
    def col2: Col = Col(_1_2, _2_2, _3_2)
    def col3: Col = Col(_1_3, _2_3, _3_3)

    override def col(j: Int): Col = j match {
      case 0 => col1
      case 1 => col2
      case 2 => col3
      case _ => throw new IndexOutOfBoundsException("col "+ j)
    }

    override def + (that: Matrix): Matrix =
      F3x3(_1_1 + that._1_1, _1_2 + that._1_2, _1_3 + that._1_3,
           _2_1 + that._2_1, _2_2 + that._2_2, _2_3 + that._2_3,
           _3_1 + that._3_1, _3_2 + that._3_2, _3_3 + that._3_3)

    override def unary_- : Matrix =
      F3x3(-_1_1, -_1_2, -_1_3,
           -_2_1, -_2_2, -_2_3,
           -_3_1, -_3_2, -_3_3)

    override def - (that: Matrix): Matrix =
      F3x3(_1_1 - that._1_1, _1_2 - that._1_2, _1_3 - that._1_3,
           _2_1 - that._2_1, _2_2 - that._2_2, _2_3 - that._2_3,
           _3_1 - that._3_1, _3_2 - that._3_2, _3_3 - that._3_3)

    override def :* (scalar: Scalar): Matrix =
      F3x3(_1_1 * scalar, _1_2 * scalar, _1_3 * scalar,
           _2_1 * scalar, _2_2 * scalar, _2_3 * scalar,
           _3_1 * scalar, _3_2 * scalar, _3_3 * scalar)

    override def *: (scalar: Scalar): Matrix =
      F3x3(scalar * _1_1, scalar * _1_2, scalar * _1_3,
           scalar * _2_1, scalar * _2_2, scalar * _2_3,
           scalar * _3_1, scalar * _3_2, scalar * _3_3)

    override def ∘ (that: Matrix): Matrix =
      F3x3(_1_1 * that._1_1, _1_2 * that._1_2, _1_3 * that._1_3,
           _2_1 * that._2_1, _2_2 * that._2_2, _2_3 * that._2_3,
           _3_1 * that._3_1, _3_2 * that._3_2, _3_3 * that._3_3)

    override def :⋅ (vector: Row): Col =
      Col(_1_1 * vector.x + _1_2 * vector.y + _1_3 * vector.z,
          _2_1 * vector.x + _2_2 * vector.y + _2_3 * vector.z,
          _3_1 * vector.x + _3_2 * vector.y + _3_3 * vector.z)

    override def ⋅: (vector: Col): Row =
      Row(vector.x * _1_1 + vector.y * _2_1 + vector.z * _3_1,
          vector.x * _1_2 + vector.y * _2_2 + vector.z * _3_2,
          vector.x * _1_3 + vector.y * _2_3 + vector.z * _3_3)

    override def * (that: Matrix): Matrix =
      F3x3(_1_1 * that._1_1 + _1_2 * that._2_1 + _1_3 * that._3_1,
           _1_1 * that._1_2 + _1_2 * that._2_2 + _1_3 * that._3_2,
           _1_1 * that._1_3 + _1_2 * that._2_3 + _1_3 * that._3_3,
           _2_1 * that._1_1 + _2_2 * that._2_1 + _2_3 * that._3_1,
           _2_1 * that._1_2 + _2_2 * that._2_2 + _2_3 * that._3_2,
           _2_1 * that._1_3 + _2_2 * that._2_3 + _2_3 * that._3_3,
           _3_1 * that._1_1 + _3_2 * that._2_1 + _3_3 * that._3_1,
           _3_1 * that._1_2 + _3_2 * that._2_2 + _3_3 * that._3_2,
           _3_1 * that._1_3 + _3_2 * that._2_3 + _3_3 * that._3_3)

    override def inverse: Matrix = {
      val minor_1_1 = _2_2 * _3_3 - _2_3 * _3_2
      val minor_1_2 = _2_1 * _3_3 - _2_3 * _3_1
      val minor_1_3 = _2_1 * _3_2 - _2_2 * _3_1
      val minor_2_1 = _1_2 * _3_3 - _1_3 * _3_2
      val minor_2_2 = _1_1 * _3_3 - _1_3 * _3_1
      val minor_2_3 = _1_1 * _3_2 - _1_2 * _3_1
      val minor_3_1 = _1_2 * _2_3 - _1_3 * _2_2
      val minor_3_2 = _1_1 * _2_3 - _1_3 * _2_1
      val minor_3_3 = _1_1 * _2_2 - _1_2 * _2_1

      val det = _1_1 * minor_1_1 - _1_2 * minor_1_2 + _1_3 * minor_1_3
      F3x3( minor_1_1 / det, -minor_2_1 / det,  minor_3_1 / det,
           -minor_1_2 / det,  minor_2_2 / det, -minor_3_2 / det,
            minor_1_3 / det, -minor_2_3 / det,  minor_3_3 / det)
    }

    override def transpose: Transpose =
      Transpose(
        _1_1, _2_1, _3_1,
        _1_2, _2_2, _3_2,
        _1_3, _2_3, _3_3)

    override def det: Scalar = {
      val minor_1_1 = _2_2 * _3_3 - _2_3 * _3_2
      val minor_1_2 = _2_1 * _3_3 - _2_3 * _3_1
      val minor_1_3 = _2_1 * _3_2 - _2_2 * _3_1

      _1_1 * minor_1_1 - _1_2 * minor_1_2 + _1_3 * minor_1_3
    }

    override def trace: Scalar =
      _1_1 + _2_2 + _3_3
  }
}
