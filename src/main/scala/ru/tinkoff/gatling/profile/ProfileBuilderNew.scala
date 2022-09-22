package ru.tinkoff.gatling.profile

import io.circe.yaml._
import io.circe.generic.auto._
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.utils.IntensityConverter.getIntensityFromString

import scala.io.Source

case class Params(method: Option[String], path: Option[String], headers: Option[List[String]], body: Option[String])

case class Request(request: Option[String], intensity: Option[String], groups: Option[List[String]], params: Option[Params]) {

  val requestIntensity: Double = getIntensityFromString(intensity.getOrElse("0 rps"))

  def toRequest: HttpRequestBuilder = {
    val regexHeader = """(.+): (.+)""".r
    val requestPrep = for {
      requestParams  <- params
      requestUri     <- requestParams.path
      requestName    <- request
      requestMethod  <- requestParams.method
      requestBody    <- requestParams.body
      requestHeaders <- requestParams.headers
    } yield http(requestName)
      .httpRequest(requestMethod, requestUri)
      .body(StringBody(requestBody))
      .headers(requestHeaders.map { case regexHeader(a, b) => (a, b) }.toMap)
    requestPrep.getOrElse(throw new NoSuchElementException("Request parse error"))
  }

  def toExec: ChainBuilder            = exec(toRequest)
  def toTuple: (Double, ChainBuilder) = (requestIntensity, toExec)

}

case class OneProfile(name: Option[String], period: Option[String], protocol: Option[String], profile: Option[List[Request]]) {

  def toRandomScenario: ScenarioBuilder = {
    val requests     = profile.get.map(request => request.toTuple)
    val intensitySum = requests.foldLeft(0.0) { case (sum, (intensity, _)) => sum + intensity }
    val prepRequests =
      requests.foldLeft(List.empty[(Double, ChainBuilder)]) { case (sum, (intensity, chain)) =>
        sum :+ (100 * intensity / intensitySum, chain)
      }
    scenario(name.getOrElse("Scenario"))
      .randomSwitch(prepRequests: _*)
  }

}

case class Metadata(name: Option[String], description: Option[String])

case class Yaml(apiVersion: Option[String], kind: Option[String], metadata: Option[Metadata], spec: Option[List[OneProfile]]) {

  def selectProfile(profileName: String): OneProfile = {
    val profileList = spec.getOrElse(throw new NoSuchElementException("No profiles in spec"))
    profileList.filter(_.name.getOrElse(throw new NoSuchElementException(s"No such profile: $profileName")) == profileName).head
  }

}

object ProfileBuilderNew {

  def buildFromYaml(path: String): Yaml = {
    val bufferedSource = Source.fromResource(path)
    val yamlContent    = bufferedSource.mkString
    bufferedSource.close
    val yamlParsed     = parser.parse(yamlContent).flatMap(json => json.as[Yaml])
    yamlParsed match {
      case Right(yaml) => yaml
      case Left(error) => throw error
    }
  }

}
