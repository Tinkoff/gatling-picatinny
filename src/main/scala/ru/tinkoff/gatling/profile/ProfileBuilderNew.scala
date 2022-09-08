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
    val uri: String                               = params
      .getOrElse(throw new NoSuchElementException("No params in request"))
      .path
      .getOrElse(throw new NoSuchElementException("No path in params"))
    val requestName                               = request.getOrElse(uri)
    val requestMethod: String                     =
      params.getOrElse(throw new NoSuchElementException("No params in request")).method.getOrElse("GET")
    val requestBody: Option[String]               = params.getOrElse(throw new NoSuchElementException("No params in request")).body
    val requestHeaders: List[String]              =
      params.getOrElse(throw new NoSuchElementException("No params in request")).headers.getOrElse(List[String]())
    val regexHeader                               = """(.+): (.+)""".r
    val requestParsedHeaders: Map[String, String] = requestHeaders.map { case regexHeader(a, b) => (a, b) }.toMap
    requestMethod match {
      case "GET"    => http(requestName).get(uri).headers(requestParsedHeaders)
      case "POST"   => http(requestName).post(uri).body(StringBody(requestBody.getOrElse(""))).headers(requestParsedHeaders)
      case "PUT"    => http(requestName).put(uri).body(StringBody(requestBody.getOrElse(""))).headers(requestParsedHeaders)
      case "DELETE" => http(requestName).delete(uri).headers(requestParsedHeaders)
    }
  }

  def toExec: ChainBuilder            = exec(toRequest)
  def toTuple: (Double, ChainBuilder) = (requestIntensity, toExec)

}

case class OneProfile(name: Option[String], period: Option[String], protocol: Option[String], profile: Option[List[Request]]) {

  def toRandomScenario: ScenarioBuilder = {
    val requests     = this.profile.get.map(request => request.toTuple)
    val intensitySum = requests.foldLeft(0.0)((sum, item) => sum + item._1)
    val prepRequests =
      requests.foldLeft(List[(Double, ChainBuilder)]())((sum, item) => sum :+ (100 * item._1 / intensitySum, item._2))
    scenario(name.getOrElse(""))
      .randomSwitch(prepRequests: _*)
  }

}

case class Metadata(name: Option[String], description: Option[String])

case class Yaml(apiVersion: Option[String], kind: Option[String], metadata: Option[Metadata], spec: Option[List[OneProfile]]) {

  def selectProfile(profileName: String): OneProfile = {
    val profileList = this.spec.getOrElse(throw new NoSuchElementException("No profiles in spec"))
    profileList.filter(_.name.getOrElse(throw new NoSuchElementException(s"No such profile: $profileName")) == profileName).head
  }

}

object ProfileBuilderNew {

  def buildFromYaml(path: String): Yaml = {
    val bufferedSource = Source.fromFile(s"""${System.getProperty("user.dir")}/$path""")
    val yamlContent    = bufferedSource.mkString
    bufferedSource.close
    val yamlParsed     = parser.parse(yamlContent).flatMap(json => json.as[Yaml])
    yamlParsed match {
      case Right(yaml) => yaml
      case Left(error) => throw error
    }
  }

}
