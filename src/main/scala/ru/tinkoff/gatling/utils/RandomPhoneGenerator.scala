package ru.tinkoff.gatling.utils

import ru.tinkoff.gatling.utils.TypePhone._
import ru.tinkoff.gatling.utils.scalaFaker.Phone

object RandomPhoneGenerator {

  def randomPhone(fileFormats: String, typePhone: TypePhone): String = {
    typePhone match {
      case PhoneNumber         => Phone(fileFormats).phoneNumber()
      case TollFreePhoneNumber => Phone(fileFormats).tollFreePhoneNumber
      case E164PhoneNumber     => Phone(fileFormats).e164PhoneNumber()
    }
  }

}
