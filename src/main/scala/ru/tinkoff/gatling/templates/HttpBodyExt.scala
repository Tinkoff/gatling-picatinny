package ru.tinkoff.gatling.templates

import io.gatling.core.Predef._
import io.gatling.core.session.el._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.templates.Syntax._

/** This extension give ability to write something like this
  *
  * {{{
  * http("PostData")
  * .post(url)
  * .jsonBody(
  *   "id" - 23,                    // in json - "id" : 23
  *   "name",                       // in json it interpreted as - "name" : get value from session variable ${name}
  *   "project" - (                 // in json - "project" : { ... }
  *     "id" ~ "projectId",         // in json - "id" : value from session var ${projectId}
  *     "name" - "Super Project",   // in json - "name": "Super Project"
  *     "sub" > ( 1,2,3,4,5,6)      // in json - "sub" : [ 1,2,3,4,5,6 ]
  *     )
  *   )
  * }}}
  */
object HttpBodyExt {
  implicit class BodyOps(val httpRequestBuilder: HttpRequestBuilder) extends AnyVal {
    def body(string: String): HttpRequestBuilder = httpRequestBuilder.body(StringBody(string.el[String]))

    def jsonBody(fs: Field*): HttpRequestBuilder =
      httpRequestBuilder
        .body(
          StringBody(makeJson(fs: _*).el[String]),
        )
        .asJson

    def xmlBody(fs: Field*): HttpRequestBuilder =
      httpRequestBuilder
        .body(
          StringBody(makeXml(fs: _*).el[String]),
        )
        .asXml
  }
}
