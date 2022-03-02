package ru.tinkoff.gatling.feeders.generators

final case class SizeBounds(string: Int, collection: Int)

object SizeBounds {
  val default: SizeBounds = SizeBounds(string = 100, collection = 20)
}
