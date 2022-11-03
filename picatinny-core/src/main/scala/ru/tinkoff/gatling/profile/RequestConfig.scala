package ru.tinkoff.gatling.profile

import io.gatling.core.structure.ChainBuilder

trait RequestConfig {
  val name: String
  val url: String
  val probability: Double

  def toExec: ChainBuilder
  def toTuple: (Double, ChainBuilder)

}
