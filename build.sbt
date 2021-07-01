val dottyVersion = "3.0.0"
val AkkaVersion = "2.6.14"
val AkkaHttpVersion = "10.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-cosmos",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-Yexplicit-nulls"
    ),

    libraryDependencies ++= Seq(
      // Using Scala 2 libraries to work with Scala 3.
      ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion).cross(CrossVersion.for3Use2_13),
      // ScalaTest has already 3.0 implementation
      "org.scalactic" %% "scalactic" % "3.2.9",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
      "org.scalatest" %% "scalatest-flatspec" % "3.2.9" % "test"
    )
  )
