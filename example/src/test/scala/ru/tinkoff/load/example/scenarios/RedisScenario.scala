package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.redis.Operations.ScenarioAppender

object RedisGetTokenScenario {
  def apply(): ScenarioBuilder = new RedisGetTokenScenario().redisGetTokenScenario
}

  class RedisGetTokenScenario {

  val getToken: HttpRequestBuilder = http("getToken")
    .get("https://httpbin.org/get")
    .check(jsonPath("$..X-Amzn-Trace-Id") saveAs "access_token")

  val redisGetTokenScenario: ScenarioBuilder = scenario("Redis get token scenario")
    .exec(getToken)
    .exec(session => session.set("param", "key0"))
    .exec(session => session.set("param1", List(0,1,2)))
    .exec(session => session.set("param2", 12))
    .exec(session => session.set("22", "key3"))
    .redisSADD1000("access_token22447", "${param1}", "${param2}", "${22}", "ddddddd", 23)
    .exec(session => {

//    .redisDEL("refresh_token")
//    .redisSADD("access_token", session.attributes("access_token") )
      session
    })
//    .redisSADD("refresh_token", session.attributes("refresh_token"))
}