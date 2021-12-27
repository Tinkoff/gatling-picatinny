package ru.tinkoff.gatling.utils.phone

import io.circe.generic.auto._
import io.circe.parser
import ru.tinkoff.gatling.utils.getRandomElement

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Random

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
      case head :: tail => loop(tail, acc.replaceFirst("X", head.toString))
      case _            => acc
    }
    loop(numbers, template)
  }

  /** Gets phone number based on parameters from PhoneFormat
    *
    * @param format
    *   phone number parameters
    * @return
    *   random string phone number
    */
  private[this] def getPhoneNumber(format: PhoneFormat): String = {
    val cc     = format.countryCode
    val ac     = areaCode(Some(cc))
    val prefix = getRandomElement[String](format.prefixes)
    val tail   = (1 to format.length - prefix.length).map(_ => Random.nextInt(10)).mkString
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
    * @param countryCode
    *   country code from PhoneFormat
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
    val starts = 1 to 999
    val xs     = (1 to 7).map(_ => Random.nextInt(10))
    s"(${getRandomElement(starts)}) ${xs.slice(0, 3).mkString}-${xs.slice(3, 7).mkString}"
  }

  /** Generates E.164 phone number using PhoneFormat without field "format"
    *
    * @param countryCode
    *   country code from PhoneFormat
    * @return
    *   random string E.164 phone number format +XXXXXXXXXXX
    */
  def e164PhoneNumber: String = getPhoneNumber(randomPhoneFormat)

}

object Phone {

  final val DEFAULT_FORMAT_RU_MOBILE = List(
    PhoneFormat(
      countryCode = "+7",
      length = 7,
      areaCodes = (900 to 999).map(_.toString),
      prefixes = Seq(""),
      format = "+XXXXXXXXXXX",
    ),
  )

  private def models(resourcePath: String): Seq[PhoneFormat] =
    parser
      .decode[Seq[PhoneFormat]](Source.fromResource(resourcePath).mkString)
      .getOrElse(DEFAULT_FORMAT_RU_MOBILE)

  def apply(resourcePath: String): Phone = new Phone(models(resourcePath))

  def apply(formats: Seq[PhoneFormat]): Phone = new Phone(formats)
}
