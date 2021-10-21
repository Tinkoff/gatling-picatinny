package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.util.EntityUtils
import org.json4s.JValue
import org.json4s.native.JsonMethods.parse

object VaultFeeder {

  private val client: CloseableHttpClient = HttpClientBuilder.create().build()

  private def httpRequestExec(request: HttpUriRequest): String = {
    val response: CloseableHttpResponse = client.execute(request)
    val entity: HttpEntity              = response.getEntity
    val results: String                 = EntityUtils.toString(entity, "UTF-8")

    results
  }

  def apply(vaultUrl: String, secretPath: String, roleId: String, secretId: String, keys: List[String]): Feeder[String] = {
    var resultMap: Map[String, String] = Map[String, String]()

    val postRequest: HttpPost = new HttpPost(vaultUrl + "/v1/auth/approle/login")
    postRequest.addHeader("Content-Type", "application/json")
    postRequest.setEntity(new StringEntity(s"""{"role_id":"$roleId","secret_id":"$secretId"}"""))

    var response: String     = httpRequestExec(postRequest)
    var json: JValue         = parse(response)
    val client_token: JValue = json \ "auth" \ "client_token"
    val vaultToken: String   = client_token.values.toString

    val get: HttpGet = new HttpGet(vaultUrl + "/v1/" + secretPath)
    get.addHeader("X-Vault-Token", vaultToken)

    response = httpRequestExec(get)
    json = parse(response)
    val data: JValue = json \ "data"

    try {
      val vaultMap: Map[Any, Any] = data.values.asInstanceOf[Map[Any, Any]]

      for (k: String <- keys) {
        if (vaultMap.contains(k)) {
          resultMap += (k -> vaultMap(k).toString)
        }
      }
      Iterator.continually(resultMap)
    } catch {
      case e: Exception =>
    }

    Iterator.continually(resultMap)
  }

}