package ru.tinkoff.gatling.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase

object SeparatedValuesFeeder {
  def apply(paramName: String, key: String, kv: Map[String, String], separator: Char): FeederBuilderBase[String] = {
    kv.view
      .filterKeys(key.contains)
      .values
      .mkString
      .split(separator)
      .toList
      .toFeeder(paramName)
  }

  def fromCsv(paramName: String, key: String, kv: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, kv, ',')
  def fromSsv(paramName: String, key: String, kv: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, kv, ';')
  def fromTsv(paramName: String, key: String, kv: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, kv, '\t')
}
