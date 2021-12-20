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
  def fromCsv(paramName: String, key: String, source: Map[String, String])                                             =
    apply(paramName, key, source, ",")

  def fromSsv(paramName: String, key: String, source: Map[String, String]) =
    apply(paramName, key, source, ";")

  def fromTsv(paramName: String, key: String, source: Map[String, String]) =
    apply(paramName, key, source, "\t")

  /** Creates a feeder with separated values from the source string
    * @param paramName
    *   feeder name
    * @param source
    *   source string
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
  def apply(paramName: String, source: String, separator: String): FeederBuilderBase[String] = {
    source
      .split(separator)
      .toList
      .toFeeder(paramName)
  }
  def fromCsv(paramName: String, source: String)                                             =
    apply(paramName, source, ",")

  def fromSsv(paramName: String, source: String) =
    apply(paramName, source, ";")

  def fromTsv(paramName: String, source: String) =
    apply(paramName, source, "\t")

  /** Creates a feeder with separated values from the source List
    * @param paramName
    *   feeder name
    * @param source
    *   source list
    * @param separator
    *   ",", ";", "\t" or other delimiter which separates values <br>you also can use following methods for the most common
    *   separators: .fromCsv(...), .fromSsv(...), .fromTsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val sourceList  = List("v11;v12", "v21;v22;v23", "")
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder("someValues", sourceString, ";").random
    * }}}
    */
  def apply(paramName: String, source: List[String], separator: String): FeederBuilderBase[String] = {
    source
      .mkString(separator)
      .split(separator)
      .toList
      .toFeeder(paramName)
  }
  def fromCsv(paramName: String, source: List[String])                                             =
    apply(paramName, source, ",")

  def fromSsv(paramName: String, source: List[String]) =
    apply(paramName, source, ";")

  def fromTsv(paramName: String, source: List[String]) =
    apply(paramName, source, "\t")
}
