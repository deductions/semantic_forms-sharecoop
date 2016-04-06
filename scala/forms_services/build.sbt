organization := "deductions"

name := "semantic_forms_services"

version := "1.0-SNAPSHOT"

lazy val semantic_forms =  RootProject(file("../forms"))

lazy val semantic_forms_services = (project in file("."))
        .dependsOn(semantic_forms)
	.enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % Test


javacOptions ++= Seq("-source","1.7", "-target","1.7")

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)
// cf http://stackoverflow.com/questions/16400877/local-dependencies-resolved-by-sbt-but-not-by-play-framework


