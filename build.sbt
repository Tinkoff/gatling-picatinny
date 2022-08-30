import Dependencies._

def UtilsModule(id: String) = Project(id, file(id))

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    name          := "gatling-picatinny",
    scalaVersion  := "2.13.8",
    libraryDependencies ++= gatlingCore,
    libraryDependencies ++= fastUUID,
    libraryDependencies ++= json4s,
    libraryDependencies ++= pureConfig,
    libraryDependencies ++= scalaTesting,
    libraryDependencies ++= generex,
    libraryDependencies ++= jwt,
    libraryDependencies ++= influxClientScala,
//    libraryDependencies ++= circeDeps,
    libraryDependencies ++= Circe.all,
    libraryDependencies ++= ZIO.all,
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
