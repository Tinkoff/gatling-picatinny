package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomPSRNFeeder {

  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPSRN())

}
