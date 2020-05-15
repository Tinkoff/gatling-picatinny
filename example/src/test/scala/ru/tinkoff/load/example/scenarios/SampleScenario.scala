package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.load.example.feeders.Feeders._

object SampleScenario {
  def apply(): ScenarioBuilder = new SampleScenario().sampleScenario
}

class SampleScenario {

  val getMainPage: HttpRequestBuilder = http("GET /")
    .get("/")
    .check(status is 200)

  //include feeders in scenario as usual
  val sampleScenario: ScenarioBuilder = scenario("Sample scenario")
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
    .exec(getMainPage)

}
