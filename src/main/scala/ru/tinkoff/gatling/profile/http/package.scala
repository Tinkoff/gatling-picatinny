package ru.tinkoff.gatling.profile

import pureconfig.ConfigReader
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

package object http {

  implicit val httpMethodConvert: ConfigReader[HttpMethodConfig] = deriveEnumerationReader[HttpMethodConfig]

  lazy val buildHttpProfileFromYaml: String => HttpProfileConfig =
    (path: String) => new ProfileBuilder[HttpProfileConfig]().buildFromYaml(path)

  lazy val buildHttpProfileFromConf: String => HttpProfileConfig =
    (path: String) => new ProfileBuilder[HttpProfileConfig].buildFromConf(path)

}
