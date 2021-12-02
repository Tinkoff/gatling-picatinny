package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import ru.tinkoff.gatling.utils.RandomDataGenerators

object RandomOGRNFeeder {

  /** Creates a feeder that generates a random OGRN (Primary State Registration Number)
    *
    * OGRN is used only in the Russian Federation (ОГРН in Russian)
    *
    * @param paramName feeder's name
    * @return random string OGRN feeder
    */
  def apply(paramName: String): Feeder[String] =
    feeder[String](paramName)(RandomDataGenerators.randomOGRN())

}
