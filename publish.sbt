ThisBuild / organization := "ru.tinkoff"
ThisBuild / scalaVersion := "2.12.10"

ThisBuild / publishMavenStyle := true

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TinkoffCreditSystems/gatling-picatinny.git"),
    "git@https://github.com/TinkoffCreditSystems/gatling-picatinny.git"
  )
)

ThisBuild / publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

ThisBuild / developers := List(
  Developer(
    id    = "chepk",
    name  = "Sergey Chepkasov",
    email = "s.chepkasov@tinkoff.ru",
    url   = url("https://github.com/chepk")
  ),
  Developer(
    id    = "MaksSieve",
    name  = "Maksim Sitnikov",
    email = "m.sintikov@tinkoff.ru",
    url   = url("https://github.com/MaksSieve")
  ),
  Developer(
    id    = "jigarkhwar",
    name  = "Ioann Akhaltsev",
    email = "i.akhaltsev@tinkoff.ru",
    url   = url("https://github.com/jigarkhwar")
  ),
  Developer(
    id    = "red-bashmak",
    name  = "Vyacheslav Kalyokin",
    email = "v.kalyokin@tinkoff.ru",
    url   = url("https://github.com/red-bashmak")
  )
)

ThisBuild / description := "Gatling Utils"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/TinkoffCreditSystems/gatling-picatinny.git"))

ThisBuild / pomIncludeRepository := { _ => false }

val NEXUS_USER = sys.env.getOrElse("NEXUS_USER", "")
val NEXUS_PASS = sys.env.getOrElse("NEXUS_PASS", "")

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", NEXUS_USER, NEXUS_PASS)
credentials += Credentials("GnuPG Key ID", "gpg", "gatling-picatinny", "ignored")
