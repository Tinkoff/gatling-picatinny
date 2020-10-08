package ru.tinkoff.gatling.utils

import io.gatling.core.Predef.Session
import io.gatling.core.session.el.ElCompiler
import pdi.jwt.Jwt

package object jwt {
  def jwt(algorithm: String, secret: String): JwtGeneratorBuilder = {
    JwtGeneratorBuilder(Header(algorithm), Payload(), algorithm, secret)
  }

  implicit class SessionAppender(s: Session) {
    def setJwt(generator: JwtGeneratorBuilder, tokenName: String): Session = {
      def compileResource(json: String)(implicit s: Session): Option[String] =
        ElCompiler.compile[String](json).apply(s).toOption

      def generateJwt(implicit s: Session): Option[String] = {
        compileResource(generator.header.json).flatMap(header =>
          compileResource(generator.payload.json).map(payload =>
            Jwt.encode(header, payload, generator.secret, generator.jwtAlgorithm)))
      }
      s.set(tokenName, generateJwt(s).get)
    }
  }

}
