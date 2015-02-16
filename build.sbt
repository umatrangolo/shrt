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
  "com.google.guava" % "guava" % "18.0",
  "org.scaldi" %% "scaldi" % "0.5.3",
  "org.scaldi" %% "scaldi-play" % "0.5.3",
  "org.apache.lucene" % "lucene-parent" % "4.10.1"
)

instrumentSettings

ScoverageKeys.minimumCoverage := 70

ScoverageKeys.failOnMinimumCoverage := false

ScoverageKeys.highlighting := { if (scalaBinaryVersion.value == "2.10") false else false }

logLevel in test := Level.Warn
