package ru.tinkoff.gatling
package feeders
package generators

import cats.syntax.apply._
import shapeless.{::, Generic, HList, HNil, Lazy}

import java.util.UUID

object Implicits {

  implicit val gs: Generator[String]                = alphanumericString
  implicit val gI: Generator[Int]                   = positiveInt
  implicit val glo: Generator[Long]                 = positiveLong
  implicit val dg: Generator[Double]                = double(-1000.0, 20000.0)
  implicit val df: Generator[Float]                 = float(-1000.0f, 20000.0f)
  implicit val gc: Generator[Char]                  = alphanumericChar
  implicit val gb: Generator[Boolean]               = bool
  implicit val ug: Generator[UUID]                  = uuid
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
