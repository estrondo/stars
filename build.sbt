
ThisBuild / organization := "com.estrondo"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / lagomCassandraEnabled := false

val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

val MacwireVersion = "2.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "stars-root"
  )
  .aggregate(`simulator`, `webapi`, `webapi-impl`)

lazy val `bhtree-engine` = (project in file("bhtree-engine"))
  .settings(
    name := "stars-burnes-hut-tree-engine"
  )
  .dependsOn(
    `simulation-context`, `simulation-engine`
  )

lazy val `simulation-engine` = (project in file("simulation-engine"))
  .settings(
    name := "stars-simulation-engine"
  )

lazy val `simulation-context` = (project in file("simulation-context"))
  .settings(
    name := "stars-simulation-context"
  )

lazy val `simulator` = (project in file("simulator"))
  .settings(
    name := "stars-simulator"
  )
  .dependsOn(`bhtree-engine`, `simulation-engine`, `simulation-context`)

lazy val `webapi` = (project in file("webapi"))
  .settings(
    name := "stars-webapi",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `webapi-impl` = (project in file("webapi-impl"))
  .settings(
    name := "stars-webapi-impl",
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % MacwireVersion % "provided",
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      "org.postgresql" % "postgresql" % "42.3.0",
      ScalaLogging
    )
  )
  .dependsOn(`webapi`)
  .enablePlugins(LagomScala)