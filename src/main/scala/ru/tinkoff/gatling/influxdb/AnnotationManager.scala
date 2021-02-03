package ru.tinkoff.gatling.influxdb

import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.gatling.config.ConfigManager._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

private[gatling] object AnnotationManager extends StrictLogging {

  private val influxHost = gatlingConfig.data.graphite.host
  private val rootPathPrefix = gatlingConfig.data.graphite.rootPathPrefix
  private val influxPort = influxConfig.getString("influx.port")
  private val db = influxConfig.getString("influx.db")

  private val influx = new InfluxUtils(influxHost, influxPort, db, rootPathPrefix)

  def start(): Unit = {
    val instance = influx.initConnection()
    influx.addStatusAnnotation(instance, Start, "StartAnnotation") onComplete {
      case Success(_) => influx.closeConnection(instance)
      case Failure(exception) => {
        logger.error(s"Failed write Start annotation to influxdb: ${exception.getMessage}")
        influx.closeConnection(instance)
      }
    }
  }

  def stop(): Unit = {
    val instance = influx.initConnection()
    influx.addStatusAnnotation(instance, Stop, "stopAnnotation") onComplete {
      case Success(_) => influx.closeConnection(instance)
      case Failure(exception) => {
        logger.error(s"Failed write Stop annotation to influxdb: ${exception.getMessage}")
        influx.closeConnection(instance)
      }
    }
  }

}
