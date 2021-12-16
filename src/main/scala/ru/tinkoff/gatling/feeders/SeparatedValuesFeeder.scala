package ru.tinkoff.gatling.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase

object SeparatedValuesFeeder {

  def apply(paramName: String, kv: Map[String, String], separator: Char): FeederBuilderBase[String] = {
    kv.values.mkString
      .split(separator)
      .toList
      .toFeeder(paramName)
  }

  def fromCsv(paramName: String, kv: Map[String, String]): FeederBuilderBase[String] = apply(paramName, kv, ',')
  def fromSsv(paramName: String, kv: Map[String, String]): FeederBuilderBase[String] = apply(paramName, kv, ';')
  def fromTsv(paramName: String, kv: Map[String, String]): FeederBuilderBase[String] = apply(paramName, kv, '\t')

}
