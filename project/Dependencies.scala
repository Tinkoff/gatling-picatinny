import sbt._

object Dependencies {
  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"           % "3.4.1",
    "io.gatling" % "gatling-test-framework" % "3.4.1"
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.4.1"
  )

  lazy val json4s: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native"  % "3.6.10",
    "org.json4s" %% "json4s-jackson" % "3.6.10"
  )

  lazy val requests: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "requests" % "0.2.0"
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig"      % "0.14.0",
    "com.github.pureconfig" %% "pureconfig-yaml" % "0.14.0"
  )

  lazy val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.0" % "test"
  )

  lazy val scalaCheck: Seq[ModuleID] = Seq(
    "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"
  )

  lazy val scalaMock: Seq[ModuleID] = Seq(
    "org.scalamock" %% "scalamock" % "5.0.0" % "test"
  )

  lazy val generex: Seq[ModuleID] = Seq(
    "com.github.mifmif" % "generex" % "1.0.2"
  )

  lazy val jwt: Seq[ModuleID] = Seq(
    "com.pauldijou" %% "jwt-core" % "4.2.0"
  )

  lazy val scalaTesting: Seq[ModuleID] = scalaCheck ++ scalaTest ++ scalaMock

}
