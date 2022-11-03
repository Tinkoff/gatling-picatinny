package ru.tinkoff.gatling.utils.phone

object TypePhone extends Enumeration {
  type TypePhone = Value

  val PhoneNumber, TollFreePhoneNumber, E164PhoneNumber = Value
}
