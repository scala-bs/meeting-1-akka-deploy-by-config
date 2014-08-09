name := """Akka Meeting #1"""

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.4",
  "com.typesafe.akka" % "akka-remote_2.11" % "2.3.4",
  "com.typesafe.akka" % "akka-cluster_2.11" % "2.3.4"
)