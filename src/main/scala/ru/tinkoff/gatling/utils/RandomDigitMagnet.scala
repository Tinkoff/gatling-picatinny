package ru.tinkoff.gatling.utils

import java.util.concurrent.ThreadLocalRandom

private[gatling] object RandomDigitMagnet {

  trait DigitMagnet {
    type Result
    def RandomImpl: Result
  }

  object DigitMagnet {

    implicit def RandomInt(max: Int): DigitMagnet = new DigitMagnet {
      override type Result = Int

      override def RandomImpl: Int = ThreadLocalRandom.current().nextInt(max)
    }

    implicit def RandomInt(intTuple: (Int, Int)): DigitMagnet = new DigitMagnet {
      override type Result = Int
      val (min, max) = intTuple

      override def RandomImpl: Int = ThreadLocalRandom.current().nextInt(min, max)
    }

    implicit def RandomLong(max: Long): DigitMagnet = new DigitMagnet {
      override type Result = Long

      override def RandomImpl: Long = ThreadLocalRandom.current().nextLong(max)
    }

    implicit def RandomLong(longTuple: (Long, Long)): DigitMagnet = new DigitMagnet {
      override type Result = Long
      val (min, max) = longTuple

      override def RandomImpl: Long = ThreadLocalRandom.current().nextLong(min, max)
    }

    implicit def RandomDouble(max: Double): DigitMagnet = new DigitMagnet {
      override type Result = Double

      override def RandomImpl: Double = ThreadLocalRandom.current().nextDouble(max)
    }

    implicit def RandomDouble(doubleTuple: (Double, Double)): DigitMagnet = new DigitMagnet {
      override type Result = Double
      val (min, max) = doubleTuple

      override def RandomImpl: Double = ThreadLocalRandom.current().nextDouble(min, max)
    }

    implicit def RandomFloat(max: Float): DigitMagnet = new DigitMagnet {
      override type Result = Float

      override def RandomImpl: Float = max * ThreadLocalRandom.current().nextFloat()
    }

    implicit def RandomFloat(floatTuple: (Float, Float)): DigitMagnet = new DigitMagnet {
      override type Result = Float
      val (min, max) = floatTuple

      override def RandomImpl: Float = min + (max - min) * ThreadLocalRandom.current().nextFloat()
    }
  }

}
