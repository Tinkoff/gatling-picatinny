package ru.tinkoff.gatling.utils.phone

import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import io.circe.{Decoder, parser}
import ru.tinkoff.gatling.utils.getRandomElement

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Random, Using}

class Phone(models: Seq[PhoneFormat]) {

  /** Gets formatted phone number
    *
    * @param template
    *   template for formatting phone number
    * @param numbers
    *   E.164 type phone
    * @return
    *   formatted string phone number
    */
  private[this] def format(template: String, numbers: Seq[Char]): String = {
    @tailrec
    def loop(xs: Seq[Char], acc: String): String = xs match {
      case Seq(head, tail @ _*) if head != '+' => loop(tail, acc.replaceFirst("X", head.toString))
      case Seq(head, tail @ _*) if head == '+' => loop(tail, acc)
      case _                                   => acc
    }
    loop(numbers, template)
  }

  /** Gets phone number based on parameters from PhoneFormat
    *
    * @param phoneFormat
    *   phone number parameters
    * @return
    *   random string phone number
    */
  private[this] def getPhoneNumber(phoneFormat: PhoneFormat): String = {
    val cc     = phoneFormat.countryCode
    val ac     = areaCode(Some(cc))
    val prefix = getRandomElement[String](phoneFormat.prefixes)
    val tail   = (1 to phoneFormat.length - prefix.length - ac.length).map(_ => Random.nextInt(10)).mkString
    s"$cc$ac$prefix$tail"
  }

  /** Selects random phone format from PhoneFormat file
    *
    * @return
    *   random phone format
    */
  private[this] def randomPhoneFormat = getRandomElement[PhoneFormat](models)

  /** Generates area code using PhoneFormat
    *
    * @param keyCountryCode
    *   country code from PhoneFormat
    * @return
    *   string area code
    */
  def areaCode(keyCountryCode: Option[String] = None): String =
    getRandomElement[String](
      keyCountryCode
        .map(cc => models.filter(_.countryCode == cc).flatMap(_.areaCodes))
        .getOrElse(randomPhoneFormat.areaCodes),
    )

  /** Generates phone number using PhoneFormat
    *
    * @return
    *   random string phone number
    */
  def phoneNumber: String = {
    val pf = randomPhoneFormat
    format(pf.format, getPhoneNumber(pf))
  }

  /** Generates Toll-free phone number without using PhoneFormat
    *
    * @return
    *   random string Toll-free phone number format (XXX) XXX-XXXX
    */
  def tollFreePhoneNumber: String = {
    val starts = List("800", "833", "844", "855", "866", "877", "888")
    val xs     = (1 to 7).map(_ => Random.nextInt(10))
    s"(${getRandomElement(starts)}) ${xs.slice(0, 3).mkString}-${xs.slice(3, 7).mkString}"
  }

  /** Generates E.164 phone number using PhoneFormat without field "format"
    *
    * @return
    *   random string E.164 phone number format +XXXXXXXXXXX
    */
  def e164PhoneNumber: String = getPhoneNumber(randomPhoneFormat)

}

object Phone extends StrictLogging {

  final val DEFAULT_FORMAT_RU_MOBILE = Seq(
    PhoneFormat(
      countryCode = "+7",
      length = 10,
      areaCodes = (900 to 999).map(_.toString),
      format = "+XXXXXXXXXXX",
    ),
  )

  private def models(resourcePath: String): Seq[PhoneFormat] = {

    val decodeFormats = Decoder[Seq[PhoneFormat]].prepare(
      _.downField("formats"),
    )

    (for {
      json <- Using(Source.fromResource(resourcePath))(_.mkString).toEither
      res  <- parser.decode[Seq[PhoneFormat]](json)(decodeFormats)
    } yield res).fold(ex => throw ex, identity)

  }

  def apply(resourcePath: String): Phone = new Phone(models(resourcePath))

  def apply(formats: Seq[PhoneFormat]): Phone = if (formats.nonEmpty) new Phone(formats)
  else new Phone(DEFAULT_FORMAT_RU_MOBILE)
}
