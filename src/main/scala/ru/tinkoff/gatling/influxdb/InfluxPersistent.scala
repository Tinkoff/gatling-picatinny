package ru.tinkoff.gatling.influxdb

import com.typesafe.scalalogging.StrictLogging
import io.razem.influxdbclient.{Database, InfluxDB, Point}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Try, Using}

private[gatling] case class InfluxPersistent(host: String,
                                             port: Int,
                                             db: String,
                                             schema: String,
                                             username: String = null,
                                             password: String = null)
    extends StrictLogging {

  private def checkSchema(schema: String): Boolean = schema match {
    case "https" => true
    case _       => false
  }

  private def exec(f: Database => Future[Boolean]): Try[Future[Boolean]] = {
    Using(InfluxDB.connect(host, port, username, password, checkSchema(schema))) { influxDb =>
      f(influxDb.selectDatabase(db))
    }
  }

  def writePoint(point: Point): Try[Future[Boolean]] = exec(_.write(point))

  def writeStatusAnnotation(status: Status, value: String): Try[Future[Boolean]] = {
    val point = Point(rootPathPrefix)
      .addTag("annotation", status.toString)
      .addField("annotation_value", value)
    writePoint(point)
  }

}
