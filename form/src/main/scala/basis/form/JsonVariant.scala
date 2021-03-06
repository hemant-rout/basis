//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.form

import basis._
import basis.collections._
import basis.text._

trait JsonVariant extends Variant { variant =>
  override type AnyForm    <: JsonValue
  override type ObjectForm <: JsonObject with AnyForm
  override type SeqForm    <: JsonSeq with AnyForm
  override type SetForm    <: JsonSet with AnyForm
  override type TextForm   <: JsonText with AnyForm
  override type DataForm   <: JsonData with AnyForm
  override type NumberForm <: JsonNumber with AnyForm
  override type DateForm   <: JsonDate with AnyForm
  override type BoolForm   <: JsonBool with AnyForm
  override type NullForm   <: JsonNull with AnyForm
  override type NoForm     <: JsonNo with AnyForm

  override val AnyForm: JsonValueFactory
  override val ObjectForm: JsonObjectFactory
  override val SeqForm: JsonSeqFactory
  override val SetForm: JsonSetFactory

  def JsonObjectValue(form: ObjectForm): AnyForm = decodeJsonObject(form)
  def JsonArrayValue(form: SeqForm): AnyForm     = form
  def JsonStringValue(form: TextForm): AnyForm   = DateForm.parse(form.toUString.toString).bindOrElse(form)

  def JsonObjectBuilder: Builder[(String, AnyForm)] with State[ObjectForm] = ObjectForm.Builder
  def JsonArrayBuilder: Builder[AnyForm] with State[SeqForm]               = SeqForm.Builder
  def JsonStringBuilder: StringBuilder with State[TextForm]                = TextForm.Builder

  def JsonString(value: String): TextForm   = TextForm(value)
  def JsonNumber(value: Int): NumberForm    = NumberForm(value)
  def JsonNumber(value: Long): NumberForm   = NumberForm(value)
  def JsonNumber(value: Float): NumberForm  = NumberForm(value)
  def JsonNumber(value: Double): NumberForm = NumberForm(value)
  def JsonNumber(value: String): NumberForm = NumberForm(value)
  def JsonTrue: BoolForm                    = TrueForm
  def JsonFalse: BoolForm                   = FalseForm
  def JsonNull: NullForm                    = NullForm
  def JsonUndefined: NoForm                 = NoForm

  def JsonNew(identifier: String, arguments: SeqForm): AnyForm = NoForm

  implicit def JsonStringContext(stringContext: StringContext): JsonStringContext[variant.type] =
    macro JsonVariantMacros.JsonStringContext[variant.type]

  private[this] def decodeJsonObject(form: ObjectForm): AnyForm = {
    if (form.size == 1) {
      val field = form.iterator.head
      val value = field._2
      field._1 match {
        case "$base64" if value.isTextForm =>
          try DataForm.fromBase64(value.asTextForm.toUString.toString)
          catch { case _: IllegalArgumentException => form }
        case "$date" if value.isNumberForm =>
          DateForm(value.asNumberForm.toLong)
        case _ => form
      }
    }
    else form
  }


  trait JsonValue extends BaseValue { this: AnyForm =>
    def writeJson(builder: StringBuilder): Unit
    def toJson: String = {
      val builder = String.Builder
      writeJson(builder)
      builder.state
    }
  }

  trait JsonValueFactory extends BaseValueFactory {
    def parseJson(json: String): AnyForm = {
      val factory = new JsonVariantFactory[variant.type](variant)
      val parser = new JsonStringParser(json)
      parser.skipWhitespace()
      if (parser.isEOF) NoForm
      else {
        val value = parser.parseValue(factory)
        parser.skipWhitespace()
        parser.parseEOF()
        value
      }
    }

    def parseJsonPrefix(json: String): (AnyForm, String) = {
      val factory = new JsonVariantFactory[variant.type](variant)
      val parser = new JsonStringParser(json)
      parser.skipWhitespace()
      val value =
        try parser.parseValue(factory)
        catch { case _: JsonException => NoForm }
      parser.skipWhitespace()
      (value, parser.remaining)
    }
  }


  trait JsonObject extends JsonValue with BaseObject { this: ObjectForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      builder.append('{')
      val fields = iterator
      var field = null: (String, AnyForm)
      while (!fields.isEmpty && { field = fields.head; !field._2.isDefined })
        fields.step() // filter leading undefined fields
      if (!fields.isEmpty) {
        TextForm(field._1).writeJson(builder)
        builder.append(':')
        field._2.writeJson(builder)
        fields.step()
        while (!fields.isEmpty) {
          field = fields.head
          if (field._2.isDefined) { // filter undefined fields
            builder.append(',')
            TextForm(field._1).writeJson(builder)
            builder.append(':')
            field._2.writeJson(builder)
          }
          fields.step()
        }
      }
      builder.append('}')
    }
  }

  trait JsonObjectFactory extends BaseObjectFactory {
    def parseJson(json: String): ObjectForm = {
      val factory = new JsonVariantFactory[variant.type](variant)
      val parser = new JsonStringParser(json)
      parser.skipWhitespace()
      val value = parser.parseObject(factory)(Builder)
      parser.skipWhitespace()
      parser.parseEOF()
      value
    }
  }


  trait JsonSeq extends JsonValue with BaseSeq { this: SeqForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      builder.append('[')
      val values = iterator
      var value = null.asInstanceOf[AnyForm]
      while (!values.isEmpty && { value = values.head; !value.isDefined })
        values.step() // filter leading undefined values
      if (!values.isEmpty) {
        value.writeJson(builder)
        values.step()
        while (!values.isEmpty) {
          value = values.head
          if (value.isDefined) {
            builder.append(',')
            value.writeJson(builder)
          }
          values.step()
        }
      }
      builder.append(']')
    }
  }

  trait JsonSeqFactory extends BaseSeqFactory {
    def parseJson(json: String): SeqForm = {
      val factory = new JsonVariantFactory[variant.type](variant)
      val parser = new JsonStringParser(json)
      parser.skipWhitespace()
      val value = parser.parseArray(factory)(Builder)
      parser.skipWhitespace()
      parser.parseEOF()
      value
    }
  }


  trait JsonSet extends JsonValue with BaseSet { this: SetForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      builder.append('[')
      val values = iterator
      var value = null.asInstanceOf[AnyForm]
      while (!values.isEmpty && { value = values.head; !value.isDefined })
        values.step() // filter leading undefined values
      if (!values.isEmpty) {
        value.writeJson(builder)
        values.step()
        while (!values.isEmpty) {
          value = values.head
          if (value.isDefined) {
            builder.append(',')
            value.writeJson(builder)
          }
          values.step()
        }
      }
      builder.append(']')
    }
  }

  trait JsonSetFactory extends BaseSetFactory {
    def parseJson(json: String): SetForm = {
      val factory = new JsonVariantFactory[variant.type](variant)
      val parser = new JsonStringParser(json)
      parser.skipWhitespace()
      val value = parser.parseArray(factory)(Builder)
      parser.skipWhitespace()
      parser.parseEOF()
      value
    }
  }


  trait JsonText extends JsonValue with BaseText { this: TextForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      def hexToChar(h: Int): Int = if (h < 10) '0' + h else 'A' + (h - 10)
      builder.append('"')
      val cs = iterator
      var c = 0
      while (!cs.isEmpty) {
        val b = c
        c = cs.head
        c match {
          case '"'  => builder.append('\\'); builder.append('"')
          case '\\' => builder.append('\\'); builder.append('\\')
          case '\b' => builder.append('\\'); builder.append('b')
          case '\f' => builder.append('\\'); builder.append('f')
          case '\n' => builder.append('\\'); builder.append('n')
          case '\r' => builder.append('\\'); builder.append('r')
          case '\t' => builder.append('\\'); builder.append('t')
          case '/'  if b == '<' => builder.append('\\'); builder.append('/')
          case c    if (c >= '\u0000' && c <= '\u001F') ||
                       (c >= '\u007F' && c <= '\u009F') =>
                       builder.append('\\'); builder.append('u')
                       builder.append(hexToChar(c >>> 12 & 0xF))
                       builder.append(hexToChar(c >>>  8 & 0xF))
                       builder.append(hexToChar(c >>>  4 & 0xF))
                       builder.append(hexToChar(c        & 0xF))
          case c    => builder.append(c)
        }
        cs.step()
      }
      builder.append('"')
    }
  }


  trait JsonData extends JsonValue with BaseData { this: DataForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      builder.append('{')
      builder.append("\"$base64\"")
      builder.append(':')
      builder.append('"')
      this.writeBase64(builder)
      builder.append('"')
      builder.append('}')
    }
  }


  trait JsonNumber extends JsonValue with BaseNumber { this: NumberForm =>
    override def writeJson(builder: StringBuilder): Unit = builder.append(toJson)
    override def toJson: String                          = if (!isNaN && !isInfinite) toDecimalString else "null"
  }


  trait JsonDate extends JsonValue with BaseDate { this: DateForm =>
    override def writeJson(builder: StringBuilder): Unit = {
      builder.append('"')
      writeISO8601(builder)
      builder.append('"')
    }
  }


  trait JsonBool extends JsonValue with BaseBool { this: BoolForm =>
    override def writeJson(builder: StringBuilder): Unit = builder.append(toJson)
    override def toJson: String                          = if (toBoolean) "true" else "false"
  }


  trait JsonNull extends JsonValue with BaseNull { this: NullForm =>
    override def writeJson(builder: StringBuilder): Unit = builder.append(toJson)
    override def toJson: String                          = "null"
  }


  trait JsonNo extends JsonValue with BaseNo { this: NoForm =>
    override def writeJson(builder: StringBuilder): Unit = builder.append(toJson)
    override def toJson: String                          = "undefined"
  }
}
