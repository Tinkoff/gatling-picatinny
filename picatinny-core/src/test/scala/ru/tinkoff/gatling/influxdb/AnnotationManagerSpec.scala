package ru.tinkoff.gatling.influxdb

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class AnnotationManagerSpec extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {
  import InfluxDbMocks._

  it should "increment last status annotation value" in {
    forAll { b: Long =>
      whenever(b >= 0 && b.isValidLong && b + 1 >= 0) {
        val future = AnnotationManager.incrementStatusAnnotationValue(Start, lastStatusValueMock(List(b)))
        val result = Await.result(future, 100 milliseconds)
        result shouldBe b + 1
      }
    }
  }

  it should "return annotation value 0 when new project is created" in {
    val future = AnnotationManager.incrementStatusAnnotationValue(Start, lastStatusValueMock(List()))
    val result = Await.result(future, 100 milliseconds)
    result shouldBe 0
  }

  it should "when writing Stop, return the same annotation value as for Start" in {
    forAll { b: Long =>
      whenever(b >= 0 && b.isValidLong && b + 1 >= 0) {
        val future = AnnotationManager.incrementStatusAnnotationValue(Stop, lastStatusValueMock(List(b)))
        val result = Await.result(future, 100 milliseconds)
        result shouldBe b
      }
    }
  }
}
