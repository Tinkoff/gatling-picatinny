package ru.tinkoff.gatling

import ru.tinkoff.gatling.config.ConfigManager.{gatlingConfig, influxConfig}
import scala.util.Try

package object influxdb {

  // graphite config from gatling.conf
  private[influxdb] lazy val influxHost: String     = gatlingConfig.data.graphite.host
  private[influxdb] lazy val rootPathPrefix: String = gatlingConfig.data.graphite.rootPathPrefix
  private lazy val graphitePort: Int                = gatlingConfig.data.graphite.port

  // influxDb config from influx-default.conf merged with custom influx.conf from project
  private[influxdb] lazy val influxApiPort: Int   = influxConfig.getInt("influx.port")
  private[influxdb] lazy val db: String           = influxConfig.getString("influx.db." + graphitePort)
  private[influxdb] lazy val influxScheme: String = influxConfig.getString("influx.scheme")

  private[influxdb] lazy val username: String = Try(influxConfig.getString("influx.username")).getOrElse(null)
  private[influxdb] lazy val password: String = Try(influxConfig.getString("influx.password")).getOrElse(null)

  lazy val influx: InfluxPersistent = InfluxPersistent(
    influxHost,
    influxApiPort,
    db,
    rootPathPrefix,
    influxScheme,
    username,
    password,
  )

}
