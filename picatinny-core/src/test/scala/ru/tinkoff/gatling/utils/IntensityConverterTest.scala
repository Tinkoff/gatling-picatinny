package ru.tinkoff.gatling.utils

import org.scalacheck.Arbitrary._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class IntensityConverterTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  import ru.tinkoff.gatling.utils.IntensityConverter._

  val intensityValue           = 3600.0
  val intensityString          = s"$intensityValue rps"
  val intensityIncorrectString = s"$intensityValue jpeg"

  it should "convert rph correctly" in {
    forAll { i: Double =>
      (i rph) shouldBe (i / 3600.0)
    }
  }
  it should "convert rpm correctly" in {
    forAll { i: Double =>
      (i rpm) shouldBe (i / 60.0)
    }
  }
  it should "convert rps correctly" in {
    forAll { i: Double =>
      (i rps) shouldBe i
    }
  }
  it should "display correctly intensity value from string" in {
    getIntensityFromString(intensityString) shouldBe intensityValue
  }

  it should "display correctly intensity value from incorrect string throw exception" in {
    assertThrows[IllegalArgumentException] { getIntensityFromString(intensityIncorrectString) }
  }
}
