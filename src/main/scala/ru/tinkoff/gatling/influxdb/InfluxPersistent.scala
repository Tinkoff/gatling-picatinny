package ru.tinkoff.gatling.influxdb

import io.razem.influxdbclient.{Database, HttpConfig, InfluxDB, Point, QueryResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[influxdb] case class InfluxPersistent(host: String,
                                              port: Int,
                                              db: String,
                                              rootPathPrefix: String,
                                              schema: String,
                                              username: String,
                                              password: String) {

  def checkSchema(schema: String): Boolean = schema match {
    case "https" => true
    case _       => false
  }

  private lazy val httpConfig = new HttpConfig().setConnectTimeout(3000).setRequestTimeout(30000) //timeout in millis

  private[influxdb] def init: Future[InfluxDB] =
    Future(InfluxDB.connect(host, port, username, password, checkSchema(schema), httpConfig))
  private[influxdb] def close(influxDb: InfluxDB): Future[Unit] = Future(influxDb.close())

  private def exec[T](influxDb: InfluxDB, f: Database => Future[T]) = f(influxDb.selectDatabase(db))
  def read(influxDb: InfluxDB, q: String): Future[QueryResult]      = exec(influxDb, _.query(q))
  def write(influxDb: InfluxDB, p: Point): Future[Boolean]          = exec(influxDb, _.write(p))
  def bulkWrite(influxDb: InfluxDB, p: Seq[Point]): Future[Boolean] = exec(influxDb, _.bulkWrite(p))

  def readLastStatusAnnotation(influxDb: InfluxDB): Future[QueryResult] =
    read(influxDb, s"""SELECT last("annotation_value") FROM $rootPathPrefix""")

  def writeStatusAnnotation(influxDb: InfluxDB, status: Status, value: BigDecimal, timestamp: Long): Future[Boolean] = {
    val point = Point(rootPathPrefix, timestamp)
      .addTag("annotation", status.toString)
      .addField("annotation_value", value)
    write(influxDb, point)
  }

  def writeCustomAnnotation(influxDb: InfluxDB,
                            tagKey: String,
                            tagValue: String,
                            fieldKey: String,
                            fieldValue: String,
                            timestamp: Long): Future[Boolean] = {
    val point = Point(rootPathPrefix, timestamp)
      .addTag(tagKey, tagValue)
      .addField(fieldKey, fieldValue)
    write(influxDb, point)
  }

  def writeCustomPoint(influxDb: InfluxDB, point: Point): Future[Boolean] = {
    write(influxDb, point)
  }

  def writeCustomPoints(influxDb: InfluxDB, points: Seq[Point]): Future[Boolean] = {
    bulkWrite(influxDb, points)
  }

}
