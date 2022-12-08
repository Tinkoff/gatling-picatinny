package ru.tinkoff.gatling.javaapi.internal

import java.{util => ju}
import scala.jdk.CollectionConverters._
import io.gatling.core.feeder._
import scala.jdk.OptionConverters._

object Feeders {
  def toJavaFeeder[T](scalaFeeder: Feeder[T]): ju.Iterator[ju.Map[String, Object]] =
    scalaFeeder.map(_.asJava).asJava.asInstanceOf[ju.Iterator[ju.Map[String, Object]]]

  def toJavaFeeder(scalaFeeder: IndexedSeq[Record[String]]): ju.Iterator[ju.Map[String, Object]] =
    scalaFeeder.map(_.asJava).asJava.iterator().asInstanceOf[ju.Iterator[ju.Map[String, Object]]]

  def toScalaOption[T](optionJava: ju.Optional[T]): Option[T] = optionJava.toScala

  def toScala(col: ju.List[ju.Map[String, Object]]): Seq[Map[String, Any]] = col.asScala.toSeq.map(x => x.asScala.toMap)
}
