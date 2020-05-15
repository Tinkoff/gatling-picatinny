package ru.tinkoff.gatling.utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import ru.tinkoff.gatling.config.SimulationConfig.baseUrl
import ru.tinkoff.gatling.influxdb.Annotations
import ru.tinkoff.gatling.profile._

import scala.concurrent.duration._

class DebugLoadTest extends Simulation{

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl) // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn: ScenarioBuilder = HttpProfile(
    ProfileConfigManager
    .profileConfigLoad()
  ).build()

  println(scn)

  setUp(
    scn.inject(
      rampUsersPerSec(0) to 1 during(1 second),
      constantUsersPerSec(1) during(1 minute)
    )
  )

}
