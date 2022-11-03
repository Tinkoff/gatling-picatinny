package ru.tinkoff.gatling.feeders.generators

import cats.Eval
import cats.data.ReaderT

object Generator {
  def apply[T](f: GeneratorContext => T): Generator[T] = ReaderT(r => Eval.now(f(r)))
}
