package ru.tinkoff.gatling.templates

object Syntax {
  case class Field(name: String, fieldVal: FieldVal)

  sealed trait FieldVal

  case class RawValString(value: String) extends FieldVal
  case class RawValGen[+T](value: T)     extends FieldVal

  case class InterpolateStrVal(interpolatorName: String) extends FieldVal
  case class InterpolateGenVal[+T](interpolatorName: T)  extends FieldVal

  case class ObjectVal(f: List[Field])    extends FieldVal
  case class ArrayVal(vs: List[FieldVal]) extends FieldVal

  def obj(fs: Field*): ObjectVal = ObjectVal(fs.toList)

  private val interpolateRegExpr = "\\$\\{(\\w+)\\}".r

  def arr[T](vs: T*): ArrayVal =
    ArrayVal(vs.map {
      case o: ObjectVal => o
      case a: ArrayVal  => a
      case s: String =>
        interpolateRegExpr.findFirstMatchIn(s).fold[FieldVal](RawValString(s))(m => InterpolateStrVal(m.group(1)))
      case other => RawValGen(other)
    }.toList)

  implicit def strToField(str: String): Field = Field(str, InterpolateStrVal(str))

  implicit class FieldValOps(val fieldName: String) extends AnyVal {
    def -(str: String): Field = Field(fieldName, RawValString(str))

    def -[T](v: T): Field = v match {
      case a: ArrayVal  => Field(fieldName, a)
      case o: ObjectVal => Field(fieldName, o)
      case other        => Field(fieldName, RawValGen(other))
    }

    def -(fs: Field*): Field = Field(fieldName, ObjectVal(fs.toList))

    def asSessionVar(str: String): Field = Field(fieldName, InterpolateStrVal(str))

    def ~(str: String): Field = Field(fieldName, InterpolateStrVal(str))

    def asSessionVar[T](t: T): Field = Field(fieldName, InterpolateGenVal(t))

    def ~[T](t: T): Field = Field(fieldName, InterpolateGenVal(t))

    def array[T](ts: Seq[T]): Field = Field(fieldName, arr(ts: _*))

    def >[T](ts: T*): Field = array(ts)

  }

  def makeJson(fields: List[Field]): String =
    fields.map {
      case Field(name, RawValString(s))       => s""""$name": "$s""""
      case Field(name, RawValGen(s))          => s""""$name": $s"""
      case Field(name, InterpolateStrVal(in)) => s""""$name": "$${$in}""""
      case Field(name, InterpolateGenVal(in)) => s""""$name": $${$in}"""
      case Field(name, ObjectVal(f))          => s""""$name": ${makeJson(f)}"""
      case Field(name, ArrayVal(vs))          => s""""$name": ${makeArrJson(vs)}"""
    }.mkString("{", ",", "}")

  def makeArrJson(vals: List[FieldVal]): String =
    vals.map {
      case RawValString(s)            => s""""$s""""
      case RawValGen(f @ Field(_, _)) => s"""${makeJson(List(f))}"""
      case RawValGen(())              => ""
      case RawValGen(s)               => s"""$s"""
      case InterpolateStrVal(in)      => s""""$${$in}""""
      case InterpolateGenVal(in)      => s"""$${$in}"""
      case ObjectVal(f)               => s"""${makeJson(f)}"""
      case ArrayVal(vs)               => s"""${makeArrJson(vs)}"""
    }.mkString("[", ",", "]")

  def makeXml(fields: List[Field]): String =
    fields
      .foldLeft(new StringBuilder) {
        case (sb, Field(name, RawValString(s)))       => sb.append(s"""<$name>$s</$name>""")
        case (sb, Field(name, RawValGen(s)))          => sb.append(s"""<$name>$s</$name>""")
        case (sb, Field(name, InterpolateStrVal(in))) => sb.append(s"""<$name>$${$in}</$name>""")
        case (sb, Field(name, InterpolateGenVal(in))) => sb.append(s"""<$name>$${$in}</$name>""")
        case (sb, Field(name, ObjectVal(f)))          => sb.append(s"""<$name>${makeXml(f)}</$name>""")
        case (sb, Field(name, ArrayVal(vs)))          => sb.append(s"""<$name>${makeXmlArray(sb, vs)}</$name>""")
      }
      .mkString

  def makeXmlArray(stringBuilder: StringBuilder, vs: List[FieldVal]): String =
    vs.foldLeft(stringBuilder) {
        case (sb, RawValString(s))            => sb.append(s"<item>$s</item>")
        case (sb, RawValGen(f @ Field(_, _))) => sb.append(s"""<item>${makeXml(List(f))}</item>""")
        case (sb, RawValGen(()))              => sb.append("")
        case (sb, RawValGen(s))               => sb.append(s"<item>$s</item>")
        case (sb, InterpolateStrVal(in))      => sb.append(s"<item>$${$in}</item>")
        case (sb, InterpolateGenVal(in))      => sb.append(s"<item>$${$in}</item>")
        case (sb, ObjectVal(f))               => sb.append(s"""<item>${makeXml(f)}</item>""")
        case (sb, ArrayVal(vs))               => sb.append(s"""<item>${makeXmlArray(sb, vs)}</item>""")
      }
      .mkString

  def makeXml(fs: Field*): String = makeXml(fs.toList)

  def makeJson(fs: Field*): String = makeJson(fs.toList)

}
