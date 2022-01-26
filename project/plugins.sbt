addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.6")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.3")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("org.openapitools" % "sbt-openapi-generator" % "5.0.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.7"
