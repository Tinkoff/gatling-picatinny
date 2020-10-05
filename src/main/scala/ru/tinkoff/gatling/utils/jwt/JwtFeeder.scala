package ru.tinkoff.gatling.utils.jwt

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object JwtFeeder {
  def apply(headerPath: String, payloadPath: String, tokenName: String, secretToken: String, jwtAlgorithm: String): JwtFeeder =
    new JwtFeeder(headerPath, payloadPath, tokenName, secretToken, jwtAlgorithm)
}

class JwtFeeder(headerPath: String, payloadPath: String, tokenName: String, secretToken: String, jwtAlgorithm: String) {

  val generator = JwtGenerator(headerPath, payloadPath)

  implicit class ScenarioAppender(build: ScenarioBuilder)(implicit s: Session) {
    def setJwt(tokenName: String,
               secretToken: String,
               jwtAlgorithm: String): ScenarioBuilder = {
      generator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
    }
  }

  implicit class ChainAppender(build: ChainBuilder)(implicit s: Session) {
    def setJwt(tokenName: String,
               secretToken: String,
               jwtAlgorithm: String): ChainBuilder = {
      generator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
    }
  }
}
