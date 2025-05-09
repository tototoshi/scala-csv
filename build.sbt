import scala.sys.process._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version := "2.0.0"

ThisBuild / scalaVersion := "2.13.15"

val defaultCrossScalaVersions = Seq("2.12.20", "2.11.12", "2.10.7", "2.13.15", "3.3.4")
val nativeScalaCrossVersions = Seq("2.12.20", "2.13.15", "3.3.4")

lazy val scalaCsv = crossProject(JVMPlatform, NativePlatform).crossType(CrossType.Pure).in(file(".")).settings(
  name := "scala-csv",

  crossScalaVersions := defaultCrossScalaVersions,

  TaskKey[Unit]("checkScalariform") := {
    val diff = "git diff".!!
    if(diff.nonEmpty){
      sys.error("Working directory is dirty!\n" + diff)
    }
  },

  organization := "com.github.tototoshi",

  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest-funspec" % "3.2.19" % Test,
    "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test,
    if (scalaVersion.value.startsWith("2.")) "org.scalacheck" %% "scalacheck" % "1.14.3" % Test
    else "org.scalacheck" %% "scalacheck" % "1.18.1" % Test
  ),

  libraryDependencies ++= {
    if (enableScalameter.value) {
      Seq("com.storm-enroute" %% "scalameter" % "0.19" % Test)
    } else {
      Nil
    }
  },

  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:implicitConversions"
  ),

  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
    case Some((2, v)) if v >= 11 => Seq("-Ywarn-unused")
  }.toList.flatten,

  scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
    case Some((2, 11 | 12)) =>
      Seq("-Xsource:3")
    case Some((2, 13)) =>
      Seq("-Xsource:3-cross")
  }.toList.flatten,

  Test / sources := {
    val s = (Test / sources).value
    val exclude = Set("CsvBenchmark.scala")
    if (enableScalameter.value) {
      s
    } else {
      s.filterNot(f => exclude(f.getName))
    }
  },

  testFrameworks ++= {
    if (enableScalameter.value) {
      Seq(new TestFramework("org.scalameter.ScalaMeterFramework"))
    } else Nil
  },

  Test / parallelExecution := false,

  logBuffered := false,

  compile / javacOptions += "-Xlint",

  compile / javacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v <= 11 =>
        Seq("-target", "6", "-source", "6")
      case _ =>
        Seq("-target", "8", "-source", "8")
    }
  },

  initialCommands := """
                       |import com.github.tototoshi.csv._
                     """.stripMargin,

  publishMavenStyle := true,

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },

  Test / publishArtifact := false,

  pomExtra := <url>https://github.com/tototoshi/scala-csv</url>
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
        <url>https://tototoshi.github.io</url>
      </developer>
    </developers>,

  Compile / unmanagedSourceDirectories += {
    val dir = CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v <= 12 =>
        "scala-2.13-"
      case _ =>
        "scala-2.13+"
    }
    crossProjectBaseDirectory.value / "src" / "main" / dir
  },
  Compile / unmanagedSourceDirectories += {
    val dir = crossProjectPlatform.value match {
      case JVMPlatform => "java" // for binary compatibility with previous JVM publications, to be deprecated
      case _ => "scala-readers"
    }
    crossProjectBaseDirectory.value / "src" / "main" / dir
  }
).nativeSettings(
  crossScalaVersions := nativeScalaCrossVersions
)

val enableScalameter = Def.setting(
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) => 11 <= v && v <= 13 && crossProjectPlatform.value == JVMPlatform
    case _ => false
  }
)

// an aggregate project to pass commands into platform projects
lazy val root = (project in file("."))
  .aggregate(scalaCsv.jvm, scalaCsv.native)
  .settings(
    Compile / sources := Seq.empty,
    Test / sources := Seq.empty,
    artifacts := Seq.empty,
    publish / skip := true,
  )
