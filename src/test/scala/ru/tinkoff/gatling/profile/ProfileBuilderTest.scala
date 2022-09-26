package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ProfileBuilderTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  val profile1FromFile: String  = "profileTemplates/profile1.yml"
  val parsedYaml: Yaml          = Yaml(
    Some("link.ru/v1alpha1"),
    Some("PerformanceTestProfiles"),
    Some(Metadata(Some("performance-test-profile"), Some("performance test profile"))),
    List(
      OneProfile(
        "maxPerf",
        Some("10.05.2022 - 20.05.2022"),
        Some("http"),
        List(
          Request(
            "request-1",
            "100 rph",
            Some(List("Group1")),
            Params("POST", "/test/a", Some(List("greetings: Hello world!")), Some("""{"a": "b"}""")),
          ),
        ),
      ),
    ),
  )
  val parsedProfile: OneProfile = OneProfile(
    "maxPerf",
    Some("10.05.2022 - 20.05.2022"),
    Some("http"),
    List(
      Request(
        "request-1",
        "100 rph",
        Some(List("Group1")),
        Params("POST", "/test/a", Some(List("greetings: Hello world!")), Some("""{"a": "b"}""")),
      ),
    ),
  )

  it should "load profile yaml correctly" in {
    ProfileBuilderNew.buildFromYaml(profile1FromFile) shouldBe parsedYaml
  }

  it should "get profile from parsed yaml correctly" in {
    ProfileBuilderNew.buildFromYaml(profile1FromFile).selectProfile("maxPerf") shouldBe parsedProfile
  }

}
