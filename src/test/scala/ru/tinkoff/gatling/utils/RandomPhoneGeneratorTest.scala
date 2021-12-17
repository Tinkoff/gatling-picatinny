package ru.tinkoff.gatling.utils

import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RandomPhoneGeneratorTest extends AnyFlatSpec with Matchers {

  val rndNumStr    = (n: Int) => Gen.listOfN(n, Gen.numChar).map(_.mkString)
//  val rndBrackets  = Gen.oneOf(Brackets.Round, Brackets.Square, Brackets.Curly, Brackets.None)
  val rndDelimiter = Gen.oneOf("", "-", " ")
  val phonePattern = """(\+?\d{1,3}[\(\[\{]?(?!0{3})\d{3}[\)\]\}]?\d{3}-?\s?\d{2}-?\s?\d{2})"""

//  it should "generate correct random phone" in {
//    forAll(rndNumStr(1), rndNumStr(3), rndDelimiter, rndBrackets) { (countryCode, regionCode, delimiter, rndBrackets) =>
//      RandomPhoneGenerator.randomPhone(countryCode, Some(regionCode), delimiter, rndBrackets).matches(phonePattern)
//    }.check()
//  }
//
//  it should "generate correct random phone with regionCode = 000" in {
//    forAll(rndNumStr(1), "000", rndDelimiter, rndBrackets) { (countryCode, regionCode, delimiter, rndBrackets) =>
//      RandomPhoneGenerator.randomPhone(countryCode, Some(regionCode), delimiter, rndBrackets).matches(phonePattern)
//    }.check()
//  }

}
