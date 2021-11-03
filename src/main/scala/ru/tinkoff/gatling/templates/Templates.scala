package ru.tinkoff.gatling.templates
import java.nio.file.{Files, Paths}

import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.jdk.CollectionConverters._

/** Send templates from same folder in resources by file name
  */
trait Templates {

  protected val templates: Map[String, Body with Expression[String]] =
    Files
      .list(
        Paths.get(Thread.currentThread.getContextClassLoader.getResource("templates").toURI),
      )
      .iterator()
      .asScala
      .map(_.toFile)
      .filter(_.isFile)
      .map(f => (f.getName.split('.').head, elBody(f.getCanonicalPath)))
      .toMap

  private def elBody(path: String) = ElFileBody(path)

  def postTemplate(templateName: String, targetUrl: String): HttpRequestBuilder =
    http(templateName)
      .post(targetUrl)
      .body(templates(templateName))
}
