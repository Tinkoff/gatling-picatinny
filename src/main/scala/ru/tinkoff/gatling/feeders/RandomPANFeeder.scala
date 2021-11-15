package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators
import ru.tinkoff.gatling.utils.RandomDataGenerators.{alphanumericString, getRandomElement}
import scala.util.Random

object RandomPANFeeder {

  def apply(paramName: String,
            status: String = getRandomElement(Seq('P', 'C', 'H', 'A', 'B', 'G', 'J', 'L', 'F', 'I'), new Random()).toString,
            name: String = alphanumericString(1)): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPAN(status, name))

}
