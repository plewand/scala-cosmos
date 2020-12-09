val dottyVersion = "3.0.0-M2"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-universe",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation"
    ),

    libraryDependencies ++= Seq(
      // Using withDottyCompat allows Scala 2 libraries to work with Scala 3.
      ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).withDottyCompat(scalaVersion.value),
      ("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion).withDottyCompat(scalaVersion.value),
      // ScalaTest has already 3.0 implementation
      "org.scalactic" %% "scalactic" % "3.2.3",
      "org.scalatest" %% "scalatest" % "3.2.3" % "test",
      "org.scalatest" %% "scalatest-flatspec" % "3.2.3" % "test"
    )
  )
