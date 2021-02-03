import Dependencies._

enablePlugins(GatlingPlugin, GitVersioning)

def UtilsModule(id: String) = Project(id, file(id))

lazy val root = (project in file("."))
  .settings(
    name := "gatling-picatinny",
    libraryDependencies ++= gatlingCore,
    libraryDependencies ++= fastUUID,
    libraryDependencies ++= json4s,
    libraryDependencies ++= requests,
    libraryDependencies ++= pureConfig,
    libraryDependencies ++= scalaTesting,
    libraryDependencies ++= generex,
    libraryDependencies ++= jwt,
    libraryDependencies ++= influxClientScala,
    libraryDependencies ++= logbackClassic,
    libraryDependencies ++= ScalaLogging,
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )

lazy val example = (project in file("example"))
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "gatling-picatinny-example",
    libraryDependencies ++= gatling
  )
  .dependsOn(root)
