package ru.tinkoff.gatling.config

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.gatling.javaapi.SimulationConfig._

import java.time.Duration

class JavaConfigTest extends AnyFlatSpec with Matchers {
  it should "get baseUrl from simulation.conf" in {
    baseUrl() shouldBe "http://testbaseurl.org/"
  }

  it should "get baseAuthUrl from simulation.conf" in {
    baseAuthUrl() shouldBe "http://testbaseauthurl.org/"
  }

  it should "get wsBaseUrl from simulation.conf" in {
    wsBaseUrl() shouldBe "http://testwsbaseurl.org/"
  }

  it should "get string param from simulation.conf" in {
    getStringParam("stringParam") shouldBe "testString"
  }

  it should "get int param from simulation.conf" in {
    getIntParam("intParam") shouldBe 10
  }

  it should "get double param from simulation.conf" in {
    getDoubleParam("doubleParam") shouldBe 10.1
  }

  it should "get boolean param from simulation.conf" in {
    getBooleanParam("booleanParam") shouldBe true
  }

  it should "get duration param from simulation.conf" in {
    getDurationParam("durationParam") shouldBe Duration.ofSeconds(10)
  }

  it should "get stages number param from simulation.conf" in {
    stagesNumber() shouldBe 5
  }

  it should "get ramp duration param from simulation.conf" in {
    rampDuration() shouldBe Duration.ofMinutes(10)
  }

  it should "get stage duration param from simulation.conf" in {
    stageDuration() shouldBe Duration.ofMinutes(30)
  }

  it should "get test duration param from simulation.conf" in {
    testDuration() shouldBe Duration.ofMinutes((10 + 30) * 5)
  }

  it should "get intensity param from simulation.conf" in {
    intensity() shouldBe 1.0
  }

}
