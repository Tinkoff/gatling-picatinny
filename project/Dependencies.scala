import sbt._

object Dependencies {
  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"           % "3.3.1",
    "io.gatling" % "gatling-test-framework" % "3.3.1"
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.3.1"
  )

  lazy val json4s: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native"  % "3.6.7",
    "org.json4s" %% "json4s-jackson" % "3.6.7"
  )

  lazy val requests: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "requests" % "0.2.0"
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.12.2"
  )

  lazy val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.1.0" % "test"
  )

  lazy val scalaCheck: Seq[ModuleID] = Seq(
    "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"
  )

  lazy val scalaMock: Seq[ModuleID] = Seq(
    "org.scalamock" %% "scalamock" % "4.4.0" % "test"
  )

  lazy val generex: Seq[ModuleID] = Seq(
    "com.github.mifmif" % "generex" % "1.0.2"
  )

  lazy val scalaTesting: Seq[ModuleID] = scalaCheck ++ scalaTest ++ scalaMock

}
