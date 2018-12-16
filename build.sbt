ThisBuild / organization := "io.github.yangzai"

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / crossScalaVersions += "2.11.12"

name := "spark-typeclass"

val sparkVersion = "2.4.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.typelevel" %% "cats-core" % "1.5.0",
  "com.chuusai" %% "shapeless" % "2.3.3" //frameless uses 2.3.2 at the moment
)

ThisBuild / wartremoverErrors ++= Warts allBut Wart.ImplicitParameter

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

import ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
