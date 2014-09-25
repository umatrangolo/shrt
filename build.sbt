name := """shrt"""

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.181",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "org.scaldi" %% "scaldi" % "0.4",
  "org.scaldi" %% "scaldi-play" % "0.4.1"
)

instrumentSettings

ScoverageKeys.minimumCoverage := 70

ScoverageKeys.failOnMinimumCoverage := false

ScoverageKeys.highlighting := {
  if (scalaBinaryVersion.value == "2.10") false
  else false
}

logLevel in test := Level.Warn
