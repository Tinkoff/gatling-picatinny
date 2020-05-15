package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder

object CustomFeeder {

  def apply[T](paramName: String, f: => T): Feeder[T] =
    feeder[T](paramName)(f)

}
