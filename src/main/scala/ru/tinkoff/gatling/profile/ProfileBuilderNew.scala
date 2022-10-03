package ru.tinkoff.gatling.profile

import io.circe
import io.circe.DecodingFailure
import io.circe.yaml._
import io.circe.generic.auto._
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.utils.IntensityConverter.getIntensityFromString

import scala.io.{BufferedSource, Source}
import scala.util.matching.Regex

case class Params(method: String, path: String, headers: Option[List[String]], body: Option[String])

case class Request(request: String, intensity: String, groups: Option[List[String]], params: Params) {

  val requestIntensity: Double = getIntensityFromString(intensity)

  def toRequest: HttpRequestBuilder = {
    val regexHeader: Regex           = """(.+?): (.+)""".r
    val requestBody: String          = params.body.getOrElse("")
    val requestHeaders: List[String] = params.headers.getOrElse(List.empty[String])
    http(request)
      .httpRequest(params.method, params.path)
      .body(StringBody(requestBody))
      .headers(requestHeaders.map { case regexHeader(a, b) => (a, b) }.toMap)
  }

  def toExec: ChainBuilder            = exec(toRequest)
  def toTuple: (Double, ChainBuilder) = (requestIntensity, toExec)

}

case class OneProfile(name: String, period: String, protocol: String, profile: List[Request]) {

  def toRandomScenario: ScenarioBuilder = {
    val requests: List[(Double, ChainBuilder)]     = profile.map(request => request.toTuple)
    val intensitySum: Double                       = requests.map { case (intensity, _) => intensity }.sum
    val prepRequests: List[(Double, ChainBuilder)] =
      requests.map { case (intensity, chain) => (100 * intensity / intensitySum, chain) }
    scenario(name)
      .randomSwitch(prepRequests: _*)
  }

}

case class Metadata(name: String, description: String)

case class ProfileSpec(profiles: List[OneProfile])

case class Yaml(apiVersion: String, kind: String, metadata: Metadata, spec: ProfileSpec) {

  def selectProfile(profileName: String): OneProfile = {
    spec.profiles.find(_.name == profileName).getOrElse(throw new NoSuchElementException(s"Selected wrong profile: $profileName"))
  }

}

object ProfileBuilderNew {

  def buildFromYaml(path: String): Yaml = {
    val bufferedSource: BufferedSource        = Source.fromFile(s"""${System.getProperty("user.dir")}/$path""")
    val yamlContent: String                   = bufferedSource.mkString
    bufferedSource.close
    val yamlParsed: Either[circe.Error, Yaml] = parser.parse(yamlContent).flatMap(json => json.as[Yaml])
    yamlParsed match {
      case Right(yaml)                     => yaml
      case Left(DecodingFailure(_, value)) =>
        throw new IllegalArgumentException(s"""Field "${value.head.productElement(0)}" is not filled""")
      case Left(error)                     => throw error
    }
  }

}
