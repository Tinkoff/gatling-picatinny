package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomPhoneFeeder {

  def apply(paramName: String, countryCode: String = "+7"): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPhone(countryCode))

}
