package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import com.mifmif.common.regex.Generex

object RegexFeeder {

  def apply(paramName: String, regex: String): Feeder[String] = {
    val generex = new Generex(regex).iterator()
    feeder[String](paramName)(generex.next())
  }

}
