package ru.tinkoff.gatling

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.feeder._

package object feeders {

  def feeder[T](name: String)(f: => T): Feeder[T] = Iterator.continually(Map(name -> f))

  implicit class FeederOps[A](val feeder: Feeder[A]) extends AnyVal {
    def **[B](other: Feeder[B]): Feeder[Any] = feeder.zip(other).map { case (r1, r2) => r1 ++ r2 }

    def toFiniteLength(n: Int)(implicit configuration: GatlingConfiguration): FeederBuilderBase[A] =
      SourceFeederBuilder(InMemoryFeederSource(feeder.take(n).toIndexedSeq), configuration)
  }

  implicit class Collection2FeederOps[A](val sequence: Seq[A]) {
    def toFeeder(name: String)(implicit configuration: GatlingConfiguration): FeederBuilderBase[A] =
      SourceFeederBuilder(InMemoryFeederSource(sequence.map(x => Map(name -> x)).toIndexedSeq), configuration)
  }

}
