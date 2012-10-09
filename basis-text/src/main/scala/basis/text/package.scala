/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis

package object text extends StringBuffers {
  import scala.language.implicitConversions
  
  implicit def String(string: java.lang.String): String = new JavaString(string)
}

package text {
  private[text] class DefaultStringBuffers {
    implicit def StringBuffer: StringBuffer[String] { type State = String } =
      (new UTF8Buffer).asInstanceOf[StringBuffer[String] { type State = String }]
  }
  
  private[text] class StringBuffers extends DefaultStringBuffers {
    implicit def UTF8Buffer: UTF8Buffer = new UTF8Buffer
    
    implicit def UTF16Buffer: UTF16Buffer = new UTF16Buffer
    
    implicit def UTF32Buffer: UTF32Buffer = new UTF32Buffer
  }
}