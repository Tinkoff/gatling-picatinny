package ru.tinkoff.gatling.utils

import io.gatling.core.Predef.Session

package object jwt {
  implicit class SessionAppender(s: Session) {
    def setJwt(jwtGenerator: JwtGenerator, tokenName: String, secretToken: String, jwtAlgorithm: String): Session =
      s.set(tokenName, jwtGenerator.generateJwt(secretToken, jwtAlgorithm)(s))
  }
}
