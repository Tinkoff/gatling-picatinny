package ru.tinkoff.gatling.profile

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck._
import ru.tinkoff.gatling.profile.http.{HttpProfileConfig, HttpRequestConfig}

class HttpProfileSpec extends AnyFlatSpec with Matchers {

  val httpRequestsGen: Gen[HttpRequestConfig] = for {
    requestName <- Gen.alphaStr
    method      <- Gen.oneOf("POST", "GET", "PUT", "DELETE")
    url         <- Gen.alphaStr
    prob        <- Gen.posNum[Double]
    body        <- Gen.option[String](Gen.alphaNumStr)
  } yield HttpRequestConfig(name = requestName, url = url, method = method, probability = prob, body = body)

  val httpProfileGen: Gen[HttpProfileConfig] = for {
    name <- Gen.alphaStr
    profile <- for {
      requests <- httpRequestsGen
      _        <- (0 to 50)
    } yield requests
  } yield HttpProfileConfig(name, profile.toList)

  implicit lazy val profileArbitrary: Arbitrary[HttpProfileConfig] = Arbitrary(httpProfileGen)

  it should "generate ScenarioBuilder from config" in {
    forAll { profileConfig: HttpProfileConfig =>
      (profileConfig.name.nonEmpty &&
        profileConfig.profile.forall(r => r.url.nonEmpty && r.name.nonEmpty && r.probability > 0.0 && r.probability < 100.0)) ==>
        HttpProfileConfig(profileConfig.name, profileConfig.profile).toRandomScenario.name.equals(profileConfig.name)
    }.check
  }

//  it should "build requests from config" in {
//    forAll { profileConfig: ProfileConfig =>
//      (profileConfig.name.nonEmpty &&
//        profileConfig.profile.forall(r => r.url.nonEmpty && r.requestName.nonEmpty && r.prob > 0.0 && r.prob < 100.0)) ==> {
//        new HttpProfileMock(profile.name, profile).buildRequest(profile.requests.head)._1 == profile.requests.head.prob
//      }
//    }.check
//  }

}