package ru.tinkoff.gatling.feeders

import io.gatling.core.Predef._
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.feeder.{FeederBuilderBase, _}

object SeparatedValuesFeeder {

  private val CommaSeparator: Char      = ','
  private val SemicolonSeparator: Char  = ';'
  private val TabulationSeparator: Char = '\t'

  private def splitter(source: String, separator: Char): IndexedSeq[String] = source.split(separator).toIndexedSeq

  /** Creates a feeder with separated values from the source String
    * @param paramName
    *   feeder name
    * @param source
    *   data source
    * @param separator
    *   ',', ';', '\t' or other delimiter which separates values.
    *
    * You also can use following methods for the most common separators: .csv(...), .ssv(...), .tsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val sourceString = "v21;v22;v23"
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder("someValues", sourceString, ';') // this will return Vector(Map(someValues -> v21), Map(someValues -> v22), Map(someValues -> v23))
    * }}}
    */
  def apply(paramName: String, source: String, separator: Char): IndexedSeq[Record[String]] = {

    val records = splitter(source, separator).map(s => Map(paramName -> s.trim))
    require(records.nonEmpty, "Feeder source is empty")

    records
  }

  /** Creates a feeder with separated values from the source Sequence
    * @param paramName
    *   feeder name
    * @param source
    *   data source
    * @param separator
    *   ',', ';', '\t' or other delimiter which separates values.
    *
    * You also can use following methods for the most common separators: .csv(...), .ssv(...), .tsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val sourceSeq = Seq("1,two", "3,4")
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder.csv("someValues", sourceSeq) // this will return Vector(Map(someValues -> 1), Map(someValues -> two), Map(someValues -> 3), Map(someValues -> 4))
    * }}}
    */
  def apply(paramName: String, source: Seq[String], separator: Char)(implicit
      configuration: GatlingConfiguration,
  ): IndexedSeq[Record[String]] = {

    val records = source.flatMap(s => splitter(s, separator)).map(s => Map(paramName -> s.trim)).toIndexedSeq
    require(records.nonEmpty, "Feeder source is empty")

    records
  }

  /** Creates a feeder with separated values from the source Seq[Map[String, String] ]
    * @param paramPrefix
    *   feeder name
    * @param source
    *   data source
    * @param separator
    *   ',', ';', '\t' or other delimiter which separates values.
    *
    * You also can use following methods for the most common separators: .csv(...), .ssv(...), .tsv(...)
    * @return
    *   a new feeder
    * @example
    * {{{
    *   val vaultFeeder: FeederBuilderBase[String] = Vector(
    *     Map(
    *       "HOSTS" -> "host11,host12",
    *       "USERS" -> "user11",
    *     ),
    *     Map(
    *       "HOSTS" -> "host21,host22",
    *       "USERS" -> "user21,user22,user23",
    *     ),
    *   )
    *   val mapFee: FeederBuilderBase[String] = SeparatedValuesFeeder(None, vaultFeeder.readRecords, ',')
    *   val separatedValuesFeeder: FeederBuilderBase[String] =
    *     SeparatedValuesFeeder("prefix", sourceSeq, ',') // this will return Vector(Map(HOSTS -> host11), Map(HOSTS -> host12), Map(USERS -> user11), Map(HOSTS -> host21), Map(HOSTS -> host22), Map(USERS -> user21), Map(USERS -> user22), Map(USERS -> user23))
    * }}}
    */
  def apply(paramPrefix: Option[String], source: Seq[Map[String, Any]], separator: Char)(implicit
      configuration: GatlingConfiguration,
  ): IndexedSeq[Record[String]] = {

    val records = source
      .flatMap(m =>
        m.map { case (k, v) =>
          splitter(v.toString, separator).map {
            paramPrefix match {
              case None         => s => Map(k -> s)
              case Some(prefix) => s => Map(s"${prefix}_$k" -> s)
            }
          }
        },
      )
      .flatten
      .toIndexedSeq
    require(records.nonEmpty, "Feeder source is empty")

    records
  }

  def csv(paramName: String, source: String): FeederBuilderBase[String] = apply(paramName, source, CommaSeparator)
  def ssv(paramName: String, source: String): FeederBuilderBase[String] = apply(paramName, source, SemicolonSeparator)
  def tsv(paramName: String, source: String): FeederBuilderBase[String] = apply(paramName, source, TabulationSeparator)

  def csv(paramName: String, source: Seq[String]): FeederBuilderBase[String] = apply(paramName, source, CommaSeparator)
  def ssv(paramName: String, source: Seq[String]): FeederBuilderBase[String] = apply(paramName, source, SemicolonSeparator)
  def tsv(paramName: String, source: Seq[String]): FeederBuilderBase[String] = apply(paramName, source, TabulationSeparator)

  def csv(paramPrefix: Option[String] = None, source: Seq[Map[String, Any]]): FeederBuilderBase[String] =
    apply(paramPrefix, source, CommaSeparator)
  def ssv(paramPrefix: Option[String] = None, source: Seq[Map[String, Any]]): FeederBuilderBase[String] =
    apply(paramPrefix, source, SemicolonSeparator)
  def tsv(paramPrefix: Option[String] = None, source: Seq[Map[String, Any]]): FeederBuilderBase[String] =
    apply(paramPrefix, source, TabulationSeparator)
}
