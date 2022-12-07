import Dependencies._

def UtilsModule(id: String) = Project(id, file(id))

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    name          := "gatling-picatinny",
    scalaVersion  := "2.13.10",
    libraryDependencies ++= gatlingCore,
    libraryDependencies ++= gatling,
    libraryDependencies ++= fastUUID,
    libraryDependencies ++= json4s,
    libraryDependencies ++= pureConfig,
    libraryDependencies ++= jackson,
    libraryDependencies ++= scalaTesting,
    libraryDependencies ++= generex,
    libraryDependencies ++= jwt,
    libraryDependencies ++= influxClientScala,
    libraryDependencies ++= circeDeps,
    libraryDependencies ++= junit,
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

lazy val example = (project in file("example"))
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "gatling-picatinny-example",
    libraryDependencies ++= gatling,
  )
  .dependsOn(root)
