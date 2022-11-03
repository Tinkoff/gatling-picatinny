package ru.tinkoff.gatling.assertions

import io.gatling.commons.shared.unstable.model.stats.assertion.AssertionPathParts
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.Predef._
import pureconfig.module.yaml.YamlConfigSource
import pureconfig.generic.auto._
import io.gatling.core.Predef.configuration

object AssertionsBuilder {

  private case class NFR(nfr: List[Record])

  private case class Record(key: String, value: Map[String, String])

  private def getNfr(path: String): NFR =
    YamlConfigSource.file(path).asObjectSource.loadOrThrow[NFR]

  private def toUtf(baseString: String): String =
    scala.io.Source.fromBytes(baseString.getBytes(), "UTF-8").mkString

  private def findGroup(key: String): AssertionPathParts = {
    AssertionPathParts.apply(key.split(" / ").toList)
  }

  private def buildAssertion(record: Record): Iterable[Assertion] = toUtf(record.key) match {
    case "Процент ошибок"                   => buildErrorAssertion(record)
    case "99 перцентиль времени выполнения" => buildPercentileAssertion(record, 99)
    case "95 перцентиль времени выполнения" => buildPercentileAssertion(record, 95)
    case "75 перцентиль времени выполнения" => buildPercentileAssertion(record, 75)
    case "50 перцентиль времени выполнения" => buildPercentileAssertion(record, 50)
    case "Максимальное время выполнения"    => buildMaxResponseTimeAssertion(record)
    case _                                  => None
  }

  private def buildErrorAssertion(record: Record): Iterable[Assertion] = record.value.map {
    case ("all", v) => global.failedRequests.percent.lt(v.toInt)
    case (k, v)     => details(findGroup(k)).failedRequests.percent.lt(v.toInt)
  }

  private def buildPercentileAssertion(record: Record, percentile: Int): Iterable[Assertion] =
    record.value.map {
      case ("all", v) => global.responseTime.percentile(percentile).lt(v.toInt)
      case (k, v)     => details(findGroup(k)).responseTime.percentile(percentile).lt(v.toInt)
    }

  private def buildMaxResponseTimeAssertion(record: Record): Iterable[Assertion] = record.value.map {
    case ("all", v) => global.responseTime.max.lt(v.toInt)
    case (k, v)     => details(findGroup(k)).responseTime.max.lt(v.toInt)
  }

  def assertionFromYaml(path: String): Iterable[Assertion] = {
    getNfr(path).nfr.flatMap(buildAssertion)
  }

}
