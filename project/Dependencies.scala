import sbt._

object Dependencies {
  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core",
    "io.gatling" % "gatling-core-java",
    "io.gatling" % "gatling-http",
    "io.gatling" % "gatling-redis",
  ).map(_ % "3.8.4" % Provided)

  lazy val fastUUID = Seq(
    "com.eatthepath" % "fast-uuid" % "0.2.0" % Provided,
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling"            % "gatling-test-framework",
  ).map(_ % "3.8.4" % Test)

  lazy val json4s: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native"  % "4.0.6",
    "org.json4s" %% "json4s-jackson" % "4.0.6",
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig"      % "0.17.1",
    "com.github.pureconfig" %% "pureconfig-yaml" % "0.17.1",
  )

  lazy val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  )

  lazy val scalaCheck: Seq[ModuleID] = Seq(
    "org.scalacheck" %% "scalacheck" % "1.17.0" % "test",
  )

  lazy val scalaTestPlus: Seq[ModuleID] = Seq(
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test,
  )

  lazy val scalaMock: Seq[ModuleID] = Seq(
    "org.scalamock" %% "scalamock" % "5.2.0" % "test",
  )

  lazy val generex: Seq[ModuleID] = Seq(
    "com.github.mifmif" % "generex" % "1.0.2",
  )

  lazy val jwt: Seq[ModuleID] = Seq(
    "com.github.jwt-scala" %% "jwt-core" % "9.1.1",
  )

  lazy val circeDeps: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-yaml",
  ).map(_ % "0.14.1")

  lazy val scalaTesting: Seq[ModuleID] = scalaCheck ++ scalaTest ++ scalaMock ++ scalaTestPlus

  // Add excludeAll netty to solve conflict run GatlinRunner with using Gatling 3.6.1 and io.netty:4.1.42.Final. Problem java.lang.NoSuchFieldError: DNT
  lazy val influxClientScala: Seq[ModuleID] = Seq(
    "io.razem" %% "scala-influxdb-client" % "0.6.3" excludeAll (
      ExclusionRule("io.netty", "netty-codec-http"),
      ExclusionRule("io.netty", "netty-buffer"),
      ExclusionRule("io.netty", "netty-codec-dns"),
      ExclusionRule("io.netty", "netty-codec-socks"),
      ExclusionRule("io.netty", "netty-codec"),
      ExclusionRule("io.netty", "netty-common"),
      ExclusionRule("io.netty", "netty-handler-proxy"),
      ExclusionRule("io.netty", "netty-handler"),
      ExclusionRule("io.netty", "netty-resolver-dns"),
      ExclusionRule("io.netty", "netty-resolver"),
      ExclusionRule("io.netty", "netty-transport")
    ),
  )

  lazy val picatinnyDependencies: Seq[sbt.ModuleID] =
    gatlingCore ++ fastUUID ++ json4s ++ pureConfig ++ scalaTesting ++ generex ++ jwt ++ influxClientScala ++ circeDeps

  lazy val picatinnyJavaDependencies: Seq[sbt.ModuleID] = gatling ++ gatlingCore
}
