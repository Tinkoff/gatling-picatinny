package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.TypePhone.TypePhone
import ru.tinkoff.gatling.utils.{RandomPhoneGenerator, TypePhone}

object RandomPhoneFeeder {

  def apply(paramName: String, fileFormats: String, typePhone: TypePhone = TypePhone.PhoneNumber): Feeder[String] =
    feeder[String](paramName)(
      RandomPhoneGenerator.randomPhone(fileFormats, typePhone),
    )

}
