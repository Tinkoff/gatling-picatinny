package ru.tinkoff.gatling.utils

import org.scalacheck.Gen
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
class RandomDataGeneratorsTest extends AnyFlatSpec with Matchers {

//  //uuid generator
  val uuidPattern = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"

//  //date generator
  val dateFormat              = "yyyy-MM-dd'T'HH:mm"
  val datePattern             = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
  val dateFrom: LocalDateTime = LocalDateTime.now()
  val dateTimezone: ZoneId    = ZoneId.systemDefault()
  val dateUnit                = ChronoUnit.DAYS

  it should "generate a string of the specified length" in {
    forAll(Gen.alphaNumStr.filter(_.nonEmpty), Gen.choose(1, 50)) { case (alphabet, len) =>
      val x = RandomDataGenerators.randomString(alphabet)(len)
      x.forall(c => alphabet.contains(c)) && len.equals(x.length)
    }.check()
  }

  it should "generate correct random UUID pattern" in {
    RandomDataGenerators.randomUUID.matches(uuidPattern)
  }.check()

  it should "generate correct random date pattern" in {
    forAll(Gen.choose(1, 100), Gen.choose(1, 100)) { (positiveOffset: Int, negativeOffset: Int) =>
      RandomDataGenerators
        .randomDate(positiveOffset, negativeOffset, dateFormat, dateFrom, dateUnit, dateTimezone)
        .matches(datePattern)
    }.check()
  }
}
