package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.{Feeder, Record}
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, JValue}
import ru.tinkoff.gatling.utils.THttpClient

import java.util.Objects.requireNonNull

object VaultFeeder {

  def apply(vaultUrl: String, secretPath: String, roleId: String, secretId: String, keys: List[String]): Feeder[String] = {
    requireNonNull(keys, "Keys list must not be null")

    implicit val formats: DefaultFormats = org.json4s.DefaultFormats

    val body: String = s"""{"role_id":"$roleId","secret_id":"$secretId"}"""

    val vaultTokenResponse: String = THttpClient()
      .POSTJson(s"""$vaultUrl/v1/auth/approle/login""", body)
      .body()

    val vaultTokenJson: JValue = JsonMethods.parse(vaultTokenResponse)
    val client_token: JValue   = vaultTokenJson \ "auth" \ "client_token"
    val vaultToken: String     = client_token.values.toString

    val getHeaders: Seq[String]   = Seq("X-Vault-Token", s"""$vaultToken""")
    val vaultDataResponse: String = THttpClient()
      .GET(s"""$vaultUrl/v1/$secretPath""", getHeaders)
      .body()

    val vaultDataJson: JValue = JsonMethods.parse(vaultDataResponse)
    val data: Record[String]  = (vaultDataJson \ "data").extract[Map[String, String]]

    Iterator.continually(data.view.filterKeys(keys.contains).toMap)
  }

}
