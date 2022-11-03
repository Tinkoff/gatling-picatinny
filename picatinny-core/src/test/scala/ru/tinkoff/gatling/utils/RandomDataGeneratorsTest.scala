package ru.tinkoff.gatling.utils

import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
class RandomDataGeneratorsTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

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
      x.foreach(c => assert(alphabet.contains(c) && len.equals(x.length)))
    }
  }

  it should "generate correct random UUID pattern" in {
    RandomDataGenerators.randomUUID should fullyMatch regex uuidPattern
  }

  it should "generate correct random date pattern" in {
    forAll(Gen.choose(1, 100), Gen.choose(1, 100)) { (positiveOffset: Int, negativeOffset: Int) =>
      RandomDataGenerators
        .randomDate(
          positiveOffset,
          negativeOffset,
          dateFormat,
          dateFrom,
          dateUnit,
          dateTimezone,
        ) should fullyMatch regex datePattern
    }
  }
}
