package ru.tinkoff.gatling.influxdb

import io.razem.influxdbclient._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[gatling] object InfluxUtils {
  def apply(influxHost: String, influxPort: String, db: String, rootPathPrefix: String): InfluxUtils =
    new InfluxUtils(influxHost, influxPort, db, rootPathPrefix)
}

private[gatling] class InfluxUtils(influxHost: String, influxPort: String, db: String, rootPathPrefix: String) {

  def addStatusAnnotation(influxdb: InfluxDB, status: Status, statusValue: String): Future[Boolean] = {


    val database = influxdb.selectDatabase(db)

    val point = Point(rootPathPrefix)
      .addTag("annotation", status.toString)
      .addField("annotation_value", statusValue)
    database.write(point)
  }

  def closeConnection(influxdb: InfluxDB): Unit = {
    influxdb.close()
  }

  def initConnection(): InfluxDB = {
    InfluxDB.connect(influxHost, influxPort.toInt)
  }
}
