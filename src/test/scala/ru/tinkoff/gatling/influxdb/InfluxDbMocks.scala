package ru.tinkoff.gatling.influxdb

import io.razem.influxdbclient.{Database, InfluxDB, Point, QueryResult, Record, Series, TagSet}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[influxdb] object InfluxDbMocks extends MockFactory {

  val tagSetMock: TagSet = mock[TagSet]

  class QueryResultMock(series: Series) extends QueryResult(List(series))

  class SeriesMock(record: Record) extends Series("", List(), List(record), tagSetMock)

  class RecordMock(values: List[Any]) extends Record(Map("" -> 0), values)

  def lastStatusValueMock(value: List[Any]) = new QueryResultMock(new SeriesMock(new RecordMock(value)))

  val testInfluxDbPersistent: InfluxPersistent = InfluxPersistent(
    "localhost",
    8086,
    "test",
    "experiment",
    "http",
    "admin",
    "admin"
  )

  val testQuery       = s"""SELECT last("annotation_value") FROM ${testInfluxDbPersistent.rootPathPrefix}"""
  val testQueryResult = lastStatusValueMock(List(BigDecimal(0)))
  val testPoint       = Point("test", -1, Nil, Nil)
  val testPoint2      = Point("test2", -1, Nil, Nil)
  val testAnnotationStatusPoint = Point(testInfluxDbPersistent.rootPathPrefix)
    .addTag("annotation", Start.toString)
    .addField("annotation_value", BigDecimal(0))

  val influxDbMock = stub[InfluxDB]
  val databaseMock = stub[Database]
  (influxDbMock.selectDatabase _)
    .when("test")
    .returns(databaseMock)
  (databaseMock.query _)
    .when(testQuery, null)
    .returns(Future(testQueryResult))
  (databaseMock.write _)
    .when(testPoint, null, null, null)
    .returns(Future(true))
  (databaseMock.bulkWrite _)
    .when(Seq(testPoint, testPoint2), null, null, null)
    .returns(Future(true))
  (databaseMock.write _)
    .when(testAnnotationStatusPoint, null, null, null)
    .returns(Future(true))

}
