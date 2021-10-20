package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

import java.time.{LocalDateTime, ZonedDateTime}
import java.time.temporal.{ChronoUnit, TemporalUnit}

object RandomDateTimezoneFeeder {

  def apply(paramName: String,
            positiveDaysDelta: Int = 1,
            negativeDaysDelta: Int = 1,
            datePattern: String = "yyyy-MM-dd",
            dateFrom: ZonedDateTime = ZonedDateTime.now(),
            unit: TemporalUnit = ChronoUnit.DAYS): Feeder[String] =
    feeder[String](paramName)(
      RandomDataGenerators.randomTimezoneDate(positiveDaysDelta, negativeDaysDelta, datePattern, dateFrom, unit))

}
