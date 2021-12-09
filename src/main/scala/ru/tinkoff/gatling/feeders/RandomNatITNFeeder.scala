package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomNatITNFeeder {

  /** Creates a feeder that generates a random ITN of the natural person (Individual Taxpayer Number)
    *
    * ITN is used only in the Russian Federation (ИНН in Russian)
    *
    * @param paramName
    *   feeder's name
    * @return
    *   random string ITN of the natural person feeder
    */
  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomNatITN())

}
