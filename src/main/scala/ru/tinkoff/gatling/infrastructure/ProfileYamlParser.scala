package ru.tinkoff.load.stargazer.infrastructure

import zio._
import io.circe.yaml.parser
import io.circe.generic.auto._
import zio.console._

import java.nio.file.{Files, Path}
import scala.io.Source

final case class ReplaceRules(regexp: String, replace: String)

object Utils {

  def replaceVariables(path: String, renamingRules: List[ReplaceRules]): String =
    renamingRules.foldLeft(path) { case (afterReplace, renamingRules: ReplaceRules) =>
      afterReplace.replaceAll(renamingRules.regexp, renamingRules.replace)
    }

  def stringToCamelCase(s: String): String = {
    val l = s.split("[\\W_]+|(?<=[a-z])(?=[A-Z][a-z])").map(_.toLowerCase)
    l(0) + l.tail.map(_.capitalize).mkString
  }

  def checkFileExists(filePath: String, fileName: String): Boolean =
    Files.exists(Path.of(filePath, fileName))

  def readFromResource(filename: String): Task[String] = {
    Managed
      .fromAutoCloseable(ZIO.effect(Source.fromFile(s"""${System.getProperty("user.dir")}/$filename""")))
      .use(x => Task(x.mkString))
  }
}

case class Metadata(name: Option[String], description: Option[String])
case class Params(method: Option[String], path: Option[String], headers: Option[List[String]], body: Option[String])
case class Request(request: Option[String], intensity: Option[String], groups: Option[String], params: Option[Params])
case class OneProfile(
    name: Option[String],
    period: Option[String],
    protocol: Option[String],
    profile: Option[List[Request]],
)

case class FullRequest(
    method: Option[String],
    request: Option[String],
    intensity: Option[String],
    name: Option[String],
    headers: Option[List[String]],
    body: Option[String],
    groups: Option[String],
)

case class Yaml(apiVersion: Option[String], kind: Option[String], metadata: Option[Metadata], spec: Option[List[OneProfile]])

object ProfileYamlParser {

  private def getPaths(yaml: Yaml): List[Params] = {
    val spec = yaml.spec.getOrElse(List.empty)

    val paths = for {
      profile <- spec
      request <- profile.profile.getOrElse(List.empty)
      params  <- request.params
      path    <- params.path
    } yield Params(params.method, Some(path), params.headers, params.body)
    paths
  }

  private def getRequests(yaml: Yaml): List[FullRequest] = {
    val spec     = yaml.spec.getOrElse(List.empty)
    val metadata = yaml.metadata.getOrElse(Metadata(Option("sampleName"), Option("sampleDescription")))
    val paths    = for {
      profile <- spec
      request <- profile.profile.getOrElse(List.empty)
      params  <- request.params
    } yield FullRequest(
      params.method,
      request.request,
      request.intensity,
      metadata.name,
      params.headers,
      params.body,
      request.groups,
    )
    paths
  }

  def apply(file: String): Task[List[FullRequest]] = {
    val profileFile = Utils.readFromResource(file)

    profileFile >>= { file =>
      Task
        .fromEither(
          parser
            .parse(file)
            .flatMap(json => json.as[Yaml]),
        )
        .foldM(
          error => putStrLn(s"Could not parse profile file with error: $error").provideLayer(Console.live) *> Task(List.empty),
//          yaml => Task.succeed(getPaths(yaml)),
          yaml => Task.succeed(getRequests(yaml)),
        )
    }
  }
}
