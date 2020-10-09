package ru.tinkoff.gatling.influxdb

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._

class InfluxUtilsSpec extends AnyFlatSpec with Matchers {
  import InfluxMock._

  val bigIntGen: Gen[Int] = Gen.posNum[Int]

  val influx = InfluxMock()

  it should "build select last annotation query from status param" in {
    influx.query(startStatus).get.shouldEqual(getLastAnnotationQuery)
  }

  it should "return correct status value from last status value" in {
    forAll(bigIntGen) { value: Int =>
      influx.getStatusValue(startStatus, value).get.toInt == value + 1
    }.check
  }

  it should "get correct last status value from influxDb" in {
    influx.getLastStatusValue(getLastAnnotationQuery).get.shouldEqual(lastStartAnnotationValue)
  }

  it should "write correct annotation to influxDb" in {
    influx.writeAnnotationToInfluxdb(startStatus, lastStartAnnotationValue).get.statusCode.shouldEqual(204)
  }

  it should "add new annotation status value to influx" in {
    influx.addStatusAnnotation(startStatus).get.statusCode.shouldEqual(204)
  }
}
