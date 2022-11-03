package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomPSRNSPFeeder {

  /** Creates a feeder that generates a random PSRNSP (Primary State Registration Number of the Sole Proprietor)
    *
    * PSRNSP is used only in the Russian Federation (ОГРНИП in Russian)
    *
    * @param paramName
    *   feeder's name
    * @return
    *   random string PSRNSP feeder
    */
  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomPSRNSP())

}
