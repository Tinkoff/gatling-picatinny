package ru.tinkoff.load.example.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.{Feeder, FeederBuilderBase}
import ru.tinkoff.gatling.config.SimulationConfig.getStringParam
import ru.tinkoff.gatling.feeders._

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object Feeders {

  private val newYearDate  = LocalDateTime.of(2020, 1, 1, 0, 0)
  private val goToWorkDate = LocalDateTime.of(2020, 1, 9, 9, 0)

  //random date +/- 3 days from now
  val simpleRandomDate: Feeder[String] = RandomDateFeeder("simpleDate", 3, 3)

  //random date from newYearDate  with specified date string pattern
  val holidaysDate: Feeder[String] = RandomDateFeeder("holidays", 8, 0, "yyyy-MM-dd'T'HH:mm", newYearDate, ChronoUnit.DAYS)

  //random time from 9:00 to 18:00
  val firstWorkDayHours: Feeder[String] =
    RandomDateFeeder("firstWorkDayHours", 9 * 60, 0, "HH:mm", goToWorkDate, ChronoUnit.MINUTES)

  //feeder provide two params:
  //startOfVacation = LocalDateTime.now()
  //endOfVacation = random date from now() to 14 days in the future
  val vacationDate: Feeder[String] =
    RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS)

  //random Int
  val randomDigit: Feeder[Int] = RandomDigitFeeder("randomDigit")

  //random phone
  //+7 country code is default
  val randomPhone: Feeder[String]    = RandomPhoneFeeder("randomPhone")
  val randomUsaPhone: Feeder[String] = RandomPhoneFeeder("randomUsaPhone", "+1")

  //random alphanumeric String with specified length
  val randomString: Feeder[String] = RandomStringFeeder("randomString", 16)

  // random String generated from specified alphabet (or alphanumeric as default)
  // with random length in specified interval from 1 to 10
  val randomRangeString: Feeder[String] =
    RandomRangeStringFeeder("randomRangeString", 1, 10, "qwertyuiop*+-123")

  //random UUID
  val randomUuid: Feeder[String] = RandomUUIDFeeder("randomUuid")

  //sequence of Long numbers from one to Long.MaxValue with specified step = 2
  val sequenceLong: Feeder[Long] = SequentialFeeder("sequenceLong", 1, 2)

  private def myFunction: String = {
    scala.util.Random.shuffle(List("first", "second", "third")).head
  }

  //custom feeder from provided function
  val myCustomFeeder: Feeder[String] = CustomFeeder[String]("myParam", myFunction)

  private val digitFeeder  = RandomDigitFeeder("digit")
  private val stringFeeder = RandomStringFeeder("string")
  private val phoneFeeder  = RandomStringFeeder("phone")

  //Vault HC feeder
  private val vaultUrl            = getStringParam("vaultUrl")
  private val secretPath          = getStringParam("secretPath")
  private val roleId              = getStringParam("roleId")
  private val secretId            = getStringParam("secretId")
  private val keys                = List("k1", "k2", "k3")
  val vaultFeeder: Feeder[String] = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)

  //how to combine together 2 or more feeders
  //as result we get feeder with 3 params: digit, string, phone
  val gluedTogetherFeeder: Feeder[Any] = digitFeeder ** stringFeeder ** phoneFeeder

  //transform values of this Feeder
  val finiteRandomDigitsWithTransform: FeederBuilderBase[Int]#F#F = RandomDigitFeeder("randomDigit")
    .toFiniteLength(20)
    .convert { case (k, v) => k -> v.toString }
    .circular

  //transform List to Feeder
  val list2feeder: FeederBuilderBase[Int]#F = List(1, 2, 3).toFeeder("listId").circular

  // string sequentially generated from the specified pattern
  val regexString: Feeder[String] = RegexFeeder("regex", "[a-zA-Z0-9]{8}")

}
