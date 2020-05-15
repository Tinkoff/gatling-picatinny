package ru.tinkoff.gatling.config

import pureconfig._
import pureconfig.generic.auto._

case class Request(requestName: String, method: String, url: String, prob: Double)
case class ProfileConfig(name: String, requests: List[Request])
