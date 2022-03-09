package ru.tinkoff.gatling.feeders

import org.scalacheck._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.tinkoff.gatling.utils.phone.{PhoneFormat, TypePhone}

import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{LocalDateTime, ZoneId}

class RandomFeedersSpec extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

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
      whenever(positive > negative) {
        RandomDateFeeder(paramName, positive, negative, datePattern, dateFrom, unit, timezone)
          .take(50)
          .foreach(record =>
            withClue(s"Invalid RandomDateFeeder with specified date pattern: $record, ") {
              record(paramName) should fullyMatch regex datePatternRegex
            },
          )
      }
    }
  }

  it should "produce IllegalArgumentException when RandomDateFeeder creates with delta dates params <0" in {
    assertThrows[IllegalArgumentException] {
      RandomDateFeeder("paramName", -1, -1, datePattern, dateFrom, unit, timezone).next()
    }
  }

  it should "create RandomDateRangeFeeder with specified date pattern" in {
    forAll(rndString, rndString, positiveInt) { (paramNameFrom, paramNameTo, offset) =>
      whenever(offset > 1 && paramNameFrom.nonEmpty && paramNameTo.nonEmpty) {
        RandomDateRangeFeeder(paramNameFrom, paramNameTo, offset, datePattern, dateFrom, unit, timezone)
          .take(50)
          .foreach { record =>
            withClue(s"Invalid RandomDigitFeeder: $record, ") {
              record(paramNameFrom) should fullyMatch regex datePatternRegex
              record(paramNameTo) should fullyMatch regex datePatternRegex
            }
          }
      }
    }
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
        .foreach(record =>
          withClue(s"Invalid RandomDigitFeeder: $record, ") {
            assert(record(paramName).isInstanceOf[Int])
          },
        )
    }
  }

  it should "create RandomStringFeeder with specified param length interval" in {
    forAll(rndString, positiveInt) { (paramName, length) =>
      RandomStringFeeder(paramName, length)
        .take(50)
        .foreach(record =>
          withClue(s"Invalid RandomStringFeeder with specified param length interval: $record, ") {
            assert(record(paramName).length == length)
          },
        )
    }
  }

  it should "create RandomRangeStringFeeder with param length in the specified interval" in {
    forAll(rndString, positiveInt, positiveInt, rndString) { (paramName, lengthFrom, lengthTo, alphabet) =>
      whenever(lengthFrom > 0 && lengthTo > 0 && lengthFrom < lengthTo && alphabet.nonEmpty) {
        RandomRangeStringFeeder(paramName, lengthFrom, lengthTo, alphabet)
          .take(50)
          .foreach(record =>
            withClue(s"Invalid RandomRangeStringFeeder with param length in the specified interval: $record, ") {
              assert((record(paramName).length >= lengthFrom) && (record(paramName).length < lengthTo))
            },
          )
      }
    }
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
        .foreach(record =>
          withClue(s"Invalid RandomUUIDFeeder: $record, ") {
            record(paramName) should fullyMatch regex uuidPattern
          },
        )
    }
  }

  it should "create RegexFeeder with specified regex pattern" in {
    forAll(rndString) { paramName =>
      RegexFeeder(paramName, regexPattern)
        .take(50)
        .foreach(record =>
          withClue(s"Invalid RegexFeeder with specified regex pattern: $record, ") {
            record(paramName) should fullyMatch regex regexPattern
          },
        )
    }
  }

  it should "create SequentialFeeder" in {
    forAll(rndString, positiveInt, positiveInt) { (paramName, start, step) =>
      val list = SequentialFeeder(paramName, start, step).take(50).toList.flatten
      withClue(s"Invalid SequentialFeeder: ${list.mkString(",")}, ") {
        assert(list.equals(list.sorted))
      }
    }
  }

  it should "create phoneFeeder with default PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with custom PhoneNumber format: $record, ") {
            record(paramName) should fullyMatch regex regexPhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with custom PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, ruMobileFormat)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with custom PhoneNumber format: $record, ") {
            record(paramName) should fullyMatch regex regexRuMobilePhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with braces custom PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, ruMobileFormat2)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with braces custom PhoneNumber format: $record, ") {
            record(paramName) should fullyMatch regex regexRuMobilePhonePatternWithBraces
          }
        }
    }
  }

  it should "create simple Toll Free format ignoring ruMobileFormat" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.TollFreePhoneNumber, ruMobileFormat)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid simple Toll Free format ignoring ruMobileFormat: $record, ") {
            record(paramName) should fullyMatch regex regexTollFreePhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with E164 PhoneNumber format ignoring ruMobileFormat" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.E164PhoneNumber, ruMobileFormat)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with E164 PhoneNumber format ignoring ruMobileFormat: $record, ") {
            record(paramName) should fullyMatch regex regexPhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with E164 PhoneNumber format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.E164PhoneNumber).take(50).foreach { record =>
        withClue(s"Invalid phoneFeeder with E164 PhoneNumber format: $record, ") {
          record(paramName) should fullyMatch regex regexPhonePattern
        }
      }
    }
  }

  it should "create phoneFeeder with Toll Free Phone Number format" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, TypePhone.TollFreePhoneNumber)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with Toll Free Phone Number format: $record, ") {
            record(paramName) should fullyMatch regex regexTollFreePhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with PhoneNumber format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with PhoneNumber format fromFile: $record, ") {
            record(paramName) should fullyMatch regex regexFilePhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with Toll Free Phone Number format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile, TypePhone.TollFreePhoneNumber)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with Toll Free Phone Number format fromFile: $record, ") {
            record(paramName) should fullyMatch regex regexTollFreePhonePattern
          }
        }
    }
  }

  it should "create phoneFeeder with E164 PhoneNumber format fromFile" in {
    forAll(rndString) { paramName =>
      RandomPhoneFeeder(paramName, phoneFormatsFromFile, TypePhone.E164PhoneNumber)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid phoneFeeder with E164 PhoneNumber format fromFile: $record, ") {
            record(paramName) should fullyMatch regex regexPhonePattern
          }
        }
    }
  }

  it should "create random snilsFeeder" in {
    forAll(rndString) { paramName =>
      RandomSNILSFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random snilsFeeder: $record, ") {
            record(paramName) should fullyMatch regex "\\d{11}"
          }
        }
    }
  }

  it should "create random panFeeder without BINs" in {
    forAll(rndString) { paramName =>
      RandomPANFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random panFeeder without BINs: $record, ") {
            assert(LuhnValidator.validate(record(paramName)))
          }
        }
    }
  }

  it should "create random panFeeder with BINs 6 numbers" in {
    forAll(rndString) { paramName =>
      RandomPANFeeder(paramName, "192837", "293847", "394857", "495867", "596871", "697881", "798192", "891726", "918273")
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random panFeeder with BINs 6 numbers: $record, ") {
            assert(LuhnValidator.validate(record(paramName)))
          }
        }
    }
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
        .foreach { record =>
          withClue(s"Invalid random panFeeder with BINs 8 numbers: $record, ") {
            assert(LuhnValidator.validate(record(paramName)))
          }
        }
    }
  }

  it should "create random rusPassportFeeder" in {
    forAll(rndString) { paramName =>
      RandomRusPassportFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random rusPassportFeeder: $record, ") {
            record(paramName) should fullyMatch regex "\\d{10}"
          }
        }
    }
  }

  it should "create random PSRNSPFeeder" in {
    forAll(rndString) { paramName =>
      RandomPSRNSPFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random PSRNSPFeeder: $record, ") {
            record(paramName).substring(0, 14).toLong % 13 % 10 shouldBe record(paramName).substring(14, 15).toInt
          }
        }
    }
  }

  it should "create random OGRNFeeder" in {
    forAll(rndString) { paramName =>
      RandomOGRNFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random OGRNFeeder: $record, ") {
            record(paramName).substring(0, 12).toLong % 11 % 10 shouldBe record(paramName).substring(12, 13).toInt
          }
        }
    }
  }

  it should "create random NatITNFeeder" in {
    forAll(rndString) { paramName =>
      RandomNatITNFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random NatITNFeeder: $record, ") {
            record(paramName) should fullyMatch regex "\\d{10}"
          }
        }
    }
  }

  it should "create random JurITNFeeder" in {
    forAll(rndString) { paramName =>
      RandomJurITNFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random JurITNFeeder: $record, ") {
            record(paramName) should fullyMatch regex "\\d{12}"
          }
        }
    }
  }

  it should "create random KPPFeeder" in {
    forAll(rndString) { paramName =>
      RandomKPPFeeder(paramName)
        .take(50)
        .foreach { record =>
          withClue(s"Invalid random KPPFeeder: $record, ") {
            record(paramName) should fullyMatch regex "\\d{9}"
          }
        }
    }
  }

}
