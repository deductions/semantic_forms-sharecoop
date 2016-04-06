organization := "deductions"

name := "social_web"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.3.9"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % Test


javacOptions ++= Seq("-source","1.7", "-target","1.7")

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)


