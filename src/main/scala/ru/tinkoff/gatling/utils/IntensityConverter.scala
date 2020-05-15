package ru.tinkoff.gatling.utils

import com.typesafe.scalalogging.LazyLogging
import scala.util.Try

object IntensityConverter {

  //implicit conversions to rps
  implicit class toRps(val count: Double) extends AnyVal {
    def rph: Double = count / 3600

    def rpm: Double = count / 60

    def rps: Double = count
  }

  def getIntensityFromString(intensity: String): Double = {
    val pattern = """(\d+\.?\d?)\s?(\w+)?""".r

    Try {
      val matcher        = pattern.findAllIn(intensity)
      val intensityValue = matcher.group(1).toDouble
      val intensityParam = Try(matcher.group(2).toLowerCase).getOrElse("rps")
      intensityParam match {
        case "rps" => intensityValue rps
        case "rpm" => intensityValue rpm
        case "rph" => intensityValue rph
      }
    }.getOrElse {
      throw new IllegalArgumentException("Simulation param for intensity incorrect")
    }
  }
}
