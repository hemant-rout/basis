/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra
package binary64

import language.existentials

/** An abstract space of ''M''x''N'' real matrices.
  * 
  * @author Chris Sachs
  * 
  * @tparam V   The row space.
  * @tparam W   The column space.
  * 
  * @define space   real matrix space
  */
trait RealMatrixSpace
    [V <: RealVectorSpace with Singleton,
     W <: RealVectorSpace with Singleton]
  extends MatrixSpace[V, W, R] {
  
  trait Element extends Any with super.Element {
    override protected def Matrix: RealMatrixSpace.this.type = RealMatrixSpace.this
    
    override def apply(k: Int): Real
    
    override def apply(i: Int, j: Int): Real = {
      if (i < 0 || i >= M || j < 0 || j >= N)
        throw new IndexOutOfBoundsException("row "+ i +", "+"col "+ j)
      apply(N * i + j)
    }
    
    override def row(i: Int): Row = {
      if (i < 0 || i >= M) throw new IndexOutOfBoundsException("row "+ i)
      val coords = new Array[Double](N)
      var j = 0
      var n = N * i
      while (j < coords.length) {
        coords(j) = this(n).value
        j += 1
        n += 1
      }
      Row(coords)
    }
    
    override def col(j: Int): Col = {
      if (j < 0 || j >= N) throw new IndexOutOfBoundsException("col "+ j)
      val coords = new Array[Double](M)
      var i = 0
      var m = j
      while (i < coords.length) {
        coords(i) = this(m).value
        i += 1
        m += N
      }
      Col(coords)
    }
    
    override def + (that: Matrix): Matrix = {
      if (M != that.M || N != that.N) throw new DimensionException
      val entries = new Array[Double](M * N)
      var k = 0
      while (k < entries.length) {
        entries(k) = this(k).value + that(k).value
        k += 1
      }
      Matrix(entries)
    }
    
    override def unary_- : Matrix = {
      val entries = new Array[Double](M * N)
      var k = 0
      while (k < entries.length) {
        entries(k) = -this(k).value
        k += 1
      }
      Matrix(entries)
    }
    
    override def - (that: Matrix): Matrix = {
      if (M != that.M || N != that.N) throw new DimensionException
      val entries = new Array[Double](M * N)
      var k = 0
      while (k < entries.length) {
        entries(k) = this(k).value - that(k).value
        k += 1
      }
      Matrix(entries)
    }
    
    override def :* (scalar: Real): Matrix = {
      val entries = new Array[Double](M * N)
      var k = 0
      while (k < entries.length) {
        entries(k) = this(k).value * scalar.value
        k += 1
      }
      Matrix(entries)
    }
    
    override def *: (scalar: Real): Matrix = this :* scalar
    
    override def :⋅ (vector: Row): Col = {
      if (N != vector.N) throw new DimensionException
      val coords = new Array[Double](M)
      var i = 0
      var i0 = 0
      while (i < coords.length) {
        var s = 0.0
        var n = i0
        var j = 0
        while (j < N) {
          s += this(n).value * vector(j).value
          n += 1
          j += 1
        }
        coords(i) = s
        i += 1
        i0 += N
      }
      Col(coords)
    }
    
    override def ⋅: (vector: Col): Row = {
      if (vector.N != M) throw new DimensionException
      val coords = new Array[Double](N)
      var j = 0
      while (j < coords.length) {
        var s = 0.0
        var n = j
        var i = 0
        while (i < M) {
          s += vector(i).value * this(n).value
          n += N
          i += 1
        }
        coords(j) = s
        j += 1
      }
     Row(coords)
    }
    
    def ⋅ [U <: RealVectorSpace with Singleton]
        (that: B#Element forSome { type B <: RealMatrixSpace[U, V] })
      : C#Matrix forSome { type C <: RealMatrixSpace[U, W] } =
      compose(that.Matrix).product(this, that)
    
    override def transpose: Transpose#Matrix = {
      val entries = new Array[Double](N * M)
      var k = 0
      var j = 0
      while (j < N) {
        var n = j
        var i = 0
        while (i < M) {
          entries(k) = this(n).value
          n += N
          k += 1
          i += 1
        }
        j += 1
      }
      Transpose(entries)
    }
    
    override def equals(other: Any): Boolean = other match {
      case that: Element =>
        var dim = M * N
        var equal = M == that.M && N == that.N
        var k = 0
        while (k < dim && equal) {
          equal = this(k).value == that(k).value
          k += 1
        }
        equal
      case _ => false
    }
    
    override def hashCode: Int = {
      import scala.util.hashing.MurmurHash3._
      var dim = M * N
      var h = -1997372447
      var k = 0
      while (k < dim) {
        h = mix(h, this(k).##)
        k += 1
      }
      finalizeHash(h, dim)
    }
  }
  
  override type Matrix <: Element
  
  override type Scalar = Real
  
  override type Transpose <: RealMatrixSpace[W, V] with Singleton {
    type Transpose = RealMatrixSpace.this.type
  }
  
  override def Transpose: Transpose
  
  override def Row: V
  
  override def Col: W
  
  override def Scalar: R = Real
  
  def apply(entries: Array[Double]): Matrix
  
  override def apply(entries: Real*): Matrix = apply(entries.map(_.toDouble).toArray[Double])
  
  override def rows(rows: Row*): Matrix = {
    if (rows.length != M) throw new DimensionException
    val entries = new Array[Double](M * N)
    var k = 0
    var i = 0
    while (i < M) {
      val row = rows(i)
      if (row.N != N) throw new DimensionException
      var j = 0
      while (j < N) {
        entries(k) = row(j)
        k += 1
        j += 1
      }
      i += 1
    }
    apply(entries)
  }
  
  override def cols(cols: Col*): Matrix = {
    if (cols.length != N) throw new DimensionException
    val entries = new Array[Double](M * N)
    var j = 0
    while (j < N) {
      val col = cols(j)
      if (col.N != M) throw new DimensionException
      var k = j
      var i = 0
      while (i < M) {
        entries(k) = col(i)
        k += N
        i += 1
      }
      j += 1
    }
    apply(entries)
  }
  
  override def zero: Matrix = apply(new Array[Double](M * N))
  
  override def compose[U <: VectorSpace[Real.type] with Singleton]
      (that: MatrixSpace[U, V, Real.type]): MatrixSpace[U, W, Real.type] = {
    if (that.isInstanceOf[RealMatrixSpace[_, V]])
      compose(that.asInstanceOf[RealMatrixSpace[U with RealVectorSpace, V]]).asInstanceOf[MatrixSpace[U, W, Real.type]]
    else super.compose[U](that)
  }
  
  /** Returns a real matrix space that maps the row space of another real
    * matrix space to this column space. */
  def compose[U <: RealVectorSpace with Singleton]
      (that: RealMatrixSpace[U, V]): RealMatrixSpace[U, W] =
    Col ⨯ that.Row
  
  /** Returns the real matrix product of the first real matrix, whose column
    * space equals this column space, times the second real matrix, whose row
    * space equals this row space, where the row space of the first matrix
    * equals the column space of the second matrix. */
  override def product[U <: VectorSpace[Real.type] with Singleton](
      matrixA: A#Element forSome { type A <: MatrixSpace[U, W, Real.type] },
      matrixB: B#Element forSome { type B <: MatrixSpace[V, U, Real.type] }): Matrix = {
    if (matrixA.isInstanceOf[Element] && matrixB.isInstanceOf[Element]) {
      val realMatrixA = matrixA.asInstanceOf[Element]
      val realMatrixB = matrixB.asInstanceOf[Element]
      val M = realMatrixA.M
      val N = realMatrixA.N
      if (N != realMatrixB.M) throw new DimensionException
      val P = realMatrixB.N
      val entries = new Array[Double](M * P)
      var k = 0
      var i = 0
      var i0 = 0
      while (i < M) {
        var j = 0
        while (j < P) {
          var s = 0.0
          var m = i0
          var n = j
          var d = 0
          while (d < N) {
            s += realMatrixA(m).value * realMatrixB(n).value
            m += 1
            n += P
            d += 1
          }
          entries(k) = s
          k += 1
          j += 1
        }
        i += 1
        i0 += N
      }
      apply(entries)
    }
    else super.product[U](matrixA, matrixB)
  }
}
