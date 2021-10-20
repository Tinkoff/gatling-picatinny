package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

object CurrentDateFeeder {

  def apply(paramName: String,
            datePattern: DateTimeFormatter,
            timezone: ZoneId = ZoneId.systemDefault()): Feeder[String] =
    feeder[String](paramName)(
      RandomDataGenerators.randomDatePattern(datePattern, LocalDateTime.now(), timezone))

}
