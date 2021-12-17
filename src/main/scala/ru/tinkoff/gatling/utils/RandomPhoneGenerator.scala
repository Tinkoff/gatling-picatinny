package ru.tinkoff.gatling.utils

import ru.tinkoff.gatling.utils.TypePhone._
import ru.tinkoff.gatling.utils.scalaFaker.{Phone, PhoneFormat}

object RandomPhoneGenerator {

  final val DEFAULT_FORMAT = Map(
    "7" -> PhoneFormat(
      countryCode = "7",
      length = 7,
      areaCodes = Option(Seq("495", "495")),
      prefixes = Seq(""),
      format = "+XXXXXXXXXXX",
    ),
  )

  def randomPhoneJson(fileFormats: Option[String], typePhone: TypePhone, keyCountryCode: Option[String]): String = {
    typePhone match {
      case PhoneNumber         =>
        fileFormats match {
          case Some(format) => Phone(format).phoneNumber(keyCountryCode)
          case None         => new Phone(DEFAULT_FORMAT).phoneNumber(keyCountryCode)
        }
      case TollFreePhoneNumber =>
        fileFormats match {
          case Some(format) => Phone(format).tollFreePhoneNumber
          case None         => new Phone(DEFAULT_FORMAT).tollFreePhoneNumber
        }
      case E164PhoneNumber     =>
        fileFormats match {
          case Some(format) => Phone(format).e164PhoneNumber(keyCountryCode)
          case None         => new Phone(DEFAULT_FORMAT).e164PhoneNumber(keyCountryCode)
        }
    }
  }

  def randomPhoneMap(
      mapFormats: Option[Map[String, PhoneFormat]],
      typePhone: TypePhone,
      keyCountryCode: Option[String],
  ): String = {
    typePhone match {
      case PhoneNumber         =>
        mapFormats match {
          case Some(format) => new Phone(format).phoneNumber(keyCountryCode)
          case None         => new Phone(DEFAULT_FORMAT).phoneNumber(keyCountryCode)
        }
      case TollFreePhoneNumber =>
        mapFormats match {
          case Some(format) => new Phone(format).tollFreePhoneNumber
          case None         => new Phone(DEFAULT_FORMAT).tollFreePhoneNumber
        }
      case E164PhoneNumber     =>
        mapFormats match {
          case Some(format) => new Phone(format).e164PhoneNumber(keyCountryCode)
          case None         => new Phone(DEFAULT_FORMAT).e164PhoneNumber(keyCountryCode)
        }
    }
  }

}
