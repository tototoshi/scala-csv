import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys.publishMavenStyle

val Name = "scala-csv"
val Organization = "com.github.tototoshi"
val Version = "1.3.6-SNAPSHOT-scalajs"
val ScalaVersion = "2.12.6"
val CrossScalaVersion = Seq("2.12.6", "2.11.12", "2.10.7", "2.13.1")


lazy val shared = project.in(file("shared")).settings(
  name := Name,
  organization := Organization,
  version := Version,
  scalaVersion := ScalaVersion,
  crossScalaVersions := CrossScalaVersion
) enablePlugins (ScalaJSPlugin)

lazy val jvm = project.in(file("jvm")) settings(
  name := Name,
  organization := Organization,
  version := Version,
  scalaVersion := ScalaVersion,
  crossScalaVersions := CrossScalaVersion,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.6-SNAP1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
  ),
  libraryDependencies ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
    case Some((2, v)) if v <= 12 =>
      Seq("com.storm-enroute" %% "scalameter" % "0.8.2" % "test")
  }.toList.flatten,
  scalacOptions ++= Seq(
    "-deprecation",
    "-language:_"
  ),
  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
    case Some((2, v)) if v >= 11 => Seq("-Ywarn-unused")
  }.toList.flatten,
  testFrameworks += new TestFramework(
    "org.scalameter.ScalaMeterFramework"
  ),
  parallelExecution in Test := false,
  logBuffered := false,
  javacOptions in compile += "-Xlint",
  javacOptions in compile ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 11 =>
      Seq("-target", "6", "-source", "6")
    case _ =>
      if (scala.util.Properties.isJavaAtLeast("9")) {
        // if Java9
        Nil
      } else {
        Seq("-target", "8")
      }
    }
  },
  initialCommands := """
                       |import com.github.tototoshi.csv._
                     """.stripMargin,
  githubOwner := "waveinch",
  githubRepository := "scala-csv",
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomExtra := <url>http://github.com/tototoshi/scala-csv</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:tototoshi/scala-csv.git</url>
      <connection>scm:git:git@github.com:tototoshi/scala-csv.git</connection>
    </scm>
    <developers>
      <developer>
        <id>tototoshi</id>
        <name>Toshiyuki Takahashi</name>
        <url>http://tototoshi.github.com</url>
      </developer>
    </developers>,
  (sources in Test) := {
    val s = (sources in Test).value
    val exclude = Set("CsvBenchmark.scala")
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v <= 12 =>
        s
      case _ =>
        s.filterNot(f => exclude(f.getName))
    }
  }
) dependsOn (shared)

