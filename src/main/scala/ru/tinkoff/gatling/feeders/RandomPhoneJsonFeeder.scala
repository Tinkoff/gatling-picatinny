package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.TypePhone.TypePhone
import ru.tinkoff.gatling.utils.{RandomPhoneGenerator, TypePhone}

object RandomPhoneJsonFeeder {

  def apply(
      paramName: String,
      jsonFormats: String = "",
      typePhone: TypePhone = TypePhone.PhoneNumber,
      keyCountryCode: String = "",
  ): Feeder[String] =
    feeder[String](paramName)(
      RandomPhoneGenerator.randomPhoneJson(
        Option.when(jsonFormats.nonEmpty)(jsonFormats),
        typePhone,
        Option.when(keyCountryCode.nonEmpty)(keyCountryCode),
      ),
    )

}
