package ru.tinkoff.gatling.config

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration
import scala.reflect.runtime.universe._
import scala.language.implicitConversions
import scala.concurrent.duration._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

private[gatling] class SimulationConfigUtils(config: Config) extends LazyLogging {
  private val StringTag         = typeTag[String]
  private val FiniteDurationTag = typeTag[FiniteDuration]
  private val StringListTag     = typeTag[List[String]]
  private val BigDecimalTag     = typeTag[BigDecimal]
  private val ConfigTag         = typeTag[Config]

  def getOpt[T](path: String)(implicit tag: TypeTag[T]): Option[T] =
    if (config.hasPath(path)) Some(getValueByType(config, path))
    else None

  def get[T](path: String)(implicit tag: TypeTag[T]): T =
    getOpt(path).getOrElse {
      logger.error(s"""Simulation param for ${path} is undefined""")
      throw new RuntimeException(s"Configuration value at $path not found")
    }

  def get[T](path: String, default: => T)(implicit tag: TypeTag[T]): T =
    getOpt(path).getOrElse(default)

  private def getValueByType[T](cfg: Config, path: String)(implicit tag: TypeTag[T]): T = {
    val result = tag match {
      case StringTag         => cfg.getString(path)
      case TypeTag.Long      => cfg.getLong(path)
      case TypeTag.Int       => cfg.getInt(path)
      case TypeTag.Double    => cfg.getDouble(path)
      case FiniteDurationTag => cfg.getDuration(path, TimeUnit.SECONDS).seconds
      case StringListTag     => cfg.getStringList(path)
      case TypeTag.Boolean   => cfg.getBoolean(path)
      case BigDecimalTag     => BigDecimal(cfg.getString(path))
      case ConfigTag         => cfg.getConfig(path)
      case _ =>
        logger.error(s"Configuration option type $tag is not implemented")
        throw new IllegalArgumentException(s"Configuration option type $tag not implemented")
    }
    val res = result.asInstanceOf[T]
    logger.info(s"Simulation param for $path is set to: ${res}")
    res
  }
}

private[gatling] object SimulationConfigUtils {
  def apply(config: Config): SimulationConfigUtils = new SimulationConfigUtils(config)
}
