package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomPhoneGenerator
import ru.tinkoff.gatling.utils.phone.PhoneFormat
import ru.tinkoff.gatling.utils.phone.TypePhone.TypePhone

object RandomPhoneFeeder {

  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone())

  def apply(
      paramName: String,
      formats: Seq[PhoneFormat],
  ): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone(formats = formats))

  def apply(
      paramName: String,
      formats: Seq[PhoneFormat],
      typePhone: TypePhone,
  ): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone(formats = formats, typePhone = typePhone))

  def apply(
      paramName: String,
      formats: Seq[PhoneFormat],
      typePhone: TypePhone,
      countryCode: String,
  ): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone(formats = formats, typePhone = typePhone, countryCode = Some(countryCode)))

  def apply(
      paramName: String,
      formatsPath: String,
  ): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone(pathToFormats = formatsPath))

  def apply(
      paramName: String,
      formatsPath: String,
      typePhone: TypePhone,
  ): Feeder[String] =
    feeder[String](paramName)(RandomPhoneGenerator.randomPhone(pathToFormats = formatsPath, typePhone = typePhone))

  def apply(
      paramName: String,
      formatsPath: String,
      typePhone: TypePhone,
      countryCode: String,
  ): Feeder[String] =
    feeder[String](paramName)(
      RandomPhoneGenerator.randomPhone(pathToFormats = formatsPath, typePhone = typePhone, countryCode = Some(countryCode)),
    )

}
