package ru.tinkoff.gatling.feeders

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.feeder._
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FeedersBaseSpec extends AnyFlatSpec with Matchers {

  it should "create Feeder object with expected types" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).isInstanceOf[Iterator[Map[String, AnyVal]]]
    }.check()
  }

  it should "generate continually Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).hasNext
    }.check()
  }

  it should "generate Feeder with given params" in {
    forAll { (n: String, v: AnyVal) =>
      feeder[AnyVal](n)(v).take(100).forall(r => r(n) == v)
    }.check()
  }

  it should "zip two feeders" in {
    forAll { (n1: String, n2: String, v1: AnyVal, v2: AnyVal) =>
      val feeder1: Feeder[AnyVal] = Iterator.continually(Map(n1 -> v1))
      val feeder2: Feeder[AnyVal] = Iterator.continually(Map(n2 -> v2))
      val result: Feeder[Any]     = feeder1 ** feeder2
      result.next().equals(Map(n1 -> v1, n2 -> v2))
    }.check()
  }

  it should "prepare feeder with finite size" in {
    forAll { (n: String, v: Char) =>
      val fdr    = RandomDigitFeeder(n)
      val result = fdr.toFiniteLength(v)(GatlingConfiguration.loadForTest())

      result.readRecords.size == v
    }.check()
  }

  it should "transform Collection to Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      val collection = List.fill(100)(v)
      val result     = collection.toFeeder(n)(GatlingConfiguration.loadForTest())

      result.readRecords.forall(r => r.equals(Map(n -> v)))
    }.check()
  }

}
