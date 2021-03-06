
ThisBuild / version := "0.1"
ThisBuild / organization := "io.github.oybek"

lazy val adjutant = (project in file("."))
  .settings(name := "adjutant")
  .settings(libraryDependencies ++= Dependencies.common)
  .settings(sonarProperties := Sonar.properties)
  .settings(Compiler.settings)

assemblyMergeStrategy in assembly := {
  case PathList("org", "slf4j", _ @ _*) => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}

testFrameworks += new TestFramework("munit.Framework")
Test / parallelExecution := false
