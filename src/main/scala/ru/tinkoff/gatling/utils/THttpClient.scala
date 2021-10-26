package ru.tinkoff.gatling.utils

import java.net.URI
import java.net.http.HttpClient.Redirect
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.time.Duration

case class THttpClient(followRedirects: String = "NEVER", connectTimeoutInSeconds: Long = 3000) {

  private val jsonContentType: String = "application/json"
  private val client: HttpClient      = buildClient()

  def buildClient(): HttpClient = {
    val client: HttpClient = HttpClient
      .newBuilder()
      .connectTimeout(Duration.ofSeconds(connectTimeoutInSeconds))
      .followRedirects(Redirect.valueOf(followRedirects))
      .build()

    client
  }

  def GET(uri: String, headers: Seq[String] = Seq.empty[String]): HttpResponse[String] = {
    val request: HttpRequest = HttpRequest
      .newBuilder()
      .uri(URI.create(uri))
      .headers(headers: _*)
      .build()

    client.send(request, HttpResponse.BodyHandlers.ofString)
  }

  def POSTJson(uri: String, json: String, headers: Seq[String] = Seq.empty[String]): HttpResponse[String] = {
    val hdrs: Seq[String] = Seq("Content-Type", s"""$jsonContentType""") ++ headers

    val request: HttpRequest = HttpRequest
      .newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(json))
      .uri(URI.create(uri))
      .headers(hdrs: _*)
      .build()

    client.send(request, HttpResponse.BodyHandlers.ofString)
  }

}
