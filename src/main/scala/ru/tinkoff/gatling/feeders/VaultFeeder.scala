package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import org.json4s.JValue
import org.json4s.native.JsonMethods

import java.io.OutputStream
import java.net.{HttpURLConnection, URL}
import scala.io.Source.fromInputStream

object VaultFeeder {

  def apply(vaultUrl: String, secretPath: String, roleId: String, secretId: String, keys: List[String]): Feeder[String] = {
    var resultMap: Map[String, String] = Map[String, String]()

    var url: URL               = new URL(vaultUrl + "/v1/auth/approle/login")
    var con: HttpURLConnection = url.openConnection.asInstanceOf[HttpURLConnection]
    con.setRequestMethod("POST")
    con.setRequestProperty("Content-Type", "application/json")
    con.setDoOutput(true)

    val body: String       = s"""{"role_id":"$roleId","secret_id":"$secretId"}"""
    val os: OutputStream   = con.getOutputStream
    val input: Array[Byte] = body.getBytes
    os.write(input, 0, input.length)

    var response: String     = fromInputStream(con.getInputStream).mkString
    var json: JValue         = JsonMethods.parse(response)
    val client_token: JValue = json \ "auth" \ "client_token"
    val vaultToken: String   = client_token.values.toString

    url = new URL(vaultUrl + "/v1/" + secretPath)
    con = url.openConnection.asInstanceOf[HttpURLConnection]
    con.setRequestMethod("GET")
    con.setRequestProperty("X-Vault-Token", vaultToken)

    response = fromInputStream(con.getInputStream).mkString
    json = JsonMethods.parse(response)
    val data: JValue = json \ "data"

    try {
      val vaultMap: Map[String, Any] = data.values.asInstanceOf[Map[String, Any]]

      for (k: String <- keys) {
        if (vaultMap.contains(k)) {
          resultMap += (k -> vaultMap(k).toString)
        }
      }
      Iterator.continually(resultMap)
    } catch {
      case _: Exception =>
    }
    Iterator.continually(resultMap)
  }

}
