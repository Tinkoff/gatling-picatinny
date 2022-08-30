package ru.tinkoff.gatling.profile.http

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.profile.RequestConfig

// method: HttpMethodConfig
case class HttpRequestConfig(name: String, probability: Double, method: String, url: String, body: Option[String])
    extends RequestConfig {

  def toRequest: HttpRequestBuilder = method match {
    case "GET"    => http(name).get(url)
    case "POST"   => http(name).post(url).body(StringBody(body.getOrElse("")))
    case "PUT"    => http(name).put(url).body(StringBody(body.getOrElse("")))
    case "DELETE" => http(name).delete(url)
  }

  override def toExec: ChainBuilder            = exec(toRequest)
  override def toTuple: (Double, ChainBuilder) = (probability, toExec)

}
