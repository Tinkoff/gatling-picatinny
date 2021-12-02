package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomKPPFeeder {

  /** Creates a feeder that generates a random KPP (Tax Registration Reason Code)
    *
    * KPP is used only in the Russian Federation (КПП in Russian)
    *
    * @param paramName feeder's name
    * @return random string KPP feeder
    */
  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomKPP())

}
