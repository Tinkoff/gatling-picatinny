package ru.tinkoff.gatling.feeders

import io.gatling.core.CoreDsl
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.feeder._
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class FeedersBaseSpec extends AnyFlatSpec with Matchers with CoreDsl with ScalaCheckDrivenPropertyChecks {

  override implicit def configuration: GatlingConfiguration = GatlingConfiguration.loadForTest()

  it should "create Feeder object with expected types" in {
    forAll { (n: String, v: AnyVal) =>
      assert(feeder[AnyVal](n)(v).isInstanceOf[Iterator[Map[String, AnyVal]]])
    }
  }

  it should "generate continually Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      assert(feeder[AnyVal](n)(v).hasNext)
    }
  }

  it should "generate Feeder with given params" in {
    forAll { (n: String, v: AnyVal) =>
      assert(feeder[AnyVal](n)(v).take(100).forall(r => r(n) == v))
    }
  }

  it should "zip two feeders" in {
    forAll { (n1: String, n2: String, v1: AnyVal, v2: AnyVal) =>
      val feeder1: Feeder[AnyVal] = Iterator.continually(Map(n1 -> v1))
      val feeder2: Feeder[AnyVal] = Iterator.continually(Map(n2 -> v2))
      val result: Feeder[Any]     = feeder1 ** feeder2
      assert(result.next().equals(Map(n1 -> v1, n2 -> v2)))
    }
  }

  it should "prepare feeder with finite size" in {
    forAll { (n: String, v: Char) =>
      val fdr    = RandomDigitFeeder(n)
      val result = fdr.toFiniteLength(v)

      assert(result.readRecords.size == v)
    }
  }

  it should "transform Collection to Feeder" in {
    forAll { (n: String, v: AnyVal) =>
      val collection = List.fill(100)(v)
      val result     = collection.toFeeder(n)

      assert(result.readRecords.forall(r => r.equals(Map(n -> v))))
    }
  }

}
