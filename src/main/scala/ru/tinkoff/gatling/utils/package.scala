package ru.tinkoff.gatling
import scala.util.Random

package object utils {

  def getRandomElement[T](seq: Seq[T]): T = seq(Random.nextInt(seq.length))

}
