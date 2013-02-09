import sbt._
import Keys._

object ScalaCSVProject extends Build {

  lazy val root = Project (
    id = "scala-csv",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      name := "scala-csv",
      version := "0.4.0",
      crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0"),
      organization := "com.github.tototoshi",
      libraryDependencies ++= Seq(
        "net.sf.opencsv" % "opencsv" % "2.3",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      )
    )
  )
}

