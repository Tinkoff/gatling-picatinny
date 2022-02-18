package ru.tinkoff.gatling.feeders.generators

import cats.syntax.apply._
import cats.syntax.semigroup._
import ru.tinkoff.gatling.feeders.feeder

trait Syntax {
  final implicit def strGeneratorOps(g: Generator[String]): Syntax.StrGeneratorOps = new Syntax.StrGeneratorOps(g)
  final implicit def strToGeneratorOps(s: String): Syntax.StrToGeneratorOps        = new Syntax.StrToGeneratorOps(s)
  final implicit def charToGeneratorOps(c: Char): Syntax.CharToGeneratorOps        = new Syntax.CharToGeneratorOps(c)
  final implicit def toConst[T](t: T): Generator[T]                                = const(t)

  final implicit def generatorToFeeder[T](g: Generator[T]): Syntax.GeneratorToFeeder[T] = new Syntax.GeneratorToFeeder(g)
}

object Syntax {
  private class StringOrChar[T]
  private[this] object StringOrChar {
    implicit object CharWitness   extends StringOrChar[Char]
    implicit object StringWitness extends StringOrChar[String]
  }

  case class SeparatorStep(gStr: Generator[String], n: Int) {
    def separateBy(sep: String): Generator[String] =
      (1 to n).map(_ => gStr).reduce((x, y) => x.flatMap(xv => y.map(yv => s"$xv$sep$yv")))
  }

  final class GeneratorToFeeder[T](val g: Generator[T]) extends AnyVal {
    @inline
    def toFeeder(name: String): io.gatling.core.feeder.Feeder[T] = feeder(name)(g(GeneratorContext.default).value)
  }

  final class StrGeneratorOps(val gStr: Generator[String]) extends AnyVal {

    @inline
    def ~[T: StringOrChar](g2: Generator[T]): Generator[String] =
      (gStr, g2).mapN((s1, s2) => s1 + s2.toString)

    @inline
    def ~(s2: String): Generator[String] = gStr.map(s1 => s1 + s2)

    @inline
    def ~(char: Char): Generator[String] = gStr.map(s1 => s1.appended(char))

    @inline
    def **(n: Int): Generator[String] = (1 to n).map(_ => gStr).reduce(_ |+| _)

    @inline
    def repeat(n: Int): SeparatorStep =
      SeparatorStep(gStr, n)

  }

  final class StrToGeneratorOps(val s1: String) extends AnyVal {
    @inline
    def ~[T: StringOrChar](g2: Generator[T]): Generator[String] = g2.map(s2 => s1 + s2)
  }

  final class CharToGeneratorOps(val c1: Char) extends AnyVal {
    @inline
    def ~[T: StringOrChar](g2: Generator[T]): Generator[String] = g2.map(s2 => c1 + s2.toString)
  }
}
