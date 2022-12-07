package ru.tinkoff.gatling.utils

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.Predef.Session
import pdi.jwt.Jwt
import io.gatling.core.session.el._

package object jwt extends StrictLogging {
  def jwt(algorithm: String, secret: String): JwtGeneratorBuilder = {
    JwtGeneratorBuilder(Header(), Payload(), algorithm, secret)
  }

  implicit class SessionAppender(s: Session) {
    def setJwt(generator: JwtGeneratorBuilder, tokenName: String): Session = {
      (for {
        header  <- generator.header.json.el[String].apply(s)
        payload <- generator.payload.json.el[String].apply(s)
      } yield s.set(tokenName, Jwt.encode(header, payload, generator.secret, generator.jwtAlgorithm))) match {
        case Success(session) => session
        case Failure(message) => logger.error(s"Jwt generation failed: $message"); s
      }
    }
  }

}
