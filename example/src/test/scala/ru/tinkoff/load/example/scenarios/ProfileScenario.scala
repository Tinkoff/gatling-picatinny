package ru.tinkoff.load.example.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.profile.http.HttpRequestConfig
import ru.tinkoff.load.stargazer.infrastructure.{FullRequest, ProfileYamlParser}

object ProfileScenario {
  def apply(): ScenarioBuilder = new ProfileScenario().profileScenario
}

class ProfileScenario {
  val profileConfigName                  = "profile.conf"
  val profileRequests: List[FullRequest] = zio.Runtime.default.unsafeRun(ProfileYamlParser(profileConfigName)) // ZIO?
  val amount: Int                        = profileRequests.length

  val methods: List[String]  = profileRequests.map(_.method.get)  // Метод http запроса
  val requests: List[String] = profileRequests.map(_.request.get) // Сам URI

  val intensityValueRegex    = """\d+""".r // нужно вытащить значение тк intensity указывается как "1234 rph"
  val intensities: List[Int] = profileRequests.map(x => intensityValueRegex.findFirstIn(x.intensity.get).get.toInt)
  val intensitiesSum: Double = intensities.sum

  val probabilities: List[Double] = intensities.map(x => x / intensitiesSum) // Вероятность запроса
  val names: List[String]         = profileRequests.map(_.name.get)          // Имя запроса
  val headers: List[List[String]] = profileRequests.map(_.headers.get)       // все хэдеры для всех запросов
  val bodies: List[String]        = profileRequests.map(_.body.get)          // все тела запросов

  val configs: Seq[HttpRequestConfig] = Seq()
  for (i <- 0 to amount) {
    configs :+ HttpRequestConfig(names(i), probabilities(i), methods(i), requests(i), Option(bodies(i)))
  }

  // compose all in scenario
  val profileScenario: ScenarioBuilder = scenario("Profile scenario")
    .randomSwitch(configs.map(requestConfig => requestConfig.toTuple): _*)

}
