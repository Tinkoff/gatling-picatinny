package ru.tinkoff.gatling.profile

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.Http
import ru.tinkoff.gatling.config.Request

class HttpProfile(profileName: String) extends Profile(profileName) {

  override def buildRequest(request: Request): (Double, ChainBuilder) = {

    val name        = request.requestName
    val url         = request.url
    val method      = request.method
    val probability = request.prob

    method match {
      case "POST"   => (probability, exec(Http(name).post(url)))
      case "GET"    => (probability, exec(Http(name).get(url)))
      case "PUT"    => (probability, exec(Http(name).put(url)))
      case "DELETE" => (probability, exec(Http(name).delete(url)))
    }
  }

  override def buildAssertions(): Unit = {}
}

object HttpProfile {
  def apply(profileName: String): HttpProfile = new HttpProfile(profileName)
}
