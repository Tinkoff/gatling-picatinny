package ru.tinkoff.gatling.influxdb

import io.gatling.core.scenario.Simulation

/**
  * Mix this trait in Simulation class to write Start/Stop annotations in influxDb before/after simulation run
  */
trait Annotations {
  simulation: Simulation =>
  {
    simulation.before(AnnotationManager.addAnnotation(Start, "startAnnotation"))

    simulation.after(AnnotationManager.addAnnotation(Stop, "stopAnnotation"))
  }
}
