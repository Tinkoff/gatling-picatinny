package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomStringFeeder {

  def apply(paramName: String, paramLength: Int = 10): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.alphanumericString(paramLength))

}
