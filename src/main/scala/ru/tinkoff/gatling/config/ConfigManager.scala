package ru.tinkoff.gatling.config

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef.configuration
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

private[gatling] object ConfigManager {

  lazy val simulationConfig = SimulationConfigUtils(
    ConfigFactory
      .load("simulation.conf"))

  lazy val influxConfig: Config = ConfigFactory.load("influx.conf")
    .withFallback(ConfigFactory.load("influx-default.conf"))

  lazy val gatlingConfig: Config = configuration.config

  def getProfileConfig(profileName: String): ProfileConfig = {
    ConfigSource.resources(profileName).loadOrThrow[ProfileConfig]
  }

}
