package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.phone.TypePhone.TypePhone
import ru.tinkoff.gatling.utils.phone.{PhoneFormat, TypePhone}

object RandomPhoneMapFeeder {

  def apply(
      paramName: String,
      mapFormats: Map[String, PhoneFormat] = Map.empty[String, PhoneFormat],
      typePhone: TypePhone = TypePhone.PhoneNumber,
      keyCountryCode: String = "",
  ): Feeder[String] =
    feeder[String](paramName)(
      RandomPhoneGenerator.randomPhoneMap(
        Option.when(mapFormats.nonEmpty)(mapFormats),
        typePhone,
        Option.when(keyCountryCode.nonEmpty)(keyCountryCode),
      ),
    )

}
