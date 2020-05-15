package ru.tinkoff.gatling.influxdb

import io.gatling.core.scenario.Simulation

trait Annotations {
  simulation: Simulation =>
  {
    simulation.before(AnnotationManager.start())

    simulation.after(AnnotationManager.stop())
  }
}
