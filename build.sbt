ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val akkaVersion = "2.10.8"
lazy val akkaGroup = "com.typesafe.akka"

resolvers +=
  "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  akkaGroup %% "akka-actor-typed" % akkaVersion,
  akkaGroup %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "pcd-ass-03-part-01-sbt"
  )
