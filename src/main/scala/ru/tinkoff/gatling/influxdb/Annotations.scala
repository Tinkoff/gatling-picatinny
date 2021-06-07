package ru.tinkoff.gatling.influxdb

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, PopulationBuilder, ScenarioBuilder}
import io.razem.influxdbclient.Point
import io.gatling.core.session.el._

/**
  * Mix this trait in Simulation class to write Start/Stop annotations in influxDb before/after simulation run
  */
trait Annotations {
  simulation: Simulation =>
  {
    simulation.before(AnnotationManager.addStatusAnnotation(Start))

    simulation.after(AnnotationManager.addStatusAnnotation(Stop))
  }
}

/**
  * Use this object to write custom points to InfluxDB
  */
object Annotations {

  def influxDataPoint(tagKey: String, tagValue: String, fieldKey: String, fieldValue: String) = {
    AnnotationManager.addCustomAnnotation(tagKey, tagValue, fieldKey, fieldValue)
  }

  def influxDataPoint(point: Point) = {
    AnnotationManager.addCustomPoint(point)
  }

  def influxDataPoint(point: Seq[Point]) = {
    AnnotationManager.addCustomPoints(point)
  }

  //for write custom points from setUp
  def userDataPoint(uniqScnName: String, point: Point): PopulationBuilder = {
    //TODO: how to do it without scenario?
    scenario(uniqScnName)
      .userDataPoint(point)
      .inject(atOnceUsers(1))
  }

  //for write default prepared points from setUp
  def userDataPoint(uniqScnName: String,
                    tagKey: String,
                    tagValue: String,
                    fieldKey: String,
                    fieldValue: String): PopulationBuilder = {
    //TODO: how to do it without scenario?
    scenario(uniqScnName)
      .userDataPoint(tagKey, tagValue, fieldKey, fieldValue)
      .inject(atOnceUsers(1))
  }

  //for usage in chain builder
  implicit class ChainAppender(cb: ChainBuilder) {
    def userDataPoint(point: Point): ChainBuilder =
      cb.exec(session => {
        influxDataPoint(point)
        session
      })

    def userDataPoint(tagKey: String, tagValue: String, fieldKey: String, fieldValue: String): ChainBuilder =
      cb.exec(session => {
        for {
          tag   <- tagValue.el[String].apply(session)
          field <- fieldValue.el[String].apply(session)
        } yield influxDataPoint(tagKey, tag, fieldKey, field)
        session
      })
  }

  //for usage in scenario
  implicit class ScenarioAppender(sb: ScenarioBuilder) {
    def userDataPoint(point: Point): ScenarioBuilder =
      sb.exec(session => {
        influxDataPoint(point)
        session
      })

    def userDataPoint(tagKey: String, tagValue: String, fieldKey: String, fieldValue: String): ScenarioBuilder =
      sb.exec(session => {
        for {
          tag   <- tagValue.el[String].apply(session)
          field <- fieldValue.el[String].apply(session)
        } yield influxDataPoint(tagKey, tag, fieldKey, field)
        session
      })
  }

  //for usage in simulation setUp
  implicit class PopulationBuilderAppender(pb: PopulationBuilder) {
    def userDataPoint(uniqScpName: String, point: Point): PopulationBuilder = {
      pb.andThen(Annotations.userDataPoint(uniqScpName, point))
    }

    def userDataPoint(uniqScpName: String,
                      tagKey: String,
                      tagValue: String,
                      fieldKey: String,
                      fieldValue: String): PopulationBuilder = {
      pb.andThen(Annotations.userDataPoint(uniqScpName, tagKey, tagValue, fieldKey, fieldValue))
    }
  }

}
