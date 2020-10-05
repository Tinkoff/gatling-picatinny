package ru.tinkoff.gatling.utils.jwt

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object JwtFeeder {

  implicit class ScenarioAppender(build: ScenarioBuilder)(implicit s: Session) {
    def setJwt(jwtGenerator: JwtGenerator, tokenName: String, secretToken: String, jwtAlgorithm: String): ScenarioBuilder =
      jwtGenerator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
  }

  implicit class ChainAppender(build: ChainBuilder)(implicit s: Session) {
    def setJwt(jwtGenerator: JwtGenerator, tokenName: String, secretToken: String, jwtAlgorithm: String): ChainBuilder =
      jwtGenerator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
  }

}
