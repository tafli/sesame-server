name := """sesame-server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.tinkerforge" % "tinkerforge" % "2.1.9"
)

fork in run := true

scalafmtConfig in ThisBuild := Some(file(".scalafmt"))
