import sbt._

object Dependencies {
  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core",
    "io.gatling" % "gatling-http"
  ).map(_ % "3.4.1" % Provided)

  lazy val fastUUID = Seq(
    "com.eatthepath" % "fast-uuid" % "0.1" % Provided
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling"            % "gatling-test-framework"
  ).map(_ % "3.4.1" % Test)

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

  lazy val influxClientScala: Seq[ModuleID] = Seq(
    "io.razem" %% "scala-influxdb-client" % "0.6.3"
  )

  lazy val logbackClassic: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val ScalaLogging: Seq[ModuleID] = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )
}
