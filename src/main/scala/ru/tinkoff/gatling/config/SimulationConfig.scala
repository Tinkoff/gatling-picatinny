package ru.tinkoff.gatling.config

import scala.concurrent.duration._
import ru.tinkoff.gatling.config.ConfigManager.simulationConfig
import ru.tinkoff.gatling.utils.IntensityConverter.getIntensityFromString

object SimulationConfig {

  def getStringParam(path: String): String           = simulationConfig.get[String](path)
  def getIntParam(path: String): Int                 = simulationConfig.get[Int](path)
  def getDoubleParam(path: String): Double           = simulationConfig.get[Double](path)
  def getDurationParam(path: String): FiniteDuration = simulationConfig.get[FiniteDuration](path)
  def getBooleanParam(path: String): Boolean         = simulationConfig.get[Boolean](path)

  lazy val baseUrl: String     = simulationConfig.get[String]("baseUrl")
  lazy val baseAuthUrl: String = simulationConfig.get[String]("baseAuthUrl")
  lazy val wsBaseUrl: String   = simulationConfig.get[String]("wsBaseUrl")

  lazy val stagesNumber: Int = simulationConfig.get[Int]("stagesNumber", 1)

  lazy val rampDuration: FiniteDuration  = simulationConfig.get[FiniteDuration]("rampDuration")
  lazy val stageDuration: FiniteDuration = simulationConfig.get[FiniteDuration]("stageDuration")
  lazy val testDuration: FiniteDuration =
    simulationConfig.get[FiniteDuration]("testDuration", (rampDuration + stageDuration) * stagesNumber)

  lazy val intensity: Double = getIntensityFromString(simulationConfig.get[String]("intensity"))

}
