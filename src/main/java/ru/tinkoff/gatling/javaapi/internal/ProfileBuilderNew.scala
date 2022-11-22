package ru.tinkoff.gatling.javaapi.internal

import ru.tinkoff.gatling.profile.Request
import io.gatling.javaapi.core.CoreDsl._
import io.gatling.javaapi.core._
import io.gatling.javaapi.http.HttpDsl._
import io.gatling.javaapi.http._

import scala.jdk.javaapi.CollectionConverters

object ProfileBuilderNew {
  def toRequest(r: Request): HttpRequestActionBuilder = {
    http(r.request)
      .httpRequest(r.params.method, r.params.path)
      .body(StringBody(r.requestBody))
      .headers(CollectionConverters.asJava(r.requestHeaders.map { case r.regexHeader(a, b) => (a, b) }.toMap))
  }

  def toExec(r: Request): ChainBuilder = io.gatling.javaapi.core.CoreDsl.exec(toRequest(r))

  def toTuple(r: Request): (java.lang.Double, ChainBuilder) = (r.requestIntensity, toExec(r))
}
