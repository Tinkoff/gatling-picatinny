package ru.tinkoff.gatling.feeders

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoUnit, TemporalUnit}
import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomDateRangeFeeder {

  def apply(paramNameFrom: String,
            paramNameTo: String,
            offsetDate: Long,
            datePattern: String = "yyyy-MM-dd",
            dateFrom: LocalDateTime = LocalDateTime.now(),
            unit: TemporalUnit = ChronoUnit.DAYS,
            timezone: ZoneId = ZoneId.systemDefault()): Feeder[String] =
    feeder[String](paramNameFrom)(dateFrom.format(DateTimeFormatter.ofPattern(datePattern)))
      .map(m => m + (paramNameTo -> RandomDataGenerators.randomDate(offsetDate, datePattern, dateFrom, unit, timezone)))

}
