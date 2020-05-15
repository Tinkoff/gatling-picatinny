package ru.tinkoff.gatling.profile

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.core.action.builder.RandomSwitchBuilder
import ru.tinkoff.gatling.config.ConfigManager.getProfileConfig
import ru.tinkoff.gatling.config.{ProfileConfig, Request}

abstract class Profile (profileName: String) {

  lazy val profileConfig: ProfileConfig = getProfileConfig(profileName)

  lazy val name: String = profileConfig.name

  protected def buildRequest(request: Request): (Double, ChainBuilder)

  protected def buildAssertions()

  def build(): ScenarioBuilder =  {

    ScenarioBuilder(name, List(new RandomSwitchBuilder(
      profileConfig.requests
      .map(request => buildRequest(request)),
      elseNext = Some(exec()))))
  }

}