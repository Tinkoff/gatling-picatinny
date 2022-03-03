package ru.tinkoff.gatling.feeders

import io.gatling.core.config.GatlingConfiguration
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.utils.phone.{PhoneFormat, TypePhone}

import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{LocalDateTime, ZoneId}

class RandomFeedersSpec extends AnyFlatSpec with Matchers {

  val positiveInt: Gen[Int] = Gen.posNum[Int]

  val rndString: Gen[String] = Gen.alphaNumStr

  val datePattern: String      = "yyyy-MM-dd"
  val datePatternRegex: String = """\d{4}-\d{2}-\d{2}"""

  val dateFrom: LocalDateTime = LocalDateTime.now()

  val timezone: ZoneId = ZoneId.systemDefault()

  val unit: TemporalUnit = ChronoUnit.DAYS

  val uuidPattern = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"

  val regexPattern = "[a-z0-9]{9}"

  val regexPhonePattern                   = """^\+?(?:[0-9]?){6,14}[0-9]$"""
  val regexFilePhonePattern               = """^\+?\d(\(\d\d\d\)\d\d\d-?\d\d-?\d\d)$"""
  val regexRuMobilePhonePattern           = """^\+\d \d\d\d \d\d\d-\d\d-\d\d"""
  val regexRuMobilePhonePatternWithBraces = """^\+\d \(\d\d\d\) \d\d\d-\d\d-\d\d"""
  val regexTollFreePhonePattern           = """^\(8(00|33|44|55|66|77|88)\) \d{3}-\d{4}$"""

  val phoneFormatsFromFile: String = "phoneTemplates/ru.json"

  val ruMobileFormat: PhoneFormat = PhoneFormat(
    countryCode = "+7",
    length = 10,
    areaCodes = Seq("903", "906", "908"),
    prefixes = Seq("55", "81", "111"),
    format = "+X XXX XXX-XX-XX",
  )

  val ruMobileFormat2: PhoneFormat = PhoneFormat(
    countryCode = "+7",
    length = 10,
    areaCodes = Seq("903", "906", "908"),
    format = "+X (XXX) XXX-XX-XX",
  )

  it should "create RandomDateFeeder with specified date pattern" in {
    forAll(rndString, positiveInt, positiveInt) { (paramName, positive, negative) =>
      (positive > negative) ==>
        RandomDateFeeder(paramName, positive, negative, datePattern, dateFrom, unit, timezone)
          .take(50)
          .forall(r => r(paramName).matches(datePatternRegex))
    }.check()
  }

  it should "produce IllegalArgumentException when RandomDateFeeder creates with delta dates params <0" in {
    assertThrows[IllegalArgumentException] {
      RandomDateFeeder("paramName", -1, -1, datePattern, dateFrom, unit, timezone).next()
    }
  }

  it should "create RandomDateRangeFeeder with specified date pattern" in {
    forAll(rndString, rndString, positiveInt) { (paramNameFrom, paramNameTo, offset) =>
      (offset > 1 && paramNameFrom.nonEmpty && paramNameTo.nonEmpty) ==>
        RandomDateRangeFeeder(paramNameFrom, paramNameTo, offset, datePattern, dateFrom, unit, timezone)
          .take(50)
          .forall { r =>
            {
              r(paramNameFrom).matches(datePatternRegex)
              r(paramNameTo).matches(datePatternRegex)
            }
          }
    }.check()
  }

  it should "produce IllegalArgumentException when RandomDateRangeFeeder creates with offset param =<1" in {
    assertThrows[IllegalArgumentException] {
      RandomDateRangeFeeder("paramNameFrom", "paramNameTo", 1, datePattern, dateFrom, unit, timezone).next()
    }
  }

  it should "create RandomDigitFeeder" in {
    forAll(rndString) { paramName =>
      RandomDigitFeeder(paramName)
        .take(50)
        .forall(r => r(paramName).isInstanceOf[Int])
    }.check()
  }

  it should "create RandomStringFeeder with specified param length interval" in {
    forAll(rndString, positiveInt) { (paramName, length) =>
      RandomStringFeeder(paramName, length)
        .take(50)
        .forall(r => r(paramName).length == length)
    }.check()
  }

  it should "create RandomRangeStringFeeder with param length in the specified interval" in {
    forAll(rndString, positiveInt, positiveInt, rndString) { (paramName, lengthFrom, lengthTo, alphabet) =>
      (lengthFrom > 0 && lengthTo > 0 && lengthFrom < lengthTo && alphabet.nonEmpty) ==>
        RandomRangeStringFeeder(paramName, lengthFrom, lengthTo, alphabet)
          .take(50)
          .forall(r => (r(paramName).length >= lengthFrom) && (r(paramName).length < lengthTo))
    }.check()
  }

  it should "produce IllegalArgumentException when RandomRangeStringFeeder creates with length params =<0" in {
    assertThrows[IllegalArgumentException] {
      RandomRangeStringFeeder("paramName", 0, 0, "alphabet").next()
    }
  }

  it should "produce IllegalArgumentException when RandomRangeStringFeeder creates with empty alphabet string" in {
    assertThrows[IllegalArgumentException] {
      RandomRangeStringFeeder("paramName", 1, 2, "").next()
    }
  }

  it should "create RandomUUIDFeeder" in {
    forAll(rndString) { paramName =>
      RandomUUIDFeeder(paramName)
        .take(50)
        .forall(r => r(paramName).matches(uuidPattern))
    }.check()
  }

  it should "create RegexFeeder with specified regex pattern" in {
    forAll(rndString) { paramName =>
      RegexFeeder(paramName, regexPattern)
        .take(50)
        .forall(r => r(paramName).matches(regexPattern))
    }
  }

  it should "create SequentialFeeder" in {
    forAll(rndString, positiveInt, positiveInt) { (paramName, start, step) =>
      val list = SequentialFeeder(paramName, start, step).take(50).toList.flatten
      list.equals(list.sorted)
    }.check()
  }

  it should "create phoneFeeder with default PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName)
        .take(50)
        .forall { r => r(paramName).matches(regexPhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with custom PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, ruMobileFormat)
        .take(50)
        .forall { r => r(paramName).matches(regexRuMobilePhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with braces custom PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, ruMobileFormat2)
        .take(50)
        .forall { r => r(paramName).matches(regexRuMobilePhonePatternWithBraces) }
    }.check()
  }

  it should "create simple Toll Free format ignoring ruMobileFormat" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.TollFreePhoneNumber, ruMobileFormat)
        .take(50)
        .forall { r => r(paramName).matches(regexTollFreePhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with E164 PhoneNumber format ignoring ruMobileFormat" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.E164PhoneNumber, ruMobileFormat)
        .take(50)
        .forall { r => r(paramName).matches(regexPhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with E164 PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.E164PhoneNumber).take(50).forall { r => r(paramName).matches(regexPhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with Toll Free Phone Number format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.TollFreePhoneNumber)
        .take(50)
        .forall { r => r(paramName).matches(regexTollFreePhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with PhoneNumber format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile)
        .take(50)
        .forall { r => r(paramName).matches(regexFilePhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with Toll Free Phone Number format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile, TypePhone.TollFreePhoneNumber)
        .take(50)
        .forall { r => r(paramName).matches(regexTollFreePhonePattern) }
    }.check()
  }

  it should "create phoneFeeder with E164 PhoneNumber format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile, TypePhone.E164PhoneNumber)
        .take(50)
        .forall { r =>
          {
            r(paramName).matches(regexPhonePattern)
          }
        }
    }.check()
  }

  it should "create random snilsFeeder" in {
    forAll(rndString) { paramName =>
      RandomSNILSFeeder(paramName)
        .take(50)
        .forall { r => r(paramName).matches("\\d{11}") }
    }.check()
  }

  it should "create random panFeeder without BINs" in {
    forAll(rndString) { paramName =>
      RandomPANFeeder(paramName)
        .take(50)
        .forall { r => LuhnValidator.validate(r(paramName)) }
    }.check()
  }

  it should "create random panFeeder with BINs 6 numbers" in {
    forAll(rndString) { paramName =>
      RandomPANFeeder(paramName, "192837", "293847", "394857", "495867", "596871", "697881", "798192", "891726", "918273")
        .take(50)
        .forall { r => LuhnValidator.validate(r(paramName)) }
    }.check()
  }

  it should "create random panFeeder with BINs 8 numbers" in {
    forAll(rndString) { paramName =>
      RandomPANFeeder(
        paramName,
        "19292837",
        "29392847",
        "39492857",
        "49592867",
        "59692871",
        "69792881",
        "79892192",
        "89192726",
        "91892273",
      )
        .take(50)
        .forall { r => LuhnValidator.validate(r(paramName)) }
    }.check()
  }

  it should "create random rusPassportFeeder" in {
    forAll(rndString) { paramName =>
      RandomRusPassportFeeder(paramName)
        .take(50)
        .forall { r => r(paramName).matches("\\d{10}") }
    }.check()
  }

  it should "create random PSRNSPFeeder" in {
    forAll(rndString) { paramName =>
      RandomPSRNSPFeeder(paramName)
        .take(50)
        .forall { r => r(paramName).substring(0, 14).toLong % 13 % 10 == r(paramName).substring(14, 15).toInt }
    }.check()
  }

}
