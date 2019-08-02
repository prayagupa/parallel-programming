name := "seq-vs-parallel"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

mainClass in assembly := Some("BlockingTasksWithGlobalExecutionContext")

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.26"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "1.0.0",
  "org.typelevel" %% "cats-core" % "1.4.0"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
