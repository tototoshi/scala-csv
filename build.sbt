import scala.sys.process._

Global / onChangedBuildSource := ReloadOnSourceChanges

name := "scala-csv"

version := "1.3.6"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.12.12", "2.11.12", "2.10.7", "2.13.3")

TaskKey[Unit]("checkScalariform") := {
  val diff = "git diff".!!
  if(diff.nonEmpty){
    sys.error("Working directory is dirty!\n" + diff)
  }
}

organization := "com.github.tototoshi"

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "3.2.3" % Test,
    ("org.scalacheck" %% "scalacheck" % "1.14.3" % Test).withDottyCompat(scalaVersion.value)
  )
}

val enableScalameter = settingKey[Boolean]("")

enableScalameter := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) =>
      11 <= v && v <= 13
    case _ =>
      false
  }
}

libraryDependencies ++= {
  if (enableScalameter.value) {
    Seq("com.storm-enroute" %% "scalameter" % "0.19" % "test")
  } else {
    Nil
  }
}

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:implicitConversions"
)

scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
  case Some((2, v)) if v >= 11 => Seq("-Ywarn-unused")
}.toList.flatten

(sources in Test) := {
  val s = (sources in Test).value
  val exclude = Set("CsvBenchmark.scala")
  if (enableScalameter.value) {
    s
  } else {
    s.filterNot(f => exclude(f.getName))
  }
}

testFrameworks += new TestFramework(
  "org.scalameter.ScalaMeterFramework"
)

parallelExecution in Test := false

logBuffered := false

javacOptions in compile += "-Xlint"

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
}

initialCommands := """
                     |import com.github.tototoshi.csv._
                   """.stripMargin

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

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
</developers>
