package ru.tinkoff.gatling.utils.jwt

import io.gatling.core.Predef.Session
import io.gatling.core.session.el.ElCompiler
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.io.Source

object JwtGenerator {
  def apply(headerPath: String, payloadPath: String): JwtGenerator =
    new JwtGenerator(headerPath, payloadPath)
}

private[jwt] class JwtGenerator(headerPath: String, payloadPath: String) {

  def readResource(path: String): Option[String] = Option(Source.fromResource(path).mkString)

  val header: Option[String]  = readResource(headerPath)
  val payload: Option[String] = readResource(payloadPath)

  def compileResource(json: String)(implicit s: Session): Option[String] =
    ElCompiler.compile[String](json).apply(s).toOption

  def jwtAlgorithmFromString(jwtAlgorithm: String): Option[JwtAlgorithm] =
    JwtAlgorithm.optionFromString(jwtAlgorithm.toUpperCase)

  def encodeJwt(header: String, payload: String, secretToken: String, jwtAlgorithm: JwtAlgorithm): String = {
    Jwt.encode(header, payload, secretToken, jwtAlgorithm)
  }

  def generateJwt(secretToken: String, jwtAlgorithm: String)(implicit s: Session): Option[String] = {
    for {
      header          <- header
      payload         <- payload
      compiledHeader  <- compileResource(header)
      compiledPayload <- compileResource(payload)
      jwtAlgorithm    <- jwtAlgorithmFromString(jwtAlgorithm)
    } yield encodeJwt(compiledHeader, compiledPayload, secretToken, jwtAlgorithm)
  }
}
