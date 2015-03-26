import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform.scalariformSettings

object ScalaCSVProject extends Build {

  lazy val root = Project (
    id = "scala-csv",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      name := "scala-csv",
      version := "1.2.2-SNAPSHOT",
      scalaVersion := "2.11.6",
      crossScalaVersions := Seq("2.11.6", "2.10.4"),
      organization := "com.github.tototoshi",
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.1.3" % "test",
        "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
      ),
      scalacOptions ++= Seq(
        "-deprecation",
        "-language:_"
      ),
      scalacOptions ++= {
        if(scalaVersion.value.startsWith("2.11")) Seq("-Ywarn-unused")
        else Nil
      },
      javacOptions in compile ++= Seq("-target", "6", "-source", "6", "-Xlint"),
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
