package ru.tinkoff.load.example.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.{Feeder, FeederBuilderBase}
import ru.tinkoff.gatling.feeders._
import ru.tinkoff.gatling.utils.RandomDataGenerators
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object Feeders {

  private val newYearDate                       = LocalDateTime.of(2020, 1, 1, 0, 0)
  private val goToWorkDate                      = LocalDateTime.of(2020, 1, 9, 9, 0)
  private val formatterShort: DateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd")

  // date2pattern
  val timeShort: Feeder[String] = CurrentDateFeeder("timeShort", formatterShort)

  // random date +/- 5 minutes with "Australia/Sydney" timezone
  val ausTZ: ZoneId                  = ZoneId.of("Australia/Sydney")
  val timezoneRandom: Feeder[String] =
    RandomDateFeeder("timezoneRandom", 5, 5, "hh:mm:dd", unit = ChronoUnit.MINUTES, timezone = ausTZ)

  // random date +/- 3 days from now
  val simpleRandomDate: Feeder[String] = RandomDateFeeder("simpleDate", 3, 3)

  // random date from newYearDate  with specified date string pattern
  val holidaysDate: Feeder[String] = RandomDateFeeder("holidays", 8, 0, "yyyy-MM-dd'T'HH:mm", newYearDate, ChronoUnit.DAYS)

  // random time from 9:00 to 18:00
  val firstWorkDayHours: Feeder[String] =
    RandomDateFeeder("firstWorkDayHours", 9 * 60, 0, "HH:mm", goToWorkDate, ChronoUnit.MINUTES)

  // feeder provide two params:
  // startOfVacation = LocalDateTime.now()
  // endOfVacation = random date from now() to 14 days in the future
  val vacationDate: Feeder[String] =
    RandomDateRangeFeeder("startOfVacation", "endOfVacation", 14, "yyyy-MM-dd", LocalDateTime.now(), ChronoUnit.DAYS)

  // random Int
  val randomDigit: Feeder[Int]        = RandomDigitFeeder("randomDigit")
  val randomRangeInt: Feeder[Int]     = CustomFeeder[Int]("randomRangeInt", RandomDataGenerators.randomDigit(1, 50))
  val randomRangeFloat: Feeder[Any] =
    CustomFeeder("randomRangeFloat", RandomDataGenerators.randomDigit { (1.toFloat, 10.toFloat) })

  // random phone
  // +7 country code is default
  val randomPhone: Feeder[String]    = RandomPhoneFeeder("randomPhone")
  val randomUsaPhone: Feeder[String] = RandomPhoneFeeder("randomUsaPhone", "+1")

  // random alphanumeric String with specified length
  val randomString: Feeder[String] = RandomStringFeeder("randomString", 16)

  // random String generated from specified alphabet (or alphanumeric as default)
  // with random length in specified interval from 1 to 10
  val randomRangeString: Feeder[String] =
    RandomRangeStringFeeder("randomRangeString", 1, 10, "qwertyuiop*+-123")

  // random UUID
  val randomUuid: Feeder[String] = RandomUUIDFeeder("randomUuid")

  // sequence of Long numbers from one to Long.MaxValue with specified step = 2
  val sequenceLong: Feeder[Long] = SequentialFeeder("sequenceLong", 1, 2)

  private def myFunction: String = {
    scala.util.Random.shuffle(List("first", "second", "third")).head
  }

  // custom feeder from provided function
  val myCustomFeeder: Feeder[String] = CustomFeeder[String]("myParam", myFunction)

  private val digitFeeder  = RandomDigitFeeder("digit")
  private val stringFeeder = RandomStringFeeder("string")
  private val phoneFeeder  = RandomStringFeeder("phone")

  // Vault HC feeder
  private val vaultUrl            = System.getenv("vaultUrl")
  private val secretPath          = System.getenv("secretPath")
  private val roleId              = System.getenv("roleId")
  private val secretId            = System.getenv("secretId")
  private val keys                = List("k1", "k2", "k3")
  val vaultFeeder: Feeder[String] = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)

  // how to combine together 2 or more feeders
  // as result we get feeder with 3 params: digit, string, phone
  val gluedTogetherFeeder: Feeder[Any] = digitFeeder ** stringFeeder ** phoneFeeder

  // transform values of this Feeder
  val finiteRandomDigitsWithTransform: FeederBuilderBase[Int] = RandomDigitFeeder("randomDigit")
    .toFiniteLength(20)
    .convert { case (k, v) => k -> v.toString }
    .circular

  // transform List to Feeder
  val list2feeder: FeederBuilderBase[Int] = List(1, 2, 3).toFeeder("listId").circular

  // string sequentially generated from the specified pattern
  val regexString: Feeder[String] = RegexFeeder("regex", "[a-zA-Z0-9]{8}")

  // random PAN
  val feederPAN: Feeder[String] = RandomPANFeeder("feederPAN", List("421345", "541673"))

  // random ITN
  val feederNatITN: Feeder[String] = RandomNatITNFeeder("feederNatITN")
  val feederJurITN: Feeder[String] = RandomJurITNFeeder("feederJurITN")

  // random OGRN
  val feederOGRN: Feeder[String] = RandomOGRNFeeder("feederOGRN")

  // random PSRNSP
  val feederPSRNSP: Feeder[String] = RandomPSRNSPFeeder("feederPSRNSP")

  // random KPP
  val feederKPP: Feeder[String] = RandomKPPFeeder("feederKPP")

  // random SNILS
  val feederSNILS: Feeder[String] = RandomSNILSFeeder("randomSNILS")

  // random russian passport
  val feederRusPassport: Feeder[String] = RandomRusPassportFeeder("feederRusPassport")

}
