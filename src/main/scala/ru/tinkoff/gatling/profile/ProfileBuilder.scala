package ru.tinkoff.gatling.profile
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.module.yaml.YamlConfigSource

import scala.reflect.ClassTag

trait RequestConfig {
  val name: String
  val url: String
  val probability: Double

  def toExec: ChainBuilder

}

trait ProfileConfig {
  val name: String
  protected val profile: Seq[RequestConfig]

  def toRandomScenario(): Seq[(Double, ChainBuilder)] = profile.map(requestConfig => (requestConfig.probability, requestConfig.toExec))

}

class ProfileBuilder[A <: ProfileConfig] {

  def buildFromYaml(path: String)(implicit evidence: ClassTag[A], reader: ConfigReader[A]): A =
    YamlConfigSource.file(path).loadOrThrow[A]

  def buildFromConf(path: String)(implicit evidence: ClassTag[A], reader: ConfigReader[A]): A =
    ConfigSource.file(path).loadOrThrow[A]

}







