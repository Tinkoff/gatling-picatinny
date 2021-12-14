package ru.tinkoff.gatling.utils.scalaFaker

import io.circe.Decoder
import io.circe.parser.decode

import java.io.InputStream
import scala.util.Random

trait Faker {

  def getResource(file: String): InputStream = Option {
    getClass.getResourceAsStream(s"$file")
  }.getOrElse {
    throw new Exception(s"Error loading invalid resource $file")
  }

  def getRandomElement[T](xs: Seq[T]): Option[T] =
    if (xs.isEmpty) None else Some(xs(Random.nextInt(xs.size)))

  def objectFrom[T](s: String)(implicit decoder: Decoder[T]): T = {
    decode[T](s) match {
      case Right(value) => value
      case Left(error)  => throw new Exception(error.getMessage)
    }
  }
}
