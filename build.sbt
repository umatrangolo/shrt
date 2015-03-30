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
  "org.apache.lucene" % "lucene-core" % "5.0.0",
  "org.apache.lucene" % "lucene-queryparser" % "5.0.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "5.0.0",
  "org.apache.lucene" % "lucene-suggest" % "5.0.0"
)

instrumentSettings

ScoverageKeys.minimumCoverage := 70

ScoverageKeys.failOnMinimumCoverage := false

ScoverageKeys.highlighting := { if (scalaBinaryVersion.value == "2.10") false else false }

logLevel in test := Level.Warn
