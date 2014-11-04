import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform.scalariformSettings

object ScalaCSVProject extends Build {

  lazy val root = Project (
    id = "scala-csv",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      name := "scala-csv",
      version := "1.1.0-SNAPSHOT",
      scalaVersion := "2.11.1",
      crossScalaVersions := Seq("2.11.1", "2.10.3", "2.9.1", "2.9.2", "2.9.3"),
      organization := "com.github.tototoshi",
      libraryDependencies ++= (
        if(scalaVersion.value.startsWith("2.1")) {
          Seq(
            "org.scalatest" %% "scalatest" % "2.1.3" % "test",
            "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
          )
        } else {
          Seq(
            "org.scalatest" %% "scalatest" % "1.9.1" % "test",
            "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
          )
        }
      ),
      libraryDependencies ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"
      }.toList,
      scalacOptions <<= scalaVersion.map { sv =>
        if (sv.startsWith("2.10")) {
          Seq(
            "-deprecation",
            "-language:_"
          )
        } else {
          Seq("-deprecation")
        }
      },
      initialCommands := """
                           |import com.github.tototoshi.csv._
                         """.stripMargin,
      publishMavenStyle := true,
      publishTo <<= version { (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      publishArtifact in Test := false,
      pomExtra := _pomExtra
    ) ++ scalariformSettings
  )

  val _pomExtra =
    <url>http://github.com/tototoshi/scala-csv</url>
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
    </developers>

}
