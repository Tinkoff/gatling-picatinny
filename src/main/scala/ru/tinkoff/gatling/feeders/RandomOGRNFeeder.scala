package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators
import ru.tinkoff.gatling.utils.RandomDataGenerators.getRandomElement
import java.time.LocalDateTime
import scala.util.Random

object RandomOGRNFeeder {

  def apply(paramName: String,
            owner: String = getRandomElement(Seq(1, 5), new Random()).toString,
            date: String = LocalDateTime.now().getYear.toString.slice(2, 4),
            reg: String = scala.util.Random.between(1, 90).toString): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomOGRN(owner, date, reg))

}
