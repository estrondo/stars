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

val Flyway = Seq(
  "org.flywaydb" % "flyway-core" % "8.0.2"
)
val ScalaTest = Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

val Postgres = Seq(
  "org.postgresql" % "postgresql" % "42.3.1"
)

val Macwire = Seq(
  "com.softwaremill.macwire" %% "macros" % "2.5.0" % Provided
)

val ScalaMock = Seq(
  "org.scalamock" %% "scalamock" % "5.1.0" % Test
)

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
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
)

val AkkaStreams = Seq(
  "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

val AkkaManagement = Seq(
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.1.1",
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion
)

val AkkaSharding = Seq(
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)

val ScalaTestcontainers = Seq(
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.12" % Test,
  "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.12" % Test,
  "com.dimafeng" %% "testcontainers-scala-kafka" % "0.39.12" % Test
)

val AlpakkaKafka = Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % AkkaKafkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion,
  //"com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.typesafe.akka" %% "akka-stream-kafka-testkit" % AkkaKafkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

val Chimney = Seq(
  "io.scalaland" %% "chimney" % "0.6.1"
)

lazy val root = (project in file("."))
  .settings(
    name := "stars-root"
  ).aggregate(`simulator-impl`, `webapi-spec`, `webapi-impl`, `test-integration`)

lazy val `test-kit` = (project in file("test-kit"))
  .settings(
    name := "test-kit",
    libraryDependencies ++= dependencies(
      ScalaTestcontainers,
      ScalaTest
    ).map(removeConfiguration)
  )
  .dependsOn(`simulator-protocol`, `webapi-spec`)

lazy val `test-integration` = (project in file("test-integration"))
  .settings(
    name := "test-integration",
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      "com.softwaremill.sttp.client3" %% "core" % "3.3.16" % Test,
      "com.softwaremill.sttp.client3" %% "play-json" % "3.3.16" % Test
    ) ++ dependencies(
      ScalaTest,
      ScalaTestcontainers,
      AlpakkaKafka,
      Logging
    )
  )
  .dependsOn(`test-kit` % Test, `simulator-protocol`, `webapi-spec`)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq(version),
    buildInfoPackage := "stars"
  )

lazy val `simulator-engine-bhtree` = (project in file("simulator-engine-bhtree"))
  .settings(
    name := "stars-simulator-engine-bhtree"
  )
  .dependsOn(
    `simulator-engine-spec`
  )

lazy val `simulator-engine-spec` = (project in file("simulator-engine-spec"))
  .settings(
    name := "stars-simulator-engine-spec"
  )
  .dependsOn(`simulator-protocol`)

lazy val `simulator-protocol` = (project in file("simulator-protocol"))
  .settings(
    name := "stars-simulator-protocol",
    Compile / PB.targets := Seq(
      scalapb.gen(
        flatPackage = true,
        lenses = false,
        grpc = false
      ) -> (Compile / sourceManaged).value / "scalapb"
    ),
    libraryDependencies ++= dependencies(
      Chimney
    )
  )

lazy val `simulator-protocol-testkit` = (project in file("simulator-protocol-testkit"))
  .settings(
    name := "stars-simulator-protocol-testkit"
  )

lazy val `simulator-impl` = (project in file("simulator-impl"))
  .settings(
    name := "stars-simulator-impl",
    libraryDependencies ++= dependencies(
      ScalaTest,
      ScalaMock,
      AkkaActors,
      AkkaStreams,
      AkkaSharding,
      AkkaManagement,
      Logging,
      AlpakkaKafka,
      TypesafeConfig,
      Chimney
    )
  )
  .dependsOn(`simulator-engine-bhtree`, `simulator-engine-spec`, `simulator-protocol`, `test-kit` % Test)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerBaseImage := "openjdk:11-jdk-slim-buster",
    dockerEnvVars ++= SimulatorInfo.EnvVars
  )
  .settings()

lazy val `webapi-spec` = (project in file("webapi-spec"))
  .settings(
    name := "stars-webapi-spec",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`simulator-protocol`)

lazy val `webapi-impl` = (project in file("webapi-impl"))
  .settings(
    name := "stars-webapi-impl",
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      lagomScaladslAkkaDiscovery
    ) ++ dependencies(
      Macwire,
      Postgres,
      Flyway,
      ScalaTest,
      AkkaPersistence,
      AlpakkaKafka,
      Logging,
      ScalaTestcontainers,
      Chimney,
      TypesafeConfig
    )
  )
  .dependsOn(`webapi-spec`, `simulator-protocol`, `test-kit` % Test)
  .enablePlugins(LagomScala)
  .settings(
    lagomCassandraEnabled := false,
    dockerBaseImage := "openjdk:11-jdk-slim-buster",
    Docker / packageName := "stars-webapi",
    dockerExposedPorts := Seq(9000),
    dockerExposedVolumes := Seq(
      "/opt/docker"
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .settings(
    Test / javaOptions ++= Seq("--illegal-access=warn")
  )

def dependencies(modules: Seq[ModuleID]*): Seq[ModuleID] = {
  modules.flatten
}

def removeConfiguration(module: ModuleID): ModuleID = {
  module.withConfigurations(None)
}