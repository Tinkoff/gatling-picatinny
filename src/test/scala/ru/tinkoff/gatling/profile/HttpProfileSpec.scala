package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._

class HttpProfileSpec extends AnyFlatSpec with Matchers {

  val httpRequestsGen: Gen[HttpRequestConfig] = for {
    requestName <- Gen.alphaStr
    method      <- Gen.oneOf("POST", "GET", "PUT", "DELETE")
    url         <- Gen.alphaStr
    prob        <- Gen.posNum[Double]
    body        <- Gen.option[String](Gen.alphaNumStr)
  } yield HttpRequestConfig(name = requestName, url = url, method = method, probability = prob, body = body)

  val httpProfileGen: Gen[Any] = for {
    name <- Gen.alphaStr
    profile <- for {
      requests <- httpProfileGen
      _        <- (0 to 50)
    } yield requests
  } yield HttpProfileConfig(name, profile.toSeq)

  implicit lazy val profileArbitrary = Arbitrary(httpProfileGen)

  class HttpProfileMock(name: String, profile: ProfileConfig) extends HttpProfileConfig {
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