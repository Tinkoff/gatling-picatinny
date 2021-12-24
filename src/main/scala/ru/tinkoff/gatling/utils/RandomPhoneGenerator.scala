package ru.tinkoff.gatling.utils

import ru.tinkoff.gatling.utils.phone.TypePhone._
import ru.tinkoff.gatling.utils.phone.{Phone, PhoneFormat, TypePhone}

object RandomPhoneGenerator {

  def randomPhone(
      formats: Seq[PhoneFormat] = Seq.empty[PhoneFormat],
      typePhone: TypePhone = TypePhone.PhoneNumber,
      countryCode: Option[String] = None,
  ): String = typePhone match {
    case PhoneNumber         => Phone(formats).phoneNumber(countryCode)
    case TollFreePhoneNumber => Phone(formats).tollFreePhoneNumber
    case E164PhoneNumber     => Phone(formats).e164PhoneNumber(countryCode)
  }

  def randomPhone(
      pathToFormats: String,
      typePhone: TypePhone = TypePhone.PhoneNumber,
      countryCode: Option[String] = None,
  ): String = typePhone match {
    case PhoneNumber         => Phone(pathToFormats).phoneNumber(countryCode)
    case TollFreePhoneNumber => Phone(pathToFormats).tollFreePhoneNumber
    case E164PhoneNumber     => Phone(pathToFormats).e164PhoneNumber(countryCode)
  }

}
