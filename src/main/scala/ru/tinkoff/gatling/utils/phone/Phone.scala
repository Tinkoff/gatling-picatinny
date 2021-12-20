package ru.tinkoff.gatling.utils.phone

import ru.tinkoff.gatling.utils.{getRandomElement, getResource, objectFrom}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Random

class Phone(models: Map[String, PhoneFormat]) {
  import Phone._

  /** Gets formatted phone number
    *
    * @param template
    *   template for formatting phone number
    * @param numbers
    *   E.164 type phone
    * @return
    *   formatted string phone number
    */
  private[this] def format(template: String, numbers: Seq[String]): String = {
    @tailrec
    def loop(xs: Seq[String], acc: String): String = xs match {
      case head +: tail => loop(tail, acc.replaceFirst("X", head))
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
    val ac     = areaCodeOption(Some(cc)).getOrElse("")
    val prefix = getRandomElement[String](format.prefixes)
    val tail   = (1 to format.length - prefix.length).map(_ => Random.nextInt(10)).mkString
    s"${cc}${ac}${prefix}${tail}"
  }

  /** Selects random phone format from PhoneFormat file
    *
    * @return
    *   random phone format
    */
  private[this] def randomPhoneFormat = getRandomElement[PhoneFormat](models.map(_._2).toSeq)

  /** Generates area code using PhoneFormat
    *
    * @param keyCountryCode
    *   country code from PhoneFormat
    * @return
    *   string area code
    */
  def areaCodeOption(keyCountryCode: Option[String] = None): Option[String] = keyCountryCode match {
    case Some(cc) =>
      for {
        pf <- models.get(cc)
        ac <- pf.areaCodes
      } yield getRandomElement[String](ac)
    case None     => randomPhoneFormat.areaCodes.map(ac => getRandomElement[String](ac))
  }

  /** Generates phone number using PhoneFormat
    *
    * @param keyCountryCode
    *   country code from PhoneFormat
    * @return
    *   random string phone number
    */
  def phoneNumber(keyCountryCode: Option[String] = None): String = keyCountryCode match {
    case Some(cc) =>
      models
        .get(cc)
        .map { phoneFormat =>
          format(phoneFormat.format, getPhoneNumber(phoneFormat).map(_.toString))
        }
        .getOrElse(throw new Exception(s"Unsupported key country code: $cc"))
    case None     =>
      val phoneFormat = randomPhoneFormat
      format(phoneFormat.format, getPhoneNumber(phoneFormat).map(_.toString))
  }

  /** Generates Toll-free phone number without using PhoneFormat
    *
    * @return
    *   random string Toll-free phone number format (XXX) XXX-XXXX
    */
  def tollFreePhoneNumber: String = {
    val starts = List("800", "888", "877", "866", "855", "844", "833")
    val xs     = (1 to 7).map(_ => Random.nextInt(10))
    s"(${getRandomElement(starts)}) ${xs.slice(0, 3).mkString}-${xs.slice(3, 7).mkString}"
  }

  /** Generates E.164 phone number using PhoneFormat without field "format"
    *
    * @param keyCountryCode
    *   country code from PhoneFormat
    * @return
    *   random string E.164 phone number format +XXXXXXXXXXX
    */
  def e164PhoneNumber(keyCountryCode: Option[String] = None): String = keyCountryCode match {
    case Some(cc) =>
      models
        .get(cc)
        .map { f =>
          s"+${getPhoneNumber(f)}"
        }
        .getOrElse(throw new Exception(s"Unsupported key country code: $cc"))
    case None     => s"+${getPhoneNumber(randomPhoneFormat)}"
  }
}

object Phone {
  private def model(resourcePath: String): PhoneModel = {
    import io.circe.Decoder
    import io.circe.generic.semiauto.deriveDecoder

    implicit val phoneFormatDecoder: Decoder[PhoneFormat] = deriveDecoder
    implicit val phoneModelDecoder: Decoder[PhoneModel]   = deriveDecoder

    val s = Source.fromInputStream(getResource(resourcePath)).mkString

    objectFrom[PhoneModel](s)
  }

  def models(resourcePath: String): Map[String, PhoneFormat] = model(resourcePath).formats.map(pf => pf.countryCode -> pf).toMap

  def apply(resourcePath: String): Phone = new Phone(models(resourcePath))
}