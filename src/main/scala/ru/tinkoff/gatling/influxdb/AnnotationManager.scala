package ru.tinkoff.gatling.influxdb

import ru.tinkoff.gatling.config.ConfigManager._

private [gatling] object AnnotationManager {

  private val influxHost       = gatlingConfig.getString("gatling.data.graphite.host")
  private val rootPathPrefix   = gatlingConfig.getString("gatling.data.graphite.rootPathPrefix")
  private val graphitePort     = gatlingConfig.getString("gatling.data.graphite.port")
  private val influxHostScheme = influxConfig.getString("influx.scheme")
  private val influxPort       = influxConfig.getString("influx.port")
  private val db               = influxConfig.getString("influx.db." + graphitePort)

  private val influxUrl = s"""$influxHostScheme://$influxHost:$influxPort"""

  private val influx = InfluxUtils(influxUrl, db, rootPathPrefix)

  def start(): Unit = influx.addStatusAnnotation(Start)

  def stop(): Unit = influx.addStatusAnnotation(Stop)

}
