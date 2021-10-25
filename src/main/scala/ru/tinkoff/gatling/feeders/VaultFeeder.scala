package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import org.json4s.JValue
import org.json4s.native.JsonMethods

import java.net.URI
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.time.Duration
import scala.util.{Failure, Success, Try}

object VaultFeeder {

  private def getResponse(uri: String, header1: String, header2: String, method: String, body: BodyPublisher): String =
    Try {
      val client: HttpClient = HttpClient
        .newBuilder()
        .build()

      val request = HttpRequest.newBuilder
        .uri(URI.create(uri))
        .header(header1, header2)
        .method(method, body)
        .timeout(Duration.ofMinutes(1))
        .build()

      client.send(request, BodyHandlers.ofString).body
    } match {
      case Success(response) => response
      case Failure(_)        => ""
    }

  def apply(vaultUrl: String, secretPath: String, roleId: String, secretId: String, keys: List[String]): Feeder[String] = {
    val body: String = s"""{"role_id":"$roleId","secret_id":"$secretId"}"""
    val vaultTokenResponse: String = getResponse(vaultUrl + "/v1/auth/approle/login",
      "Content-Type",
      "application/json",
      "POST",
      BodyPublishers.ofString(body))

    val vaultTokenJson: JValue = JsonMethods.parse(vaultTokenResponse)
    val client_token: JValue   = vaultTokenJson \ "auth" \ "client_token"
    val vaultToken: String     = client_token.values.toString

    val vaultDataResponse: String =
      getResponse(vaultUrl + "/v1/" + secretPath, "X-Vault-Token", vaultToken, "GET", BodyPublishers.noBody)

    val vaultDataJson: JValue = JsonMethods.parse(vaultDataResponse)
    val data: JValue          = vaultDataJson \ "data"

    keys match {
      case null => Iterator.continually(Map[String, String]())
      case _ =>
        Iterator.continually(
          data.foldField[Map[String, String]](Map[String, String]())(
            (res, kv) => {
              val (k, v) = kv
              keys.contains(k) match {
                case true  => res + (k -> v.values.toString)
                case false => Map[String, String]()
              }
            }
          )
        )
    }
  }

}
