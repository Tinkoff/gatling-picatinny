package ru.tinkoff.load.example.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder._
import ru.tinkoff.gatling.feeders._
import ru.tinkoff.gatling.utils.RandomDataGenerators
import ru.tinkoff.gatling.utils.phone._

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}

object Feeders {

  private val newYearDate                       = LocalDateTime.of(2020, 1, 1, 0, 0)
  private val goToWorkDate                      = LocalDateTime.of(2020, 1, 9, 9, 0)
  private val formatterShort: DateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd")

//  override implicit def configuration: GatlingConfiguration = GatlingConfiguration.loadForTest()

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
  val randomDigit: Feeder[Int]      = RandomDigitFeeder("randomDigit")
  val randomRangeInt: Feeder[Int]   = CustomFeeder[Int]("randomRangeInt", RandomDataGenerators.randomDigit(1, 50))
  val randomRangeFloat: Feeder[Any] =
    CustomFeeder("randomRangeFloat", RandomDataGenerators.randomDigit(1.toFloat, 10.toFloat))

  // random phone
  // +7 country code is default
  val randomPhone: Feeder[String] = RandomPhoneFeeder("randomPhone")

  // random USA phone
  val usaPhoneFormats: PhoneFormat =
    PhoneFormat(
      countryCode = "+1",
      length = 10,
      areaCodes = Seq("484", "585", "610"),
      prefixes = Seq("55", "81", "111"),
      format = "+X XXX XXX-XX-XX",
    )

  val randomUsaPhone: Feeder[String] = RandomPhoneFeeder("randomUsaPhone", usaPhoneFormats)

  val phoneFormatsFromFile: String   = "phoneTemplates/ru.json"
  val ruMobileFormat: PhoneFormat    = PhoneFormat(
    countryCode = "+7",
    length = 10,
    areaCodes = Seq("903", "906", "908"),
    prefixes = Seq("55", "81", "111"),
    format = "+X XXX XXX-XX-XX",
  )
  val ruCityPhoneFormat: PhoneFormat = PhoneFormat(
    countryCode = "8",
    length = 7,
    areaCodes = Seq("495", "495"),
    format = "X(XXX)XXX-XX-XX",
  )
  val ruPhoneFormats                 = List(ruMobileFormat, ruCityPhoneFormat)

  val simplePhoneNumber: Feeder[String]                 = RandomPhoneFeeder("simplePhoneFeeder")
  val randomPhoneNumberFromJson: Feeder[String]         =
    RandomPhoneFeeder("randomPhoneNumberFile", phoneFormatsFromFile)
  val randomPhoneNumber: Feeder[String]                 =
    RandomPhoneFeeder("randomPhoneNumber", ruPhoneFormats: _*)
  val randomE164PhoneNumberFromJson: Feeder[String]     =
    RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber)
  val randomE164PhoneNumber: Feeder[String]             =
    RandomPhoneFeeder("randomE164PhoneNumber", TypePhone.E164PhoneNumber, ruMobileFormat, ruCityPhoneFormat)
  val randomTollFreePhoneNumberFromJson: Feeder[String] =
    RandomPhoneFeeder("randomTollFreePhoneNumberFile", phoneFormatsFromFile, TypePhone.TollFreePhoneNumber)
  val randomTollFreePhoneNumber: Feeder[String]         =
    RandomPhoneFeeder("randomTollFreePhoneNumber", TypePhone.TollFreePhoneNumber, ruMobileFormat)

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
  private val vaultUrl                       = System.getenv("vaultUrl")
  private val secretPath                     = System.getenv("secretPath")
  private val roleId                         = System.getenv("roleId")
  private val secretId                       = System.getenv("secretId")
  private val keys                           = List("k1", "k2", "k3")
  val vaultFeeder: FeederBuilderBase[String] = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)

  // Get separated values feeder from the source
  // SeparatedValuesFeeder will return Vector(Map(HOSTS -> host11), Map(HOSTS -> host12), Map(USERS -> user11), Map(HOSTS -> host21), Map(HOSTS -> host22), Map(USERS -> user21), Map(USERS -> user22), Map(USERS -> user23))
  val vaultData: FeederBuilderBase[String]             = Vector(
    Map(
      "HOSTS" -> "host11,host12",
      "USERS" -> "user11",
    ),
    Map(
      "HOSTS" -> "host21,host22",
      "USERS" -> "user21,user22,user23",
    ),
  )
  val separatedValuesFeeder: FeederBuilderBase[String] =
    SeparatedValuesFeeder(None, vaultData.readRecords, ',')

  // how to combine together 2 or more feeders
  // as result we get feeder with 3 params: digit, string, phone
  val gluedTogetherFeeder: Feeder[Any] = digitFeeder ** stringFeeder ** phoneFeeder

  // transform values of this Feeder
  val finiteRandomDigitsWithTransform: FeederBuilderBase[Any] = RandomDigitFeeder("randomDigit")
    .toFiniteLength(20)
    .transform { case (k, v) => k -> v.toString }
    .circular

  // transform List to Feeder
  val list2feeder: FeederBuilderBase[Int] = List(1, 2, 3).toFeeder("listId").circular

  // string sequentially generated from the specified pattern
  val regexString: Feeder[String] = RegexFeeder("regex", "[a-zA-Z0-9]{8}")

  // random PAN
  val feederWithoutBinPAN: Feeder[String] = RandomPANFeeder("feederWithoutBinPAN")
  val feederPAN: Feeder[String]           = RandomPANFeeder("feederPAN", "421345", "541673")

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
