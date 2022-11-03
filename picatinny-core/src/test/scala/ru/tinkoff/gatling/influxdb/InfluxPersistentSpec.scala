package ru.tinkoff.gatling.influxdb

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class InfluxPersistentSpec extends AnyFlatSpec with Matchers {
  import InfluxDbMocks._

  it should "return true for https scheme" in {
    assert {
      testInfluxDbPersistent.checkSchema("https")
    }
  }

  it should "return false for http scheme" in {
    assert {
      !testInfluxDbPersistent.checkSchema("http")
    }
  }

  it should "return QueryResult when read from InfluxDB" in {
    assert {
      val future = testInfluxDbPersistent.read(influxDbMock, testQuery)
      val result = Await.result(future, 100 milliseconds)
      result == testQueryResult
    }
  }

  it should "write Point to InfluxDB" in {
    assert {
      val future = testInfluxDbPersistent.write(influxDbMock, testPoint)
      Await.result(future, 100 milliseconds)
    }
  }
  it should "bulk write Points to InfluxDB" in {
    assert {
      val future = testInfluxDbPersistent.bulkWrite(influxDbMock, Seq(testPoint, testPoint2))
      Await.result(future, 100 milliseconds)
    }
  }

  it should "read last status annotation from InfluxDB" in {
    assert {
      val future = testInfluxDbPersistent.readLastStatusAnnotation(influxDbMock)
      val result = Await.result(future, 100 milliseconds)
      result == testQueryResult
    }
  }

  it should "write status annotation to InfluxDB" in {
    assert {
      val future = testInfluxDbPersistent
        .writeStatusAnnotation(influxDbMock, Start, BigDecimal(0), -1)
      Await.result(future, 100 milliseconds)
    }
  }
}
