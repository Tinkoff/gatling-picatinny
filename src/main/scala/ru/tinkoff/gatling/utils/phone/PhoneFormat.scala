package ru.tinkoff.gatling.utils.phone

final case class PhoneFormat(
    countryCode: String,
    length: Int,
    areaCodes: Seq[String],
    format: String,
    prefixes: Seq[String] = (0 to 999).map(_.toString),
)
