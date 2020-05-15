package ru.tinkoff.load.example

import io.gatling.core.Predef._
import ru.tinkoff.gatling.config.SimulationConfig._
import ru.tinkoff.gatling.influxdb.Annotations
import ru.tinkoff.load.example.scenarios.SampleScenario
import ru.tinkoff.gatling.utils.IntensityConverter._

/**
  * trait Annotations allows you to write Start annotation to influxdb before starting the simulation
  * and Stop annotation after completion of the simulation
  */
class SampleSimulation extends Simulation with Annotations {

  /**
    * how to get custom params from simulation.conf OR from JVM params like -DparamName=""
    */
  val stageWeight    = getDoubleParam("stageWeight")
  val startIntensity = getDoubleParam("startIntensity")
  val warmUpDuration = getDurationParam("warmUp")

  /**
    * intensity, stagesNumber, stageDuration, rampDuration, testDuration, baseUrl - default provided params.
    * Values are taken from the simulation.conf or -DparamName="".
    * Passing this params to the simulation is not required if you do not use them.
    *
    * warmUpDuration, stageWeight, startIntensity - custom params
    */
  setUp(
    SampleScenario().inject(
      rampUsersPerSec(3600 rph) //IntensityConverter rph convert this to 1.0 Double value
        to (120 rpm)            //IntensityConverter rpm convert this to 2.0 Double value
        during warmUpDuration,
      incrementUsersPerSec(intensity * stageWeight)
        .times(stagesNumber)
        .eachLevelLasting(stageDuration)
        .separatedByRampsLasting(rampDuration)
        .startingFrom(startIntensity)
    )
  ).protocols(httpProtocol)
    .maxDuration(testDuration)

}
