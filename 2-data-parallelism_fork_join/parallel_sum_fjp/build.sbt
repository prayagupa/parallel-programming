name := "parallel-sum-fjp"

version := "1.0"

scalaVersion := "3.6.4"

// scala-library is bundled in Scala 3 — removed explicit dep
libraryDependencies += "org.scalatest"      %% "scalatest"    % "3.2.19" % Test
libraryDependencies += "org.jetbrains.kotlin" % "kotlin-stdlib" % "2.1.20"

resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"
resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases"
