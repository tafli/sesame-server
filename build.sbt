lazy val root = (project in file("."))
  .enablePlugins(PlayService)
    .enablePlugins(RoutesCompiler)
.enablePlugins(BuildInfoPlugin)
  .settings(
    scalaVersion := "2.12.4",

    name := """SesamServer""",
    organization := "tafli.io",

    libraryDependencies ++= Seq(
      guice,
      akkaHttpServer,
      logback,
      "com.tinkerforge" % "tinkerforge" % "2.1.16",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
    ),

    buildInfoKeys := BuildInfoKey.ofN(name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "utils"
  )