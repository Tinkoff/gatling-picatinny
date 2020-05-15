package ru.tinkoff.gatling.templates

object Syntax {
  case class Field(name: String, fieldVal: FieldVal)

  sealed trait FieldVal

  case class RawValString(value: String) extends FieldVal
  case class RawValGen[T](value: T)      extends FieldVal

  case class InterpolateStrVal(interpolatorName: String) extends FieldVal
  case class InterpolateGenVal[T](interpolatorName: T)   extends FieldVal

  case class MultiVal(f: List[Field])       extends FieldVal
  case class ArrayVal(vals: List[FieldVal]) extends FieldVal

  implicit def strToField(str: String): Field = Field(str, InterpolateStrVal(str))

  implicit class FieldValOps(val fieldName: String) extends AnyVal {
    def -(str: String): Field = Field(fieldName, RawValString(str))

    def -[T](v: T): Field = Field(fieldName, RawValGen(v))

    def -(fs: Field*): Field = Field(fieldName, MultiVal(fs.toList))

    def asSessionVar(str: String): Field = Field(fieldName, InterpolateStrVal(str))

    def ~(str: String): Field = Field(fieldName, InterpolateStrVal(str))

    def asSessionVar[T](t: T): Field = Field(fieldName, InterpolateGenVal(t))

    def ~[T](t: T): Field = Field(fieldName, InterpolateGenVal(t))

    def array[T](ts: Seq[T]): Field =
      Field(fieldName, ArrayVal(ts.map {
        case s: String => RawValString(s)
        case o         => RawValGen(o)
      }.toList))

    def >[T](ts: T*): Field = array(ts)

  }

  def makeJson(fields: List[Field]): String =
    fields.map {
      case Field(name, RawValString(s))       => s""""$name": "$s""""
      case Field(name, RawValGen(s))          => s""""$name": $s"""
      case Field(name, InterpolateStrVal(in)) => s""""$name": "$${$in}""""
      case Field(name, InterpolateGenVal(in)) => s""""$name": $${$in}"""
      case Field(name, MultiVal(f))           => s""""$name": ${makeJson(f)}"""
      case Field(name, ArrayVal(vs))          => s""""$name": ${makeArrJson(vs)}"""
    }.mkString("{", ",", "}")

  def makeArrJson(vals: List[FieldVal]): String =
    vals.map {
      case RawValString(s) => s""""$s""""

      case RawValGen(f @ Field(_, _)) => s"""${makeJson(List(f))}"""

      case RawValGen(s)          => s"""$s"""
      case InterpolateStrVal(in) => s""""$${$in}""""
      case InterpolateGenVal(in) => s"""$${$in}"""
      case MultiVal(f)           => s"""${makeJson(f)}"""
      case ArrayVal(vs)          => s"""${makeArrJson(vs)}"""
    }.mkString("[", ",", "]")

  def makeXml(fields: List[Field]): String =
    fields
      .foldLeft(new StringBuilder) {
        case (sb, Field(name, RawValString(s)))       => sb.append(s"""<$name>$s</$name>""")
        case (sb, Field(name, RawValGen(s)))          => sb.append(s"""<$name>$s</$name>""")
        case (sb, Field(name, InterpolateStrVal(in))) => sb.append(s"""<$name>$${$in}</$name>""")
        case (sb, Field(name, InterpolateGenVal(in))) => sb.append(s"""<$name>$${$in}</$name>""")
        case (sb, Field(name, MultiVal(f)))           => sb.append(s"""<$name>${makeXml(f)}</$name>""")
      }
      .mkString

  def makeXml(fs: Field*): String = makeXml(fs.toList)

  def makeJson(fs: Field*): String = makeJson(fs.toList)

}
