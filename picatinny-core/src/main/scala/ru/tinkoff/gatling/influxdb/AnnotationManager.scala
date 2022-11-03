package ru.tinkoff.gatling.influxdb

import com.typesafe.scalalogging.StrictLogging
import io.razem.influxdbclient.{InfluxDB, Point, QueryResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

private[gatling] object AnnotationManager extends StrictLogging {

  private def completeAddStatusAnnotation[T](connection: InfluxDB, status: Status, res: Future[T]): Unit = {
    res onComplete {
      case Success(_)         =>
        logger.info(s"$status annotation has been written to influxdb")
        influx.close(connection)
      case Failure(exception) =>
        logger.error(s"Failed to write $status annotation to influxdb: ${exception.getMessage}")
        influx.close(connection)
    }
  }

  private def completeWrite[T](connection: InfluxDB, res: Future[T]): Unit = {
    res onComplete {
      case Success(_)         =>
        logger.info(s"Custom point has been written to influxdb")
        influx.close(connection)
      case Failure(exception) =>
        logger.error(s"Failed to write custom point to influxdb: ${exception.getMessage}")
        influx.close(connection)
    }
  }

  def incrementStatusAnnotationValue(status: Status, res: QueryResult): Future[BigDecimal] = {
    def incrementAndGet(i: Int): Future[BigDecimal] =
      Future(BigDecimal(res.series.last.records.last.allValues.last.toString) + i).recover { case _: Throwable =>
        BigDecimal(0)
      }
    status match {
      case Start => incrementAndGet(1)
      case Stop  => incrementAndGet(0)
    }
  }

  def addStatusAnnotation(status: Status): Unit = {
    for {
      connection <- influx.init
      lastValue  <- influx.readLastStatusAnnotation(connection)
      value      <- incrementStatusAnnotationValue(status, lastValue)
      res         = influx.writeStatusAnnotation(connection, status, value, System.currentTimeMillis() * 1000000)
    } yield completeAddStatusAnnotation(connection, status, res)
  }

  def addCustomAnnotation(tagKey: String, tagValue: String, fieldKey: String, fieldValue: String): Unit = {
    for {
      connection <- influx.init
      res         =
        influx.writeCustomAnnotation(connection, tagKey, tagValue, fieldKey, fieldValue, System.currentTimeMillis() * 1000000)
    } yield completeWrite(connection, res)
  }

  def addCustomPoint(point: Point): Unit = {
    for {
      connection <- influx.init
      res         = influx.writeCustomPoint(connection, point)
    } yield completeWrite(connection, res)
  }

  def addCustomPoints(points: Seq[Point]): Unit = {
    for {
      connection <- influx.init
      res         = influx.writeCustomPoints(connection, points)
    } yield completeWrite(connection, res)
  }

}
