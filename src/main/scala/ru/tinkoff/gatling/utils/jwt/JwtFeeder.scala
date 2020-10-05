package ru.tinkoff.gatling.utils.jwt

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object JwtFeeder {

  implicit class ScenarioAppender(build: ScenarioBuilder) {
    def setJwt(jwtGenerator: JwtGenerator, tokenName: String, secretToken: String, jwtAlgorithm: String)(
        implicit s: Session): ScenarioBuilder =
      jwtGenerator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
  }

  implicit class ChainAppender(build: ChainBuilder) {
    def setJwt(jwtGenerator: JwtGenerator, tokenName: String, secretToken: String, jwtAlgorithm: String)(
        implicit s: Session): ChainBuilder =
      jwtGenerator
        .generateJwt(secretToken, jwtAlgorithm)
        .map(jwt => build.exec(_.set(tokenName, jwt)))
        .getOrElse(build)
  }

}
