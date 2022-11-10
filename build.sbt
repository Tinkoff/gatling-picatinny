import Dependencies._

def UtilsModule(id: String) = Project(id, file(id))

Global / scalaVersion := "2.13.8"

lazy val root = Project("gatling-picatinny", file("."))
  .enablePlugins(GitVersioning)
  .aggregate(
    core,
    coreJava,
    example,
  )

lazy val core = UtilsModule("picatinny-core")
  .settings(
    libraryDependencies ++= picatinnyDependencies,
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps",
    ),
  )

lazy val coreJava = UtilsModule("picatinny-java")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= picatinnyJavaDependencies)

lazy val example = UtilsModule("example")
  .settings(
    libraryDependencies ++= gatling,
  )
  .dependsOn(core)
