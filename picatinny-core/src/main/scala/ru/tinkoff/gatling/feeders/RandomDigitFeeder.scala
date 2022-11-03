package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomDigitFeeder {

  def apply(paramName: String): Feeder[Int] =
    feeder[Int](paramName)(RandomDataGenerators.randomDigit())

}
