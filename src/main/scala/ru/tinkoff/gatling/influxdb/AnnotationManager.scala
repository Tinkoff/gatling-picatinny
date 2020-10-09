package ru.tinkoff.gatling.influxdb

import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.gatling.config.ConfigManager._

import scala.util.{Failure, Success}

private [gatling] object AnnotationManager extends StrictLogging {

  private val influxHost       = gatlingConfig.getString("gatling.data.graphite.host")
  private val rootPathPrefix   = gatlingConfig.getString("gatling.data.graphite.rootPathPrefix")
  private val graphitePort     = gatlingConfig.getString("gatling.data.graphite.port")
  private val influxHostScheme = influxConfig.getString("influx.scheme")
  private val influxPort       = influxConfig.getString("influx.port")
  private val db               = influxConfig.getString("influx.db." + graphitePort)

  private val influxUrl = s"""$influxHostScheme://$influxHost:$influxPort"""

  private val influx = InfluxUtils(influxUrl, db, rootPathPrefix)

  def start(): Unit = influx.addStatusAnnotation(Start) match {
    case Success(_) => ()
    case Failure(exception) => logger.error(s"Failed write Start annotation to influxdb: ${exception.getMessage}")
  }

  def stop(): Unit = influx.addStatusAnnotation(Stop) match {
    case Success(_) => ()
    case Failure(exception) => logger.error(s"Failed write Stop annotation to influxdb: ${exception.getMessage}")
  }

}
