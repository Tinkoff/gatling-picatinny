package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators
import java.time.LocalDateTime

object RandomPassportFeeder {

  def apply(paramName: String,
            region: String = scala.util.Random.between(1, 90).toString,
            date: String = LocalDateTime.now().getYear.toString.slice(2, 4)): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPassport(region, date))

}
