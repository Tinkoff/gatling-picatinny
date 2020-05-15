package ru.tinkoff.gatling.influxdb

import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, JInt}
import requests.{RequestBlob, Response}

private[gatling] object InfluxUtils {
  def apply(influxUrl: String, db: String, rootPathPrefix: String): InfluxUtils =
    new InfluxUtils(influxUrl, db, rootPathPrefix)
}

private[gatling] class InfluxUtils(influxUrl: String, db: String, rootPathPrefix: String) {

  def get(url: String,
              params: Iterable[(String, String)],
              headers: Iterable[(String, String)] = Nil,
              data: RequestBlob = RequestBlob.EmptyRequestBlob): Response = {
    requests.get(url, params = params, headers = headers, data = data)
  }

  def post(url: String,
          params: Iterable[(String, String)],
          headers: Iterable[(String, String)] = Nil,
          data: RequestBlob = RequestBlob.EmptyRequestBlob): Response = {
    requests.post(url, params = params, headers = headers, data = data)
  }

  private implicit val formats: DefaultFormats = DefaultFormats

  def query(status: Status): Option[String] =
    Option(s"SELECT last(annotation_value) FROM $rootPathPrefix WHERE annotation='$status'")

  def getStatusValue(status: Status, lastStatusValue: BigInt): Option[BigInt] =
    Option(lastStatusValue).map { value =>
      status match {
        case Start => value + 1
        case _     => value
      }
    }

  def writeAnnotationToInfluxdb(status: Status, value: BigInt): Response = {
    val params  = Map("db"           -> db)
    val headers = Map("content-type" -> "text/xml")
    val data =
      s"""$rootPathPrefix,annotation=$status annotation_value=$value,annotation_time_ms=${System.currentTimeMillis}""".stripMargin
    post(s"$influxUrl/write", params, headers, data)
  }

  def getLastStatusValue(query: String): Option[BigInt] = {
    val response = get(s"$influxUrl/query", Map("db" -> db, "q" -> query))
    val text = response.text
    val json     = parse(text)
    (json \\ "values" \ classOf[JInt]).headOption.orElse(Option(BigInt(0)))
  }

  def addStatusAnnotation(status: Status): Option[Response] = {
    for {
      q               <- query(status)
      lastStatusValue <- getLastStatusValue(q)
      statusValue     <- getStatusValue(status, lastStatusValue)
    } yield writeAnnotationToInfluxdb(status, statusValue)
  }

}
