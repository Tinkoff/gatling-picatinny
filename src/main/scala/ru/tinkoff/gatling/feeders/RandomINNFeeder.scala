package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomINNFeeder {

  def apply(paramName: String,
            isPhysPers: Boolean = true,
            reg: Int = scala.util.Random.between(1, 90),
            number: Int = scala.util.Random.between(1, 100)): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomINN(isPhysPers, reg, number))

}
