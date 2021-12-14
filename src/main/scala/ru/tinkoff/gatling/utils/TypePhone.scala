package ru.tinkoff.gatling.utils

object TypePhone extends Enumeration {
  type TypePhone = Value

  val PhoneNumber, TollFreePhoneNumber, E164PhoneNumber = Value
}
