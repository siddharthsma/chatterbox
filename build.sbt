name := "chatterbox"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.7" % "test"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.3.1")
    