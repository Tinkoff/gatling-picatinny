package ru.tinkoff.gatling.feeders

import java.util.concurrent.atomic.AtomicLong

import io.gatling.core.feeder.Feeder

object SequentialFeeder {

  def apply(paramName: String, start: Int = 0, step: Int = 1): Feeder[Long] = {
    val value = new AtomicLong(start)
    feeder[Long](paramName)(value.getAndAdd(step))
  }

}
