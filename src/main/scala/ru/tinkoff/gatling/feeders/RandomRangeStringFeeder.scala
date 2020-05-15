package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomRangeStringFeeder {

  lazy val alphabet = """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#%"&*()_-+={}<>?|:[].~"""

  def apply(paramName: String, from: Int = 10, to: Int = 15, alphabet: String = alphabet): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomString(alphabet)(RandomDataGenerators.randomDigit(from, to)))

}
