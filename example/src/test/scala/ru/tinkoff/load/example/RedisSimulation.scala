package ru.tinkoff.load.example

import io.gatling.core.Predef._
import ru.tinkoff.load.example.scenarios.RedisScenario

class RedisSimulation extends Simulation {
  setUp(
    RedisScenario().inject(
      atOnceUsers(1)
    )
  ).protocols(httpProtocol)
}
