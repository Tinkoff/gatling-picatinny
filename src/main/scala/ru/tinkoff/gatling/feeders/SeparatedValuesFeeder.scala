package ru.tinkoff.gatling.feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase

object SeparatedValuesFeeder {

  /** Creates a feeder with separated values from the source Map(k -> v) for the specified key
    * @param paramName
    *   feeder name
    * @param key
    *   the key to get one single element from the source Map
    * @param source
    *   data source
    * @param separator
    *   ",", ";", "\t" or other delimiter which separates values <br>you also can use following methods for the most common
    *   separators: .fromCsv(...), .fromSsv(...), .fromTsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val someFeeder = Iterator( Map( "k1" -> "v11,v12", "k2" -> "v21;v22;v23", "k3" -> "token" ) )
    *   val sourceMap  = someFeeder.next()
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder("someValues", "k2", sourceMap, ";").random
    * }}}
    */
  def apply(paramName: String, key: String, source: Map[String, String], separator: String): FeederBuilderBase[String] = {
    source.view
      .filterKeys(key.contains)
      .values
      .mkString
      .split(separator)
      .toList
      .toFeeder(paramName)
  }

  def fromCsv(paramName: String, key: String, source: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, source, ",")
  def fromSsv(paramName: String, key: String, source: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, source, ";")
  def fromTsv(paramName: String, key: String, source: Map[String, String]): FeederBuilderBase[String] =
    apply(paramName, key, source, "\t")

  /** Creates a feeder with separated values from the source string
    * @param paramName
    *   feeder name
    * @param source
    *   source String or List
    * @param separator
    *   ",", ";", "\t" or other delimiter which separates values <br>you also can use following methods for the most common
    *   separators: .fromCsv(...), .fromSsv(...), .fromTsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val sourceString  = "v21;v22;v23"
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder("someValues", sourceString, ";").random
    * }}}
    */
  def apply[T](paramName: String, source: T, separator: String): FeederBuilderBase[String] = source match {
    case _: String         =>
      s"$source"
        .split(separator)
        .toList
        .toFeeder(paramName)
    case source @ List(_*) =>
      source
        .mkString(separator)
        .split(separator)
        .toList
        .toFeeder(paramName)
    case _                 => throw new Exception(s"Source has to be String, List(Any) or Map(String -> String)")
  }

  def fromCsv[T](paramName: String, source: T): FeederBuilderBase[String] = apply(paramName, source, ",")
  def fromSsv[T](paramName: String, source: T): FeederBuilderBase[String] = apply(paramName, source, ";")
  def fromTsv[T](paramName: String, source: T): FeederBuilderBase[String] = apply(paramName, source, "\t")

}
