package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.redis.Operations.ScenarioAppender

object RedisScenario {
  def apply(): ScenarioBuilder = new RedisScenario().redisGetTokenScenario
}

  class RedisScenario {

  val getParameter: HttpRequestBuilder = http("GET PARAMETER")
    .get("/")
    .check(regex("""SPDX-License-Identifier: (\w.+)""").saveAs("parameter_1"))
    .check(regex("""Copyright (\w.+)""").saveAs("parameter_2"))

  val redisGetTokenScenario: ScenarioBuilder = scenario("Redis scenario")
    .exec(getParameter)
    //Add the specified members to the set stored at key
    .redisSADD("key", "${parameter_1}", "${parameter_2}")
    //Remove the specified members from the set stored at key
    .redisSREM("key","${parameter_1}")
    //Removes the specified keys
    .redisDEL("key", "key1")

}