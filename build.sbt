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
  "org.apache.commons" % "commons-lang3" % "3.3.2"
)
