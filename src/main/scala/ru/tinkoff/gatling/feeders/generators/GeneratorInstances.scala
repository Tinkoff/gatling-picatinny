package ru.tinkoff.gatling.feeders.generators

import cats.Eval
import cats.data.ReaderT

trait GeneratorInstances {
  final type Generator[T] = ReaderT[Eval, GeneratorContext, T]

  final def const[T](t: T): Generator[T] = ReaderT.pure(t)

  final def int(min: Int, max: Int): Generator[Int] = Generator { ctx =>
    val res =
      if (max < Int.MaxValue) ctx.gen.between(min, max + 1)
      else if (min > Int.MinValue) ctx.gen.between(min - 1, max) + 1
      else ctx.gen.nextInt()

    res
  }

  final def long(min: Long, max: Long): Generator[Long] = Generator { ctx =>
    val res =
      if (max < Int.MaxValue) ctx.gen.between(min, max + 1)
      else if (min > Int.MinValue) ctx.gen.between(min - 1, max) + 1
      else ctx.gen.nextLong()

    res
  }

  final def intBounded(bound: Int): Generator[Int]    = Generator(_.gen.nextInt(bound))
  final def longBounded(bound: Long): Generator[Long] = Generator(_.gen.nextLong(bound))

  private val stringSize: Generator[Int]     = Generator(ctx => ctx.gen.nextInt(ctx.sizeBounds.string))
  private val collectionSize: Generator[Int] = Generator(ctx => ctx.gen.nextInt(ctx.sizeBounds.collection))

  final val positiveInt: Generator[Int]   = intBounded(Int.MaxValue)
  final val positiveLong: Generator[Long] = longBounded(Long.MaxValue)

  final val bool: Generator[Boolean] = Generator(_.gen.nextBoolean())

  final def oneOf[T](seq: Seq[T]): Generator[T] = intBounded(seq.size).map(seq)

  final def oneOf[T](t1: T, t2: T, ts: T*): Generator[T] = {
    val all = t1 +: t2 +: ts
    intBounded(all.size).map(all)
  }

  final def oneOf[T](g1: Generator[T], g2: Generator[T], gs: Generator[T]*): Generator[T] = {
    val all = g1 +: g2 +: gs
    intBounded(all.size).flatMap(all)
  }

  final def listOfN[T](gen: Generator[T], n: Int): Generator[List[T]] =
    Generator(r => LazyList.continually(gen(r).value).take(n).toList)

  final def randomList[T: Generator]: Generator[List[T]] = collectionSize.flatMap(listOfN(implicitly[Generator[T]], _))

  final def stringOfN(gen: Generator[Char], n: Int): Generator[String] =
    Generator(r => LazyList.continually(gen(r).value).take(n).mkString)

  final def char(min: Char, max: Char): Generator[Char] = int(min.toInt, max.toInt).map(_.toChar)

  final val upperChar: Generator[Char]        = char('A', 'Z')
  final val lowerChar: Generator[Char]        = char('a', 'z')
  final val digitChar: Generator[Char]        = char('0', '9')
  final val alphaChar: Generator[Char]        = oneOf(upperChar, lowerChar)
  final val alphanumericChar: Generator[Char] = oneOf(upperChar, lowerChar, digitChar)
  final val printableChar: Generator[Char]    = char(33, 126)

  final def alphaStringN(n: Int): Generator[String]        = stringOfN(alphaChar, n)
  final def numberStringN(n: Int): Generator[String]       = stringOfN(digitChar, n)
  final def alphanumericStringN(n: Int): Generator[String] = Generator(_.gen.alphanumeric.take(n).mkString)
  final def printableStringN(n: Int): Generator[String]    = stringOfN(printableChar, n)

  final val alphaString: Generator[String]        = stringSize.flatMap(alphaStringN)
  final val numberString: Generator[String]       = stringSize.flatMap(numberStringN)
  final val alphanumericString: Generator[String] = stringSize.flatMap(alphanumericStringN)
  final val printableString: Generator[String]    = stringSize.flatMap(printableStringN)

  final def numberStringF(format: String, min: Int, max: Int): Generator[String] = int(min, max).map(format.format(_))

  final val KPP: Generator[String] = numberStringN(4) ~ numberStringN(2) ~ numberStringN(3)

}
