import sbt._

object Dependencies {
  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core",
    "io.gatling" % "gatling-core-java",
    "io.gatling" % "gatling-http",
    "io.gatling" % "gatling-http-java",
    "io.gatling" % "gatling-redis",
    "io.gatling" % "gatling-core-java",
    "io.gatling" % "gatling-redis-java",
  ).map(_ % "3.9.5" % Provided)

  lazy val fastUUID = Seq(
    "com.eatthepath" % "fast-uuid" % "0.2.0" % Provided,
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling"            % "gatling-test-framework",
  ).map(_ % "3.9.5" % Test)

  lazy val json4s: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native"  % "4.1.0-M3",
    "org.json4s" %% "json4s-jackson" % "4.1.0-M3",
  )

  lazy val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig"      % "0.17.4",
    "com.github.pureconfig" %% "pureconfig-yaml" % "0.17.4",
  )

  lazy val jackson: Seq[ModuleID] = Seq(
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.15.1",
    "com.fasterxml.jackson.core"       % "jackson-core"            % "2.15.1",
  )

  lazy val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.15" % "test",
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
    "com.github.jwt-scala" %% "jwt-core" % "9.2.0",
  )

  lazy val circeDeps: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"    % "0.15.0-M1",
    "io.circe" %% "circe-generic" % "0.15.0-M1",
    "io.circe" %% "circe-parser"  % "0.15.0-M1",
    "io.circe" %% "circe-yaml"    % "0.15.0-RC1",
  )

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

  lazy val junit: Seq[ModuleID] = Seq("org.junit.jupiter" % "junit-jupiter-engine" % "5.9.3" % Test)

}
