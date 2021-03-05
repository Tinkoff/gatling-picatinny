package ru.tinkoff.gatling.influxdb

import requests.{RequestBlob, Response, ResponseBlob}

import scala.collection.mutable
import scala.util.Try

object InfluxMock {

  val influxUrl                = "http://localhost:8086"
  val db                       = "testdb"
  val rootPathPrefix           = "TEST"
  val queryPath                = "query"
  val writePath                = "write"
  val ok                       = 200
  val noContent                = 204
  val okMessage                = "OK"
  val noContentMessage         = "No Content"
  val startStatus              = Start
  val lastStartAnnotationValue = 14

  val getAnnotationResponseBody =
    s"""{
       |   "results":[
       |      {
       |         "statement_id":0,
       |         "series":[
       |            {
       |               "name":"$rootPathPrefix",
       |               "columns":[
       |                  "time",
       |                  "last"
       |               ],
       |               "values":[
       |                  [
       |                     "2020-04-27T10:18:26.171006366Z",
       |                     $lastStartAnnotationValue
       |                  ]
       |               ]
       |            }
       |         ]
       |      }
       |   ]
       |}""".stripMargin

  val writeAnnotationResponseBogy = ""

  val getLastAnnotationQuery = s"SELECT last(annotation_value) FROM $rootPathPrefix WHERE annotation='$startStatus'"

  def apply(): InfluxMock = new InfluxMock(influxUrl, db, rootPathPrefix)

}

class InfluxMock(influxUrl: String, db: String, rootPathPrefix: String) extends InfluxUtils(influxUrl, db, rootPathPrefix) {

  import ru.tinkoff.gatling.influxdb.InfluxMock._

  override def get(url: String,
                   params: Iterable[(String, String)],
                   headers: Iterable[(String, String)],
                   data: RequestBlob): Try[Response] = {
    val headers = Map(
      "x-influxdb-version" -> Seq("1.7.10"),
      "x-request-id"       -> Seq("fbe4c74d-8866-11ea-aece-00505696af00"),
      "request-id"         -> Seq("fbe4c74d-8866-11ea-aece-00505696af00"),
      "date"               -> Seq("Mon, 27 Apr 2020 09:10:48 GMT"),
      "content-type"       -> Seq("application/json"),
      "x-influxdb-build"   -> Seq("OSS"),
      "transfer-encoding"  -> Seq("chunked"),
      "content-encoding"   -> Seq("gzip")
    )
    val body = new ResponseBlob(getAnnotationResponseBody.getBytes())
    Try(Response(s"$influxUrl/$queryPath", ok, okMessage, headers, body, None))
  }

  override def post(url: String,
                    params: Iterable[(String, String)],
                    headers: Iterable[(String, String)],
                    data: RequestBlob): Try[Response] = {
    val headers = Map(
      "x-influxdb-version" -> Seq("1.7.10"),
      "x-request-id"       -> Seq("fbe4c74d-8866-11ea-aece-00505696af00"),
      "request-id"         -> Seq("fbe4c74d-8866-11ea-aece-00505696af00"),
      "date"               -> Seq("Mon, 27 Apr 2020 09:10:48 GMT"),
      "content-type"       -> Seq("application/json"),
      "x-influxdb-build"   -> Seq("OSS"),
    )
    val body = new ResponseBlob(writeAnnotationResponseBogy.getBytes())
    Try(Response(s"$influxUrl/$writePath", noContent, noContentMessage, headers, body, None))
  }
}
