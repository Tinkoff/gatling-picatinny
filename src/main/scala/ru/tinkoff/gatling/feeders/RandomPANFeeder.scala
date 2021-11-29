package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomPANFeeder {

  def apply(paramName: String,
            bin: List[String] = List.empty[String]): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPAN(bin))

}
