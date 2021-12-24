package ru.tinkoff.gatling
import java.io.InputStream
import scala.util.Random

package object utils {

  def getResource(file: String): InputStream = Option {
    getClass.getResourceAsStream(s"$file")
  }.getOrElse {
    throw new Exception(s"Error loading invalid resource $file")
  }

  def getRandomElement[T](seq: Seq[T]): T = seq(Random.nextInt(seq.length))

}
