package ru.tinkoff.gatling.feeders

import io.gatling.core.config.GatlingConfiguration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TransformFeedersSpec extends AnyFlatSpec with Matchers {
  
  private implicit val configuration: GatlingConfiguration = GatlingConfiguration.loadForTest()
  
  val stringForSeparatedValuesFeeder = "host11,host12"

  val seqStringForSeparatedValuesFeeder: Seq[String] = Seq("1\ttwo", "3\t4")

  val seqMapForSeparatedValuesFeeder: Seq[Map[String, String]] = Vector(
    Map(
      "HOSTS" -> "host11;host12",
      "USERS" -> "user11",
    ),
    Map(
      "HOSTS" -> "host21;host22",
      "USERS" -> "user21;user22;user23",
    ),
  )
  
  it should "SeparatedValuesFeeder extract from Seq[Map[String, String]]" in {
    SeparatedValuesFeeder(None, seqMapForSeparatedValuesFeeder, ';') shouldBe Vector(
      Map("HOSTS" -> "host11"),
      Map("HOSTS" -> "host12"),
      Map("USERS" -> "user11"),
      Map("HOSTS" -> "host21"),
      Map("HOSTS" -> "host22"),
      Map("USERS" -> "user21"),
      Map("USERS" -> "user22"),
      Map("USERS" -> "user23"),
    )
  }
  
  it should "SeparatedValuesFeeder extract from Seq[String]" in {
    SeparatedValuesFeeder("rndString", seqStringForSeparatedValuesFeeder, '\t') shouldBe Vector(
      Map("rndString" -> "1"),
      Map("rndString" -> "two"),
      Map("rndString" -> "3"),
      Map("rndString" -> "4"),
    )
  }
  
  it should "SeparatedValuesFeeder extract from String" in {
    SeparatedValuesFeeder("rndString", stringForSeparatedValuesFeeder, ',') shouldBe Array(
      Map("rndString" -> "host11"),
      Map("rndString" -> "host12"),
    )
  }

}
