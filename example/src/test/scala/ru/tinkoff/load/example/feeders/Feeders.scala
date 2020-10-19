package ru.tinkoff.load.example.feeders

import io.gatling.core.Predef._
import ru.tinkoff.gatling.feeders._
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


object Feeders {

  private val newYearDate  = LocalDateTime.of(2020, 1, 1, 0, 0)
  private val goToWorkDate = LocalDateTime.of(2020, 1, 9, 9, 0)

  //random date +/- 3 days from now
  val simpleRandomDate = RandomDateFeeder("simpleDate", 3, 3)

  //random date from newYearDate  with specified date string pattern
  val holidaysDate = RandomDateFeeder("holidays", 8, 0, "yyyy-MM-dd'T'HH:mm", newYearDate, ChronoUnit.DAYS)

  //random time from 9:00 to 18:00
  val firstWorkDayHours = RandomDateFeeder("firstWorkDayHours", 9 * 60, 0, "HH:mm", goToWorkDate, ChronoUnit.MINUTES)

  //feeder provide two params:
  //startOfVacation = LocalDateTime.now()
  //endOfVacation = random date from now() to 14 days in the future
  val vacationDate =
    RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS)

  //random Int
  val randomDigit = RandomDigitFeeder("randomDigit")

  //random phone
  //+7 country code is default
  val randomPhone    = RandomPhoneFeeder("randomPhone")
  val randomUsaPhone = RandomPhoneFeeder("randomUsaPhone", "+1")

  //random alphanumeric String with specified length
  val randomString = RandomStringFeeder("randomString", 16)

  // random String generated from specified alphabet (or alphanumeric as default)
  // with random length in specified interval from 1 to 10
  val randomRangeString =
    RandomRangeStringFeeder("randomRangeString", 1, 10, "qwertyuiop*+-123")

  //random UUID
  val randomUuid = RandomUUIDFeeder("randomUuid")

  //sequence of Long numbers from one to Long.MaxValue with specified step = 2
  val sequenceLong = SequentialFeeder("sequenceLong", 1, 2)

  private def myFunction: String = {
    scala.util.Random.shuffle(List("first", "second", "third")).head
  }

  //custom feeder from provided function
  val myCustomFeeder = CustomFeeder[String]("myParam", myFunction)

  private val digitFeeder  = RandomDigitFeeder("digit")
  private val stringFeeder = RandomStringFeeder("string")
  private val phoneFeeder  = RandomStringFeeder("phone")

  //how to combine together 2 or more feeders
  //as result we get feeder with 3 params: digit, string, phone
  val gluedTogetherFeeder = digitFeeder ** stringFeeder ** phoneFeeder

  //tranform values of this Feeder
  val finiteRandomDigitsWithTransform = RandomDigitFeeder("randomDigit")
    .toFiniteLength(20)
    .convert { case (k, v) => k -> v.toString() }
    .circular

  //tranform List to Feeder
  val list2feeder = List(1, 2, 3).toFeeder("listId").circular

  // string sequentially generated from the specified pattern
  val regexString = RegexFeeder("regex", "[a-zA-Z0-9]{8}")

}
