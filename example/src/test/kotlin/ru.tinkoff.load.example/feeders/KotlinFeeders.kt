package ru.tinkoff.load.example.feeders

import ru.tinkoff.gatling.javaapi.Feeders.*
import ru.tinkoff.gatling.javaapi.utils.phone.PhoneFormatBuilder
import ru.tinkoff.gatling.javaapi.utils.phone.TypePhone
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Map
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.List

class KotlinFeeders {
    private val newYearDate = LocalDateTime.of(2020, 1, 1, 0, 0)
    private val goToWorkDate = LocalDateTime.of(2020, 1, 9, 9, 0)
    private val formatterShort = DateTimeFormatter.ofPattern("MM:dd")

    // date2pattern
    val timeShort = CurrentDateFeeder("timeShort", formatterShort)

    // random date +/- 5 minutes with "Australia/Sydney" timezone
    val ausTZ = ZoneId.of("Australia/Sydney")
    val timezoneRandom = CurrentDateFeeder("timeShort", formatterShort, ausTZ)

    // random date +/- 3 days from now
    val simpleRandomDate = RandomDateFeeder("simpleDate", 3, 3)

    // random date from newYearDate  with specified date string pattern
    val holidaysDate = RandomDateFeeder("holidays", 1, 5, "hh:mm:dd", newYearDate, ChronoUnit.MINUTES, ausTZ)

    // random time from 9:00 to 18:00
    val firstWorkDayHours =
        RandomDateFeeder("firstWorkDayHours", 9 * 60, 0, "HH:mm", goToWorkDate, ChronoUnit.MINUTES, ausTZ)

    // feeder provide two params:
    // startOfVacation = LocalDateTime.now()
    // endOfVacation = random date from now() to 14 days in the future
    val vacationDate = RandomDateRangeFeeder(
        "startOfVacation",
        "endOfVacation",
        14L,
        "yyyy-MM-dd",
        LocalDateTime.now(),
        ChronoUnit.DAYS,
        ausTZ
    )

    // random Int
    val randomDigit = RandomDigitFeeder("randomDigit")
    val randomRangeInt = CustomFeeder("randomRangeInt") { ThreadLocalRandom.current().nextInt(1, 100 + 1) }

    // random phone
    // +7 country code is default
    val randomPhone = RandomPhoneFeeder("randomPhone")

    // random USA phone
    val usaPhoneFormats = PhoneFormatBuilder.apply(
        "+1",
        10,
        listOf("484", "585", "610"),
        "+X XXX XXX-XX-XX",
        listOf("55", "81", "111")
    )

    val randomUsaPhone = RandomPhoneFeeder("randomUsaPhone", usaPhoneFormats)

    val phoneFormatsFromFile = "phoneTemplates/ru.json"
    val ruMobileFormat = PhoneFormatBuilder.apply(
        "+7",
        10,
        listOf("945", "946"),
        "+X XXX XXX-XX-XX",
        listOf("55", "81", "111")
    )
    val ruCityPhoneFormat = PhoneFormatBuilder.apply("+7", 10, listOf("945", "946"), "+X XXX XXX-XX-XX")

    val simplePhoneNumber = RandomPhoneFeeder("simplePhoneFeeder")
    val randomPhoneNumberFromJson = RandomPhoneFeeder("randomPhoneNumberFile", phoneFormatsFromFile)
    val randomPhoneNumber = RandomPhoneFeeder("randomPhoneNumber", ruMobileFormat, ruCityPhoneFormat)
    val randomE164PhoneNumberFromJson =
        RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber())
    val randomE164PhoneNumber = RandomPhoneFeeder(
        "randomE164PhoneNumber",
        TypePhone.E164PhoneNumber(),
        ruMobileFormat,
        ruCityPhoneFormat
    )
    val randomTollFreePhoneNumberFromJson = RandomPhoneFeeder(
        "randomTollFreePhoneNumberFile",
        phoneFormatsFromFile,
        TypePhone.TollFreePhoneNumber()
    )
    val randomTollFreePhoneNumber =
        RandomPhoneFeeder("randomTollFreePhoneNumber", TypePhone.TollFreePhoneNumber(), ruMobileFormat)

    // random alphanumeric String with specified length
    val randomString = RandomStringFeeder("randomString", 16)

    // random String generated from specified alphabet (or alphanumeric as default)
    // with random length in specified interval from 1 to 10
    val randomRangeString = RandomRangeStringFeeder("randomRangeString", 1, 10, "qwertyuiop*+-123")

    // random UUID
    val randomUuid = RandomUUIDFeeder("randomUuid")

    // sequence of Long numbers from one to Long.MaxValue with specified step = 2
    val sequenceLong = SequentialFeeder("sequenceLong", 1, 2)

    private fun myFunction(): String {
        val array = ByteArray(7)
        Random().nextBytes(array)
        return String(array, StandardCharsets.UTF_8)
    }

    // custom feeder from provided function
    val myCustomFeeder = CustomFeeder("myParam") { myFunction() }

    val digitFeeder = RandomDigitFeeder("digit")
    val stringFeeder = RandomStringFeeder("string")
    val phoneFeeder = RandomStringFeeder("phone")

    // Vault HC feeder
    private val vaultUrl = System.getenv("vaultUrl")
    private val secretPath = System.getenv("secretPath")
    private val roleId = System.getenv("roleId")
    private val secretId = System.getenv("secretId")
    private val keys = listOf("k1", "k2", "k3")
    val vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)

    // Get separated values feeder from the source
    // SeparatedValuesFeeder will return MutableIterator(Map(HOSTS -> host11), Map(HOSTS -> host12), Map(USERS -> user11), Map(HOSTS -> host21), Map(HOSTS -> host22), Map(USERS -> user21), Map(USERS -> user22), Map(USERS -> user23))
    val vaultData = listOf(
        Map.of<String, Any>("HOSTS", "host11,host12"),
        Map.of<String, Any>("USERS", "user21,user22,user23")
    )

    val separatedValuesFeeder = SeparatedValuesFeeder.apply(Optional.empty(), vaultData, ',')

    var sourceList = listOf(Map.of("HOSTS", "host11,host12"), Map.of("USERS", "user21,user22,user23"))
    var separatedValuesFeeder1 = SeparatedValuesFeeder.csv(null, sourceList)

    // string sequentially generated from the specified pattern
    val regexString = RegexFeeder("regex", "[a-zA-Z0-9]{8}")

    // random PAN
    val feederWithoutBinPAN = RandomPANFeeder("feederWithoutBinPAN")
    val feederPAN = RandomPANFeeder("feederPAN", "421345", "541673")

    // random ITN
    val feederNatITN = RandomNatITNFeeder("feederNatITN")
    val feederJurITN = RandomJurITNFeeder("feederJurITN")

    // random OGRN
    val feederOGRN = RandomOGRNFeeder("feederOGRN")

    // random PSRNSP
    val feederPSRNSP = RandomPSRNSPFeeder("feederPSRNSP")

    // random KPP
    val feederKPP = RandomKPPFeeder("feederKPP")

    // random SNILS
    val feederSNILS = RandomSNILSFeeder("randomSNILS")

    // random russian passport
    val feederRusPassport = RandomRusPassportFeeder("feederRusPassport")
}