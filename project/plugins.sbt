addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.6.5")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.6")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.2")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")


libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.1"