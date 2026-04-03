name := "seq-vs-parallel"

version := "0.1"

scalaVersion := "3.6.4"

// -Ypartial-unification is the default in Scala 3 — removed
// scalaz is not published for Scala 3; replaced with cats equivalents
assembly / mainClass := Some("BlockingTasksWithGlobalExecutionContext")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.5.7",
  "org.typelevel" %% "cats-core"   % "2.12.0"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
