ThisBuild / organization := "io.estrondo"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xlint:unused"
)

ThisBuild / Test / fork := true

val AkkaVersion = "2.6.18"
val AkkaKafkaVersion = "2.1.1"
val JacksonVersion = "2.11.4"
val AkkaHttpVersion = "10.2.7"

val Flyway = Seq(
  "org.flywaydb" % "flyway-core" % "8.3.0"
)
val ScalaTest = Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

val Postgres = Seq(
  "org.postgresql" % "postgresql" % "42.3.1"
)

val Macwire = Seq(
  "com.softwaremill.macwire" %% "macros" % "2.5.2" % Provided
)

val ScalaMock = Seq(
  "org.scalamock" %% "scalamock" % "5.2.0" % Test
)

val Logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.10"
)

val TypesafeConfig = Seq(
  "com.iheart" %% "ficus" % "1.5.1"
)

val ScalaPB = Seq(
  "com.thesamet.scalapb" %% "compilerplugin" % "0.11.7"
)

val AkkaPersistence = Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)

val AkkaActors = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "com.typesafe.akka" %% "akka-remote" % AkkaVersion
)

val AkkaStreams = Seq(
  "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

val AkkaHttp = Seq(
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
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
  // "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.typesafe.akka" %% "akka-stream-kafka-testkit" % AkkaKafkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

val Chimney = Seq(
  "io.scalaland" %% "chimney" % "0.6.1"
)

val Octopus = Seq(
  "com.github.krzemin" %% "octopus" % "0.4.1"
)

lazy val root = (project in file("."))
  .settings(
    name := "stars-root"
  )
  .aggregate(
    `akka-helper`,
    `simulator-impl`,
    `simulator-protocol`,
    `simulator-engine-spec`,
    `simulator-engine-bhtree`,
    `simulator-protocol-testkit`,
    `webapi`,
    `webapi-core`,
    `webapi-simulator`,
    `util`
  )

lazy val `simulator-engine-bhtree` =
  (project in file("simulator-engine-bhtree"))
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

lazy val `simulator-protocol-testkit` =
  (project in file("simulator-protocol-testkit"))
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
  .dependsOn(
    `simulator-engine-bhtree`,
    `simulator-engine-spec`,
    `simulator-protocol`
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerBaseImage := "openjdk:11-jdk-slim-buster"
  )
  .settings()

lazy val `webapi-simulator` = (project in file("webapi-simulator"))
  .settings(
    name := "stars-webapi-simulator",
    libraryDependencies ++= dependencies(
      AkkaSharding,
      AlpakkaKafka,
      ScalaTest,
      Chimney,
      ScalaTestcontainers
    )
  )
  .dependsOn(`webapi-core`, `simulator-protocol`, `webapi-core-test` % Test)

lazy val `webapi-core` = (project in file("webapi-core"))
  .settings(
    name := "stars-webapi-core",
    libraryDependencies ++= dependencies(
      AkkaSharding,
      ScalaTest,
      Octopus,
      ScalaMock
    )
  )
  .dependsOn(`akka-helper`, `util`)

lazy val `webapi-core-test` = (project in file("webapi-core-test"))
  .settings(
    name := "stars-webapi-core-test",
    libraryDependencies ++= dependencies(
      ScalaTest,
      ScalaMock
    )
  )
  .dependsOn(`webapi-core`)

lazy val `webapi` = (project in file("webapi"))
  .settings(
    name := "stars-webapi",
    libraryDependencies ++= Seq(
      "io.estrondo" % "stars-webapi-client" % version.value % Test
    ) ++ dependencies(
      AkkaHttp,
      AkkaActors,
      AkkaStreams,
      AlpakkaKafka,
      Macwire,
      Chimney,
      ScalaTest,
      ScalaTestcontainers
    ),
    openApiInputSpec := "webapi/src/main/resources/openapi.yml",
    openApiConfigFile := "webapi/src/main/resources/openapi-config.yml",
    resolvers ++= Seq(
      Resolver.mavenLocal
    )
  )
  .dependsOn(`webapi-core`, `webapi-simulator`, `akka-helper`, `util`)

lazy val `util` = (project in file("util"))
  .settings(
    name := "stars-util"
  )

lazy val `akka-helper` = (project in file("akka-helper"))
  .settings(
    name := "stars-akka-helper",
    libraryDependencies ++= dependencies(
      AkkaActors,
      TypesafeConfig,
      AkkaSharding,
      Logging
    )
  )

def dependencies(modules: Seq[ModuleID]*): Seq[ModuleID] = {
  modules.flatten
}

def removeConfiguration(module: ModuleID): ModuleID = {
  module.withConfigurations(None)
}
