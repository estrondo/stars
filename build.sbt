import play.core.PlayVersion

ThisBuild / organization := "com.estrondo"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / Test / fork := true

val AkkaVersion = PlayVersion.akkaVersion
val MacwireVersion = "2.5.0"

val Flyway = "org.flywaydb" % "flyway-core" % "8.0.2"
val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.10" % "test"
val Postgres = "org.postgresql" % "postgresql" % "42.3.0"

val Logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-core" % "1.2.6"
)

val AkkaPersistence = Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)

val AkkaActors = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

val Testcontainers = Seq(
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.10" % "test",
  "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.10" % "test"
)


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
    name := "stars-simulator",
    libraryDependencies ++= Seq(
      ScalaTest
    ) ++ Set(
      AkkaActors,
      Logging
    ).flatten
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
    lagomCassandraEnabled := false,
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % MacwireVersion % "provided",
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      Postgres,
      Flyway,
      ScalaTest
    ) ++ Set(
      AkkaPersistence,
      Logging,
      Testcontainers
    ).flatten
  )
  .dependsOn(`webapi`)
  .enablePlugins(LagomScala)
  .settings(lagomForkedTestSettings: _*)