package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RandomDateOfPattern {

  def apply(paramName: String,
            datePattern: DateTimeFormatter): Feeder[String] =
    feeder[String](paramName)(
      RandomDataGenerators.randomDatePattern(datePattern, LocalDateTime.now()))

}
