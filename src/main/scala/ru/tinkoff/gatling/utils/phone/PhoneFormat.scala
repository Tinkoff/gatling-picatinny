package ru.tinkoff.gatling.utils.phone

final case class PhoneFormat(
    countryCode: String,
    length: Int,
    areaCodes: Option[Seq[String]] = None,
    prefixes: Seq[String],
    format: String,
)
