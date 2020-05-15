package ru.tinkoff.gatling.profile

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef.configuration

object ProfileConfigManager {

  def profileConfigLoad(profilePath: String = "default-profile.conf"): Config = configuration.config
    .withFallback(ConfigFactory.load(profilePath).getConfig("profile"))

}
