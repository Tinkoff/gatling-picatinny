package ru.tinkoff.gatling.profile

import io.circe.yaml._
import io.circe.generic.auto._
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.utils.IntensityConverter.getIntensityFromString

import cats.syntax.either._

import java.io.FileNotFoundException
import java.nio.file.Paths
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

case class Params(method: String, path: String, headers: Option[List[String]], body: Option[String])

case class Request(request: String, intensity: String, groups: Option[List[String]], params: Params) {

  val requestIntensity: Double     = getIntensityFromString(intensity)
  val regexHeader: Regex           = """(.+?): (.+)""".r
  val requestBody: String          = params.body.getOrElse("")
  val requestHeaders: List[String] = params.headers.getOrElse(List.empty[String])

  def toRequest: HttpRequestBuilder = {
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
    spec.profiles
      .find(_.name == profileName)
      .getOrElse(throw new NoSuchElementException(s"Selected wrong profile: $profileName"))
  }
}

object ProfileBuilderNew {

  final case class ProfileBuilderException(msg: String, cause: Throwable) extends Throwable(msg, cause, false, false)

  private def toProfileBuilderException(path: String): PartialFunction[Throwable, ProfileBuilderException] = {
    case e: FileNotFoundException => ProfileBuilderException(s"File not found $path", e)
    case e: io.circe.Error        => ProfileBuilderException(s"Incorrect file content in $path", e)
    case e: Throwable             => ProfileBuilderException(s"Unknown error", e)
  }

  def buildFromYaml(path: String): Yaml = {
    val attemptToParse = for {
      fullPath    <- sys.props
                       .get("user.dir")
                       .toRight(new NoSuchElementException("'user.dir' property not defined"))
                       .map(Paths.get(_, path))
      yamlContent <- Using(Source.fromFile(fullPath.toFile))(_.mkString).toEither
      parsed      <- parser.parse(yamlContent).flatMap(_.as[Yaml])
    } yield parsed

    attemptToParse.leftMap(toProfileBuilderException(path)).toTry.get
  }

  def buildFromYamlJava(path: String): Yaml = {
    try {
      val fullPath    = Paths.get(sys.props.toMap.apply("user.dir"), path)
      val yamlContent = Using.resource(Source.fromFile(fullPath.toFile))(_.mkString)
      parser.parse(yamlContent).flatMap(_.as[Yaml]).toTry.get
    } catch {
      case e: FileNotFoundException => throw ProfileBuilderException(s"File not found $path", e)
      case e: io.circe.Error        => throw ProfileBuilderException(s"Incorrect file content in $path", e)
      case e: Exception             => throw ProfileBuilderException(s"Unknown error", e)
    }
  }
}
