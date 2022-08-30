package ru.tinkoff.load.example

import io.gatling.core.Predef._
import ru.tinkoff.gatling.assertions.AssertionsBuilder.assertionFromYaml
import ru.tinkoff.gatling.config.SimulationConfig._
import ru.tinkoff.gatling.influxdb.Annotations
import ru.tinkoff.gatling.profile.ProfileBuilder
import ru.tinkoff.gatling.profile.http.{HttpProfileConfig, buildHttpProfileFromYaml}
import ru.tinkoff.gatling.utils.IntensityConverter._
import ru.tinkoff.load.example.scenarios.{SampleScenario, ProfileScenario}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import pureconfig.generic.auto._
import ru.tinkoff.load.stargazer.infrastructure.{Params, ProfileYamlParser, Request}
import zio.Task

import scala.language.postfixOps

/** trait Annotations allows you to write Start annotation to influxdb before starting the simulation and Stop annotation after
  * completion of the simulation
  */
class ProfileSimulation extends Simulation with Annotations {

  /** how to get custom params from simulation.conf OR from JVM params like -DparamName=""
    */
  val stageWeight                    = getDoubleParam("stageWeight")
  val startIntensity                 = getDoubleParam("startIntensity")
  val warmUpDuration                 = getDurationParam("warmUp")
//  val config: HttpProfileConfig = new ProfileBuilder[HttpProfileConfig].buildFromYaml(profileConfigName)
//  val scn: ScenarioBuilder = config.toRandomScenario

//  val intensitySum = profileRequests.map(_.).sum
  // надо 1) получить сумму intensity,
  //      2) для каждого реквеста посчитать вес = intensity/intensitySum (добавить в профиль?)
//        3) ккаим-то образом составить сценарий?? (или это вообще не надо)

//  val config: HttpProfileConfig = new ProfileBuilder[HttpProfileConfig].buildFromYaml(profileConfigName)
//  val scn: ScenarioBuilder = config.toRandomScenario

  /** intensity, stagesNumber, stageDuration, rampDuration, testDuration, baseUrl - default provided params. Values are taken
    * from the simulation.conf or -DparamName="". Passing this params to the simulation is not required if you do not use them.
    *
    * warmUpDuration, stageWeight, startIntensity - custom params
    */
  setUp(
    ProfileScenario().inject(
      rampUsersPerSec(3600 rph) // IntensityConverter rph convert this to 1.0 Double value
        to (120 rpm)            // IntensityConverter rpm convert this to 2.0 Double value
        during warmUpDuration,
      incrementUsersPerSec(intensity * stageWeight)
        .times(stagesNumber)
        .eachLevelLasting(stageDuration)
        .separatedByRampsLasting(rampDuration)
        .startingFrom(startIntensity),
    ),
  ).protocols(httpProtocol)
    .maxDuration(testDuration)
    .assertions(assertionFromYaml("src/test/resources/nfr.yml"))

}
