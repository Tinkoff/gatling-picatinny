package ru.tinkoff.gatling.utils.scalaFaker

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Random

sealed case class PhoneFormat(
    countryCode: String,
    length: Int,
    areaCodes: Option[Seq[String]] = None,
    prefixes: Seq[String],
    format: String,
)
sealed case class PhoneModel(formats: Seq[PhoneFormat])

class Phone(models: Map[String, PhoneFormat]) {
  import Phone._

  private[this] def format(template: String, numbers: Seq[String]): String = {
    @tailrec
    def loop(xs: Seq[String], acc: String): String = xs match {
      case head +: tail => loop(tail, acc.replaceFirst("X", head))
      case _            => acc
    }
    loop(numbers, template)
  }

  private[this] def getPhoneNumber(format: PhoneFormat, enableE164: Boolean = false): String = {
    val cc     = format.countryCode
    val rac    = areaCodeOption(Some(cc)).getOrElse("")
    val ac     = if (enableE164) removeHeadingZero(rac) else rac
    val prefix = getRandomElement[String](format.prefixes).get
    // scalastyle:off
    val tail   = (1 to format.length - prefix.length).map(_ => Random.nextInt(10)).mkString
    // scalastyle:on
    s"${cc}${ac}${prefix}${tail}"
  }

  private[this] def randomPhoneFormat = getRandomElement[PhoneFormat](models.map(_._2).toSeq).get

  private[this] def removeHeadingZero(s: String): String =
    if (s.startsWith("0")) removeHeadingZero(s.drop(1).mkString) else s

  // generates phone numbers of type: "+7 980 123-45-67" or "8(912)123-45-67", use phoneFormat file
  def phoneNumber(countryCode: Option[String] = None): String = countryCode match {
    case Some(cc) =>
      models
        .get(cc)
        .map { phoneFormat =>
          format(phoneFormat.format, getPhoneNumber(phoneFormat).map(_.toString))
        }
        .getOrElse(throw new Exception(s"Unsupported country code: $cc"))
    case None     =>
      val phoneFormat = randomPhoneFormat
      format(phoneFormat.format, getPhoneNumber(phoneFormat).map(_.toString))
  }

  def countryCode: String = getRandomElement[String](models.map(_._1).toSeq).get

  def areaCodeOption(countryCode: Option[String] = None): Option[String] = countryCode match {
    case Some(cc) =>
      for {
        pf <- models.get(cc)
        ac <- pf.areaCodes
      } yield getRandomElement[String](ac).get
    case None     => randomPhoneFormat.areaCodes.map(ac => getRandomElement[String](ac).get)
  }

  // generates phone numbers of type: "(800) 123-4567", not use phoneFormat file
  def tollFreePhoneNumber: String = {
    // scalastyle:off
    val starts = List("777", "800", "888")
    val xs     = (1 to 7).map(_ => Random.nextInt(10))
    s"(${starts(Random.nextInt(2))}) ${xs.slice(0, 3).mkString}-${xs.slice(3, 7).mkString}"
    // scalastyle:on
  }

  // generates phone numbers of type: "+71234567891", use phoneFormat file without field "format"
  def e164PhoneNumber(countryCode: Option[String] = None): String = countryCode match {
    case Some(cc) =>
      models
        .get(cc)
        .map { f =>
          s"+${getPhoneNumber(f, true)}"
        }
        .getOrElse(throw new Exception(s"Unsupported country code: $cc"))
    case None     => s"+${getPhoneNumber(randomPhoneFormat, true)}"
  }
}

object Phone extends Faker {
  private def model(fileFormats: String): PhoneModel        = {
    import io.circe.Decoder
    import io.circe.generic.semiauto.deriveDecoder

    implicit val phoneFormatDecoder: Decoder[PhoneFormat] = deriveDecoder
    implicit val phoneModelDecoder: Decoder[PhoneModel]   = deriveDecoder

    val s = Source.fromInputStream(getResource(fileFormats)).mkString
    objectFrom[PhoneModel](s)
  }
  def models(fileFormats: String): Map[String, PhoneFormat] = model(fileFormats).formats.map(pf => pf.countryCode -> pf).toMap

  def apply(fileFormats: String): Phone = new Phone(models(fileFormats))
}
