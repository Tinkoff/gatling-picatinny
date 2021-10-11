package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.config.SimulationConfig._
import ru.tinkoff.gatling.redis.Operations.ScenarioAppender
import ru.tinkoff.gatling.utils.jwt._
import ru.tinkoff.load.example.feeders.Feeders._

object SampleScenario {
  def apply(): ScenarioBuilder = new SampleScenario().sampleScenario
}

class SampleScenario {
  //get sensitive data from ENV
  val jwtSecretToken = getStringParam("jwtSecret")

  //prepare jwt generator
  val jwtGenerator = jwt("HS256", jwtSecretToken) //pass here jwt algorithm and jwt secret key
    .defaultHeader //use default jwt header: {"alg": "HS256","typ": "JWT"}
    .payloadFromResource("jwtTemplates/payload.json") //use payload template from json file

  //get some configuration param from simulation.conf/ENV/JAVA_OPTS
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
    //Add the specified members to the set stored at key
    .redisSADD("myset", "val1")
    //Remove the specified members from the set stored at key
    .redisSREM("myset", "val1")
    //Removes the specified keys
    .redisDEL("myset")
    //generate JWT using values from feeders
    .exec(_.setJwt(jwtGenerator, "jwtToken"))
    //set JWT cookie
    .exec(addCookie(Cookie("JWT_TOKEN", "${jwtToken}").withDomain(jwtCookieDomain).withPath("/")))
    //execute request signed with JWT_TOKEN cookie
    .exec(getMainPage)

}
