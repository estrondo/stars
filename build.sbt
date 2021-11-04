ThisBuild / organization := "com.estrondo"
ThisBuild / scalaVersion := "2.13.7" //PlayVersion.scalaVersion
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen"
)

ThisBuild / Test / fork := true

val AkkaVersion = "2.6.14" //PlayVersion.akkaVersion
val AkkaKafkaVersion = "2.1.0"
val JacksonVersion = "2.11.4"

val Flyway = "org.flywaydb" % "flyway-core" % "8.0.2"
val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.10" % "test"
val Postgres = "org.postgresql" % "postgresql" % "42.3.1"
//val SprayJSON = "io.spray" %% "spray-json" % "1.3.6"
val Macwire = "com.softwaremill.macwire" %% "macros" % "2.5.0" % "provided"
val ScalaMock = "org.scalamock" %% "scalamock" % "5.1.0" % Test

val Logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.6"
)

val TypesafeConfig = Seq(
  "com.iheart" %% "ficus" % "1.5.1"
)

val ScalaPB = Seq(
  "com.thesamet.scalapb" %% "compilerplugin" % "0.11.3"
)

val AkkaPersistence = Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)

val AkkaActors = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

val AkkaStreams = Seq(
  "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

val AkkaSharding = Seq(
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)

val Testcontainers = Seq(
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.10" % "test",
  "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.10" % "test"
)

val AlpakkaKafka = Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % AkkaKafkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion,
  //"com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.typesafe.akka" %% "akka-stream-kafka-testkit" % AkkaKafkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "org.testcontainers" % "kafka" % "1.15.3" % Test
)

val Chimney = Seq(
  "io.scalaland" %% "chimney" % "0.6.1"
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
    `simulation-engine`
  )

lazy val `simulation-engine` = (project in file("simulation-engine"))
  .settings(
    name := "stars-simulation-engine"
  )
  .dependsOn(`simulation-protocol`)

lazy val `simulation-protocol` = (project in file("simulation-protocol"))
  .settings(
    name := "stars-simulation-protocol",
    Compile / PB.targets := Seq(
      scalapb.gen(
        flatPackage = true,
        lenses = false,
        grpc = false
      ) -> (Compile / sourceManaged).value / "scalapb"
    ),
    libraryDependencies ++= Seq(
    ) ++ Seq(
      Chimney
    ).flatten
  )

lazy val `simulator` = (project in file("simulator"))
  .settings(
    name := "stars-simulator",
    libraryDependencies ++= Seq(
      ScalaTest,
      ScalaMock
    ) ++ Set(
      AkkaActors,
      AkkaStreams,
      AkkaSharding,
      Logging,
      AlpakkaKafka,
      TypesafeConfig
    ).flatten
  )
  .dependsOn(`bhtree-engine`, `simulation-engine`, `simulation-protocol`)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerBaseImage := "openjdk:11-jdk-slim-buster"
  )
  .settings()

lazy val `webapi` = (project in file("webapi"))
  .settings(
    name := "stars-webapi",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`simulation-protocol`)

lazy val `webapi-impl` = (project in file("webapi-impl"))
  .settings(
    name := "stars-webapi-impl",
    libraryDependencies ++= Seq(
      Macwire,
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      lagomScaladslAkkaDiscovery,
      Postgres,
      Flyway,
      ScalaTest
    ) ++ Set(
      AkkaPersistence,
      AlpakkaKafka,
      Logging,
      Testcontainers,
      Chimney,
      TypesafeConfig
    ).flatten
  )
  .dependsOn(`webapi`, `simulation-protocol`)
  .enablePlugins(LagomScala)
  .settings(
    lagomCassandraEnabled := false
  )
  .settings(lagomForkedTestSettings: _*)