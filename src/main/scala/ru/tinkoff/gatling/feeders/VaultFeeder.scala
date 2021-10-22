package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import org.json4s.JValue
import org.json4s.native.JsonMethods

import java.net.{URL, URLConnection}
import scala.io.Source
import scala.util.{Failure, Success, Using}

object VaultFeeder {

  private def getResponseFromConnection(connection: URLConnection): String = {
    Using(Source.fromInputStream(connection.getInputStream)) { source =>
      source.mkString
    } match {
      case Success(value) => value
      case Failure(_)     => ""
    }
  }

  def apply(vaultUrl: String, secretPath: String, roleId: String, secretId: String, keys: List[String]): Feeder[String] = {
    var resultMap: Map[String, String] = Map[String, String]()

    val approleLoginConnection: URLConnection = new URL(vaultUrl + "/v1/auth/approle/login").openConnection
    approleLoginConnection.setRequestProperty("Content-Type", "application/json")
    approleLoginConnection.setDoOutput(true)

    val body: Array[Byte] = s"""{"role_id":"$roleId","secret_id":"$secretId"}""".getBytes
    approleLoginConnection.getOutputStream.write(body, 0, body.length)

    var json: JValue         = JsonMethods.parse(getResponseFromConnection(approleLoginConnection))
    val client_token: JValue = json \ "auth" \ "client_token"
    val vaultToken: String   = client_token.values.toString

    val vaultSecretsConnection: URLConnection = new URL(vaultUrl + "/v1/" + secretPath).openConnection
    vaultSecretsConnection.setRequestProperty("X-Vault-Token", vaultToken)

    json = JsonMethods.parse(getResponseFromConnection(vaultSecretsConnection))
    val data: JValue = json \ "data"

    data.foldField[Any](0)(
      (_, kv) =>
        if (keys.contains(kv._1))
          resultMap += (kv._1 -> kv._2.values.toString))

    Iterator.continually(resultMap)
  }

}
