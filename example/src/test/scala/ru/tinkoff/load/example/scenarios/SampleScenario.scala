package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.load.example.feeders.Feeders._
import ru.tinkoff.gatling.utils.jwt._
import ru.tinkoff.gatling.config.SimulationConfig._

object SampleScenario {
  def apply(): ScenarioBuilder = new SampleScenario().sampleScenario
}

class SampleScenario {

  //load JWT templates in memory
  val jwtGenerator = JwtGenerator("jwtTemplates/header.json", "jwtTemplates/payload.json")
  //get sensitive data from ENV or JAVA_OPTS
  val secretToken  = getStringParam("secretToken")

  val jwtCookieDomain = getStringParam("domain")

  //single request
  val getMainPage: HttpRequestBuilder = http("GET /")
    .get("/")
    .check(status is 200)

  //compose all in scenario
  val sampleScenario: ScenarioBuilder = scenario("Sample scenario")
  //include feeders in scenario as usual
    .feed(firstWorkDayHours)
    .feed(holidaysDate)
    .feed(myCustomFeeder)
    .feed(randomDigit)
    .feed(randomPhone)
    .feed(randomRangeString)
    .feed(randomString)
    .feed(randomUsaPhone)
    .feed(randomUuid)
    .feed(sequenceLong)
    .feed(simpleRandomDate)
    .feed(vacationDate)
    .feed(gluedTogetherFeeder)
    .feed(list2feeder)
    .feed(finiteRandomDigitsWithTransform)
    .feed(regexString)
    //generate JWT using feeders current data
    .exec(_.setJwt(jwtGenerator, "myToken", secretToken, "HS256"))
    //set JWT cookie
    .exec(addCookie(Cookie("JWT_TOKEN", "${myToken}", Option(jwtCookieDomain), Option("/"))))
    //execute request
    .exec(getMainPage)

}
