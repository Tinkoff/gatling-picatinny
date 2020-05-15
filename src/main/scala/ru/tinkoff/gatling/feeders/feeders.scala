package ru.tinkoff.gatling

import io.gatling.core.feeder.Feeder

package object feeders {

  def feeder[T](name: String)(f: => T): Feeder[T] = Iterator.continually(Map(name -> f))

  implicit class FeederOps[A](val feeder: Feeder[A]) extends AnyVal {
    def **[B](other: Feeder[B]): Feeder[Any] = feeder.zip(other).map { case (r1, r2) => r1 ++ r2 }
  }

}
