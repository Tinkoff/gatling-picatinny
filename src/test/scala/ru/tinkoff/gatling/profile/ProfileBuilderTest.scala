package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import java.io.FileNotFoundException

class ProfileBuilderTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  val profile1FromFile: String  = "src/test/resources/profileTemplates/profile1.yml"
  val parsedYaml: Yaml          = Yaml(
    "link.ru/v1alpha1",
    "PerformanceTestProfiles",
    Metadata("performance-test-profile", "performance test profile"),
    ProfileSpec(
      List(
        OneProfile(
          "maxPerf",
          "10.05.2022 - 20.05.2022",
          "http",
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
    ),
  )
  val parsedProfile: OneProfile = OneProfile(
    "maxPerf",
    "10.05.2022 - 20.05.2022",
    "http",
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

  it should "test expected exceptions if file not exists" in {
    val thrown = intercept[Exception] {
      ProfileBuilderNew.buildFromYaml("notExistsFile")
    }
    assert(thrown.getMessage == s"File notExistsFile was not found")
  }

}
