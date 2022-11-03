package ru.tinkoff.gatling.utils.jwt

import pdi.jwt.JwtAlgorithm
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.StrictLogging
import org.json4s._
import org.json4s.JValue
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import scala.io.Source

final case class JwtGeneratorBuilder(header: Header, payload: Payload, algorithm: String, secret: String)
    extends StrictLogging {

  private def validateJson(json: String): Either[String, String] = {
    try {
      Right(compact(render(parse(json))).toString)
    } catch {
      case e: Exception =>
        Left(s"Invalid json: $json")
    }
  }

  def headerFromResource(path: String): JwtGeneratorBuilder = {
    val resource = Source.fromResource(path).mkString
    validateJson(resource) match {
      case Right(json) => this.modify(_.header.json).setTo(json)
      case Left(msg)   =>
        logger.error(msg)
        this
    }
  }

  def header(header: String): JwtGeneratorBuilder = {
    validateJson(header) match {
      case Right(json) => this.modify(_.header.json).setTo(json)
      case Left(msg)   =>
        logger.error(msg)
        this
    }
  }

  def defaultHeader: JwtGeneratorBuilder = {
    def getBody(algorithm: String): JValue =
      ("alg"   -> algorithm) ~
        ("typ" -> "JWT")
    def header(algorithm: String): String = compact(render(getBody(algorithm))).toString

    this.modify(_.header.json).setTo(header(algorithm))
  }

  def payloadFromResource(path: String): JwtGeneratorBuilder = {
    val resource = Source.fromResource(path).mkString
    validateJson(resource) match {
      case Right(json) => this.modify(_.payload.json).setTo(json)
      case Left(msg)   =>
        logger.error(msg)
        this
    }
  }

  def payload(payload: String): JwtGeneratorBuilder = {
    validateJson(payload) match {
      case Right(json) => this.modify(_.payload.json).setTo(json)
      case Left(msg)   =>
        logger.error(msg)
        this
    }
  }

  private[jwt] val jwtAlgorithm: JwtAlgorithm = JwtAlgorithm.fromString(algorithm.toUpperCase)

}
