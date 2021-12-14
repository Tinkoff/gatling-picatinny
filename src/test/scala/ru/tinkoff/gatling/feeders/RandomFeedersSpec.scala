package ru.tinkoff.gatling.feeders

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.utils.Brackets

import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{LocalDateTime, ZoneId}

class RandomFeedersSpec extends AnyFlatSpec with Matchers {

  val positiveInt = Gen.posNum[Int]

  val rndString = Gen.alphaNumStr

  val datePattern: String      = "yyyy-MM-dd"
  val datePatternRegex: String = """\d{4}-\d{2}-\d{2}"""

  val dateFrom: LocalDateTime = LocalDateTime.now()

  val timezone: ZoneId = ZoneId.systemDefault()

  val unit: TemporalUnit = ChronoUnit.DAYS

  // phone generators
  val rndNumStr    = (n: Int) => Gen.listOfN(n, Gen.numChar).map(_.mkString)
  val rndBrackets  = Gen.oneOf(Brackets.Round, Brackets.Square, Brackets.Curly, Brackets.None)
  val rndDelimiter = Gen.oneOf("", "-", " ")
  val phonePattern = """(\+?\d{1,3}[\(\[\{]?(?!0{3})\d{3}[\)\]\}]?\d{3}-?\s?\d{2}-?\s?\d{2})"""

  val uuidPattern = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"

  val regexPattern = "[a-z0-9]{9}"

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
    forAll(rndString) { (paramName) =>
      RandomDigitFeeder(paramName)
        .take(50)
        .forall(r => r(paramName).isInstanceOf[Int])
    }.check()
  }
//
//  it should "create RandomPhoneFeeder with specified parameters" in {
//    forAll(rndString, rndNumStr(1), rndNumStr(3), rndDelimiter, rndBrackets) {
//      (paramName, countryCode, regionCode, delimiter, rndBrackets) =>
//        RandomPhoneFeeder(paramName, countryCode, regionCode, delimiter, rndBrackets)
//          .take(50)
//          .forall(r => r(paramName).matches(phonePattern))
//    }.check()
//  }

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
    forAll(rndString) { (paramName) =>
      RandomUUIDFeeder(paramName)
        .take(50)
        .forall(r => r(paramName).matches(uuidPattern))
    }.check()
  }

  it should "create RegexFeeder with specified regex pattern" in {
    forAll(rndString) { (paramName) =>
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

}
