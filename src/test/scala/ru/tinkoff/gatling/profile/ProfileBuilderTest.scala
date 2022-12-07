package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import ru.tinkoff.gatling.profile.ProfileBuilderNew.ProfileBuilderException

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

  it should "Java test expected exceptions if file not exists" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYamlJava("notExistsFile")
    }
    assert(thrown.getMessage.contains("File not found notExistsFile"))
  }

  it should "Java test expected exceptions if file path not passed" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYamlJava("")
    }
    assert(thrown.getMessage.contains("File not found"))
  }

  it should "Java test expected exceptions if incorrect file content" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYamlJava("src/test/resources/profileTemplates/incorrectProfile.yml")
    }
    assert(thrown.getMessage.contains("Incorrect file content in src/test/resources/profileTemplates/incorrectProfile.yml"))
  }

  it should "test expected exceptions if file not exists" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYaml("notExistsFile")
    }
    assert(thrown.getMessage.contains("File not found notExistsFile"))
  }

  it should "test expected exceptions if file path not passed" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYaml("")
    }
    assert(thrown.getMessage.contains("File not found"))
  }

  it should "test expected exceptions if incorrect file content" in {
    val thrown = intercept[ProfileBuilderException] {
      ProfileBuilderNew.buildFromYaml("src/test/resources/profileTemplates/incorrectProfile.yml")
    }
    assert(thrown.getMessage.contains("Incorrect file content in src/test/resources/profileTemplates/incorrectProfile.yml"))
  }

}
