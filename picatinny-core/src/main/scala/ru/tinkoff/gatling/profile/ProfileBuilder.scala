package ru.tinkoff.gatling.profile
import pureconfig.module.yaml.YamlConfigSource
import pureconfig.{ConfigReader, ConfigSource}

import scala.reflect.ClassTag

class ProfileBuilder[A <: ProfileConfig] {

  def buildFromYaml(path: String)(implicit evidence: ClassTag[A], reader: ConfigReader[A]): A =
    YamlConfigSource.file(path).loadOrThrow[A]

  def buildFromConf(path: String)(implicit evidence: ClassTag[A], reader: ConfigReader[A]): A =
    ConfigSource.file(path).loadOrThrow[A]

}
