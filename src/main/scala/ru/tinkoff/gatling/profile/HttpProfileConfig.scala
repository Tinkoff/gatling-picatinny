package ru.tinkoff.gatling.profile
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.{Http, HttpRequestBuilder}

sealed trait HttpMethod
case object GET extends HttpMethod
case object POST extends HttpMethod
case object PUT extends HttpMethod
case object DELETE extends HttpMethod

private[gatling] case class HttpRequestConfig(name: String,
                                              probability: Double,
                                              url: String,
                                              method: String,
                                              body: Option[String]) extends RequestConfig {

  private def createRequest(): Http = http(name)
  private def addUrl(baseRequest: Http): HttpRequestBuilder = method.toUpperCase match {
    case "GET"    => baseRequest.get(url)
    case "POST"   => baseRequest.post(url).body(createBody())
    case "PUT"    => baseRequest.put(url).body(createBody())
    case "DELETE" => baseRequest.delete(url)
  }

  private def createBody(): Body with Expression[String] = {
    if (body.isDefined) StringBody(body.get) else StringBody("")
  }

  override def toExec: ChainBuilder = exec(addUrl(createRequest()))
}

case class HttpProfileConfig(name: String, profile: List[HttpRequestConfig]) extends ProfileConfig

object HttpProfileBuilder extends ProfileBuilder[HttpProfileConfig] {

}

