package ru.tinkoff.gatling
package feeders
package generators

import cats.syntax.apply._
import shapeless.{::, Generic, HList, HNil, Lazy}

object Implicits {

  implicit val gs: Generator[String]                = alphanumericString
  implicit val gI: Generator[Int]                   = positiveInt
  implicit val gc: Generator[Char]                  = alphanumericChar
  implicit val gb: Generator[Boolean]               = bool
  implicit def gl[T: Generator]: Generator[List[T]] = randomList[T]

  implicit def hNilGenerator: Generator[HNil] = const(HNil)

  implicit def hListGenerator[H, T <: HList](implicit
      hGenerator: Lazy[Generator[H]],
      tGenerator: Generator[T],
  ): Generator[H :: T] = (hGenerator.value, tGenerator).mapN(_ :: _)

  implicit def genericGenerator[A, R](implicit
      gen: Generic.Aux[A, R],
      rGenerator: Lazy[Generator[R]],
  ): Generator[A] = rGenerator.value.map(r => gen.from(r))

}
