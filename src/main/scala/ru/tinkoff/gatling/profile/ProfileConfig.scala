package ru.tinkoff.gatling.profile

import io.gatling.core.Predef.scenario
import io.gatling.core.structure.ScenarioBuilder

trait ProfileConfig {
  val name: String
  val profile: Seq[RequestConfig]

  def toRandomScenario: ScenarioBuilder = scenario(name)
    .randomSwitch(profile.map(requestConfig => requestConfig.toTuple):_*)

}
