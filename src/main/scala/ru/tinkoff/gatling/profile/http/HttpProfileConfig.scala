package ru.tinkoff.gatling.profile.http

import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.{Http, HttpRequestBuilder}
import ru.tinkoff.gatling.profile.{ProfileConfig, RequestConfig}

case class HttpRequestConfig(name: String, probability: Double, url: String,  method: String, body: Option[String]) extends RequestConfig {

  private def createRequest: Http = http(name)

  private def addUrl(baseRequest: Http): HttpRequestBuilder = method.toUpperCase match {
    case "GET"    => baseRequest.get(url)
    case "POST"   => baseRequest.post(url).body(createBody)
    case "PUT"    => baseRequest.put(url).body(createBody)
    case "DELETE" => baseRequest.delete(url)
  }

  private def createBody: Body with Expression[String] = StringBody(body.getOrElse(""))

  override def toExec: ChainBuilder = exec(addUrl(createRequest))
}

case class HttpProfileConfig(name: String, profile: List[HttpRequestConfig]) extends ProfileConfig
