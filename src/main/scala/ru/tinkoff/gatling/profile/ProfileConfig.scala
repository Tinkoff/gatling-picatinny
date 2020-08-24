package ru.tinkoff.gatling.profile

import io.gatling.core.Predef.scenario
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

trait ProfileConfig {
  val name: String
  protected val profile: Seq[RequestConfig]

  def toRandomScenario: ScenarioBuilder = scenario(name)
    .randomSwitch(profile.map(requestConfig => (requestConfig.probability, requestConfig.toExec)):_*)

}
