package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.{Brackets, RandomPhoneGenerator}

object RandomPhoneFeeder {

  def apply(
      paramName: String,
      countryCode: String = "+7",
      regionCode: String = "",
      delimiter: String = "",
      brackets: Brackets = Brackets.None,
  ): Feeder[String] =
    feeder[String](paramName)(
      RandomPhoneGenerator.randomPhone(countryCode, Option.when(regionCode.nonEmpty)(regionCode), delimiter, brackets),
    )

}
