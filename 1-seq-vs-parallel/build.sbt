name := "seq-vs-parallel"

version := "0.1"

scalaVersion := "2.12.7"

mainClass in assembly := Some("BlockingTasksWithGlobalExecutionContext")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.26"
