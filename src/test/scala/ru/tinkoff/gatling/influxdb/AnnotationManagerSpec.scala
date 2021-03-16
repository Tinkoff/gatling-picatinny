package ru.tinkoff.gatling.influxdb

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalacheck.Prop.{forAll, propBoolean}

class AnnotationManagerSpec extends AnyFlatSpec with Matchers {
  import InfluxDbMocks._

  it should "increment last status annotation value" in {
    forAll { b: BigDecimal =>
      (b >= BigDecimal(0) && b.isValidLong) ==> {
        val future = AnnotationManager.incrementStatusAnnotationValue(Start, lastStatusValueMock(List(b)))
        val result = Await.result(future, 100 milliseconds)
        result == b + 1
      }
    }.check()
  }

  it should "return annotation value 0 when new project is created" in {
    assert {
      val future = AnnotationManager.incrementStatusAnnotationValue(Start, lastStatusValueMock(List()))
      val result = Await.result(future, 100 milliseconds)
      result == BigDecimal(0)
    }
  }

  it should "when writing Stop, return the same annotation value as for Start" in {
    forAll { b: BigDecimal =>
      (b >= BigDecimal(0) && b.isValidLong) ==> {
        val future = AnnotationManager.incrementStatusAnnotationValue(Stop, lastStatusValueMock(List(b)))
        val result = Await.result(future, 100 milliseconds)
        result == b
      }
    }.check()
  }
}
