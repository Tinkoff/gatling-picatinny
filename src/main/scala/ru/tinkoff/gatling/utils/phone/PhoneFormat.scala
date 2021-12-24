package ru.tinkoff.gatling.utils.phone

final case class PhoneFormat(
    countryCode: String,
    length: Int,
    areaCodes: Seq[String],
    format: String,
    prefixes: Seq[String] = Seq.empty[String],
)
