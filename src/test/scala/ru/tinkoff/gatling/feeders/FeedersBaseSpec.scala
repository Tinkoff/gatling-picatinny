package ru.tinkoff.gatling.feeders

import io.gatling.core.feeder.Feeder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import org.scalatest.prop._
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalatest._

class FeedersBaseSpec extends AnyFlatSpec with Matchers {

  it should "create Feeder object with expected types" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).isInstanceOf[Iterator[Map[String, AnyVal]]]
    }.check
  }

  it should "generate continually Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).hasNext
    }.check
  }

  it should "generate Feeder with given params" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).take(100).forall(r => r(n).equals(v))
    }.check
  }

  it should "zip two feeders" in {
    forAll { (n1: String, n2: String, v1: AnyVal, v2: AnyVal) =>
      val feeder1: Feeder[AnyVal] = Iterator.continually(Map(n1 -> v1))
      val feeder2: Feeder[AnyVal] = Iterator.continually(Map(n2 -> v2))
      val result: Feeder[Any]     = feeder1 ** feeder2
      result.next().equals(Map(n1 -> v1, n2 -> v2))
    }.check
  }

  it should "transform values of this feeder" in {
    forAll { (n: String, v: Int) =>
      val result = List.fill(20)(v).toFeeder(n).mapValues(_ + 1)

      result.forall(_.equals(Map(n -> (v + 1))))
    }.check
  }

  it should "transform Collection to Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      val collection = List.fill(100)(v)
      val result     = collection.toFeeder(n)

      result.forall(r => r.equals(Map(n -> v)))
    }.check
  }

}
