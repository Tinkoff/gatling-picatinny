package ru.tinkoff.load.example.scenarios

import com.redis.RedisClientPool
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import ru.tinkoff.gatling.config.SimulationConfig._
import ru.tinkoff.gatling.profile.http.HttpRequestConfig
import ru.tinkoff.gatling.redis.RedisActionBuilder._
import ru.tinkoff.gatling.utils.jwt._
import ru.tinkoff.load.example.feeders.Feeders._
import ru.tinkoff.load.stargazer.infrastructure.{FullRequest, ProfileYamlParser}

import scala.collection.mutable.ListBuffer

object ProfileScenario {
  def apply(): ScenarioBuilder = new ProfileScenario().profileScenario
}

class ProfileScenario {
  val profileConfigName                  = "profile.conf"
  val profileRequests: List[FullRequest] = zio.Runtime.default.unsafeRun(ProfileYamlParser(profileConfigName)) // ZIO?
  val amount: Int = profileRequests.length

  val methods: List[String]  = profileRequests.map(_.method.get)  // Метод http запроса
  val requests: List[String] = profileRequests.map(_.request.get) // Сам URI

  val intensityValueRegex    = """$\d{1,10}""".r // нужно вытащить значение тк intensity указывается как "1234 rph"
  val intensities: List[Int] = profileRequests.map(x => intensityValueRegex.findFirstIn(x.intensity.get).get.toInt)
  val intensitiesSum: Double = intensities.sum

  val probabilities: List[Double] = intensities.map(x => x / intensitiesSum) // Вероятность запроса
  val names: List[String]         = profileRequests.map(_.name.get)          // Имя запроса
  val headers: List[List[String]] = profileRequests.map(_.headers.get)       // все хэдеры для всех запросов
  val bodies: List[String]        = profileRequests.map(_.body.get)          // все тела запросов

  val configs: Seq[HttpRequestConfig] = Seq()
  for (i <- 0 to amount) {
    configs :+ HttpRequestConfig(names(i), probabilities(i), methods(i), requests(i), bodies(i))
  }

  // compose all in scenario
  val profileScenario: ScenarioBuilder = scenario("Profile scenario")
    .randomSwitch(configs.map(requestConfig => requestConfig.toTuple): _*)

}
