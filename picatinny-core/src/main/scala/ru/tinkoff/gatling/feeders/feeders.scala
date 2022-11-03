package ru.tinkoff.gatling

import io.gatling.core.feeder._
import ru.tinkoff.gatling.feeders.generators.Generator

package object feeders {

  def feeder[T](name: String)(f: => T): Feeder[T] = Iterator.continually(Map(name -> f))

  def feederT[T: Generator](name: String): Feeder[T] = implicitly[Generator[T]].toFeeder(name)

  implicit class FeederOps[A](val feeder: Feeder[A]) extends AnyVal {
    def **[B](other: Feeder[B]): Feeder[Any] = feeder.zip(other).map { case (r1, r2) => r1 ++ r2 }

    def toFiniteLength(n: Int): IndexedSeq[Map[String, A]] = feeder.take(n).toIndexedSeq
  }

  implicit class Collection2FeederOps[A](val sequence: Seq[A]) {
    def toFeeder(name: String): IndexedSeq[Map[String, A]] = sequence.map(x => Map(name -> x)).toIndexedSeq
  }

}
