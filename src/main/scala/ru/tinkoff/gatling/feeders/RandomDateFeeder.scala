package ru.tinkoff.gatling.feeders

import java.time.{LocalDateTime, ZoneId}
import java.time.temporal.{ChronoUnit, TemporalUnit}
import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomDateFeeder {

  def apply(paramName: String,
            positiveDaysDelta: Int = 1,
            negativeDaysDelta: Int = 1,
            datePattern: String = "yyyy-MM-dd",
            dateFrom: LocalDateTime = LocalDateTime.now(),
            timezone: ZoneId = ZoneId.systemDefault(),
            unit: TemporalUnit = ChronoUnit.DAYS): Feeder[String] =
    feeder[String](paramName)(
      RandomDataGenerators.randomDate(positiveDaysDelta, negativeDaysDelta, datePattern, dateFrom, timezone, unit))

}
