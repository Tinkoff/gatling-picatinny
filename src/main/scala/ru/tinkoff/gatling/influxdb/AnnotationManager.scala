package ru.tinkoff.gatling.influxdb

import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

private[gatling] object AnnotationManager extends StrictLogging {

  //check Future result
  private def complete(status: Status, res: Future[Boolean]): Unit = {
    res onComplete {
      case Success(_) =>
        logger.info(s"$status annotation has been written to influxdb")
      case Failure(exception) =>
        logger.error(s"Failed to write $status annotation to influxdb: ${exception.getMessage}")
    }
  }

  def addAnnotation(status: Status, value: String): Unit =
    influx
      .writeStatusAnnotation(status, value)
      .map(complete(status, _))

}
