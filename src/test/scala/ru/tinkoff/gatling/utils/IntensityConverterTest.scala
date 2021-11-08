package ru.tinkoff.gatling.utils

import org.scalacheck.Arbitrary._
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IntensityConverterTest extends AnyFlatSpec with Matchers {

  import ru.tinkoff.gatling.utils.IntensityConverter._

  val intensityValue           = 3600.0
  val intensityString          = s"$intensityValue rps"
  val intensityIncorrectString = s"$intensityValue jpeg"

  it should "convert rph correctly" in {
    forAll { i: Double =>
      (i rph) equals (i / 3600.0)
    }.check()
  }
  it should "convert rpm correctly" in {
    forAll { i: Double =>
      (i rpm) equals (i / 60.0)
    }.check()
  }
  it should "convert rps correctly" in {
    forAll { i: Double =>
      (i rps) equals (i)
    }.check()
  }
  it should "display correctly intensity value from string" in {
    getIntensityFromString(intensityString) shouldBe (intensityValue)
  }
  it should "display correctly intensity value from incorrect string throw exception" in {
    assertThrows[IllegalArgumentException] { getIntensityFromString(intensityIncorrectString) }
  }
}
