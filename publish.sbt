ThisBuild / organization := "ru.tinkoff"
ThisBuild / scalaVersion := "2.13.6"

ThisBuild / publishMavenStyle := true

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TinkoffCreditSystems/gatling-picatinny.git"),
    "git@https://github.com/TinkoffCreditSystems/gatling-picatinny.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chepk",
    name = "Sergey Chepkasov",
    email = "s.chepkasov@tinkoff.ru",
    url = url("https://github.com/chepk")
  ),
  Developer(
    id = "MaksSieve",
    name = "Maksim Sitnikov",
    email = "m.sintikov@tinkoff.ru",
    url = url("https://github.com/MaksSieve")
  ),
  Developer(
    id = "jigarkhwar",
    name = "Ioann Akhaltsev",
    email = "i.akhaltsev@tinkoff.ru",
    url = url("https://github.com/jigarkhwar")
  ),
  Developer(
    id = "red-bashmak",
    name = "Vyacheslav Kalyokin",
    email = "v.kalyokin@tinkoff.ru",
    url = url("https://github.com/red-bashmak")
  )
)

ThisBuild / description := "Gatling Utils"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/TinkoffCreditSystems/gatling-picatinny.git"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ =>
  false
}
