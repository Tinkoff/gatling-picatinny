package ru.tinkoff.gatling.utils


import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.UUID
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._
class RandomDataGeneratorsTest extends AnyFlatSpec with Matchers {

//  //uuid generator
  val uuidPattern = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"

//  //date generator
  val dateFormat     = "yyyy-MM-dd'T'HH:mm"
  val datePattern    = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
  val dateFrom       = LocalDateTime.now()
  val dateTimezone   = ZoneId.systemDefault()
  val dateUnit       = ChronoUnit.DAYS

  it should "generate a string of the specified length" in {
    forAll{ (alphabet:String, length:Int) =>
      (length < 100 && length > 0 && !alphabet.equals("") ) ==>
          RandomDataGenerators.randomString(alphabet)(length).length.equals(length)
    }.check
  }

  it should "generate correct random phone" in {
    forAll{ (countryCode:Int) =>
      (countryCode<100 && countryCode > -1) ==>
        RandomDataGenerators.randomPhone(s"+$countryCode").matches(s"\\+$countryCode\\d{10}")
    }.check
  }

  it should "generate correct random UUID pattern" in {
      RandomDataGenerators.randomUUID.matches(uuidPattern)
  }

  it should "generate correct random date pattern" in {
    forAll{ (positiveOffset:Int, negativeOffset:Int) =>
      (positiveOffset<101 && positiveOffset > 0 && negativeOffset<101 && negativeOffset>0) ==>
        RandomDataGenerators.randomDate(positiveOffset, negativeOffset, dateFormat, dateFrom, dateUnit, dateTimezone)
          .matches(datePattern)
    }.check
  }
}
