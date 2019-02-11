lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    scalaVersion := "2.12.8",

    name := """SesamServer""",
    organization := "tafli.io",

    libraryDependencies ++= Seq(
      guice,
      "com.tinkerforge" % "tinkerforge" % "2.1.22",
      "org.apache.commons" % "commons-lang3" % "3.8.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
    ),

    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber),
    buildInfoPackage := "utils"
  )
PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value