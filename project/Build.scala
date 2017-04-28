import sbt._
import Keys._

object ScalaCSVProject extends Build {

  lazy val root = Project (
    id = "scala-csv",
    base = file ("."),
    settings = Seq (
      name := "scala-csv",
      version := "1.3.5-SNAPSHOT",
      scalaVersion := "2.11.11",
      crossScalaVersions := Seq("2.11.11", "2.10.6", "2.12.2"),
      TaskKey[Unit]("checkScalariform") := {
        val diff = "git diff".!!
        if(diff.nonEmpty){
          sys.error("Working directory is dirty!\n" + diff)
        }
      },
      organization := "com.github.tototoshi",
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.3" % "test",
        "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
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
      (sources in Test) := {
        val s = (sources in Test).value
        val exclude = Set("CsvBenchmark.scala")
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, v)) if v <= 12 =>
            s
          case _ =>
            s.filterNot(f => exclude(f.getName))
        }
      },
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
            Seq("-target", "8")
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
    )
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
