package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomSNILSFeeder {

  /** Creates a feeder that generates a random SNILS (Insurance Number of Individual Ledger Account)
    *
    * SNILS is used only in the Russian Federation (СНИЛС in Russian)
    *
    * @param paramName feeder's name
    * @return random string SNILS feeder
    */
  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomSNILS())

}
