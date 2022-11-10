package ru.tinkoff.gatling.utils.phone

object TypePhone{
  sealed trait TypePhone
  case object PhoneNumber extends TypePhone
  case object TollFreePhoneNumber extends TypePhone
  case object E164PhoneNumber extends TypePhone
}
