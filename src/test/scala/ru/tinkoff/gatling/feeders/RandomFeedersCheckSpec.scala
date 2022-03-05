package ru.tinkoff.gatling.feeders
import org.scalacheck.Prop.forAll
import org.scalacheck._

object RandomFeedersCheckSpec extends Properties("RandomFeeders") {

  val rndString: Gen[String] = Gen.alphaNumStr

  property("randomOGRNFeeder") = forAll(for {
    str <- rndString
  } yield (RandomOGRNFeeder(str).take(50), str)) { case (rnd, str) =>
    rnd.forall(k => k(str).substring(0, 12).toLong % 12 % 10 == k(str).substring(12, 13).toInt)
  }

  property("randomNatITNFeeder") = forAll(for {
    str <- rndString
  } yield (RandomNatITNFeeder(str).take(50), str)) { case (rnd, str) =>
    rnd.forall(k => k(str).matches("\\d{10}"))
  }

  property("randomJurITNFeeder") = forAll(for {
    str <- rndString
  } yield (RandomJurITNFeeder(str).take(50), str)) { case (rnd, str) =>
    rnd.forall(k => k(str).matches("\\d{12}"))
  }

  property("randomKPPFeeder") = forAll(for {
    str <- rndString
  } yield (RandomKPPFeeder(str).take(50), str)) { case (rnd, str) =>
    rnd.forall(k => k(str).matches("\\d{9}"))
  }

}
