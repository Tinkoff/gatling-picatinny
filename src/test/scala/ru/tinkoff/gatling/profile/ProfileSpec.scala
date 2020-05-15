package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.config.{ProfileConfig, Request}
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._

class ProfileSpec extends AnyFlatSpec with Matchers {

  val requestsGen = for {
    requestName <- Gen.alphaStr
    method      <- Gen.oneOf("POST", "GET", "PUT", "DELETE")
    url         <- Gen.alphaStr
    prob        <- Gen.posNum[Double]
  } yield Request(requestName, method, url, prob)

  val profileGen = for {
    profileName <- Gen.alphaStr
    request <- for {
                requests <- requestsGen
                _        <- (0 to 50)
              } yield requests
  } yield ProfileConfig(profileName, request.toList)

  implicit lazy val profileArbitrary = Arbitrary(profileGen)

  class HttpProfileMock(profileName: String, profile: ProfileConfig) extends HttpProfile(profileName) {
    override lazy val profileConfig = profile
  }

  it should "generate ScenarioBuilder from config" in {
    forAll { profile: ProfileConfig =>
      (profile.name.nonEmpty &&
      profile.requests.forall(r => r.url.nonEmpty && r.requestName.nonEmpty && r.prob > 0.0 && r.prob < 100.0)) ==>
        new HttpProfileMock(profile.name, profile).build().name.equals(profile.name)
    }.check
  }

  it should "build requests from config" in {
    forAll { profile: ProfileConfig =>
      (profile.name.nonEmpty &&
      profile.requests.forall(r => r.url.nonEmpty && r.requestName.nonEmpty && r.prob > 0.0 && r.prob < 100.0)) ==> {
        new HttpProfileMock(profile.name, profile).buildRequest(profile.requests.head)._1 == profile.requests.head.prob
      }
    }.check
  }

}
