import scala.sys.process._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "com.github.tototoshi"
ThisBuild / version := "2.0.0"

lazy val enableScalameter = settingKey[Boolean]("")

lazy val scalaCsv = projectMatrix.in(file("."))
  .settings(
    name := "scala-csv",

    enableScalameter := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, v)) =>
          v >= 3
        case _ =>
          false
      }
    },

    libraryDependencies ++= {
      if (virtualAxes.value.contains(VirtualAxis.jvm)) {
        Seq(
          "org.scalatest" %% "scalatest-funspec" % "3.2.19" % Test,
          "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test,
          "org.scalacheck" %% "scalacheck" % "1.19.0" % Test
        ) ++ (if (enableScalameter.value && CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 2)) Seq("com.storm-enroute" %% "scalameter" % "0.19" % "test") else Nil)
      } else if (virtualAxes.value.contains(VirtualAxis.native)) {
        Seq(
          "org.scalatest" %%% "scalatest-funspec" % "3.2.19" % Test,
          "org.scalatest" %%% "scalatest-shouldmatchers" % "3.2.19" % Test,
          "org.scalacheck" %%% "scalacheck" % "1.19.0" % Test
        )
      } else {
        Nil
      }
    },

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-language:implicitConversions"
    ),

    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, v)) if v >= 11 => Seq("-Ywarn-unused")
    }.toList.flatten,

    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, 11 | 12)) =>
        Seq("-Xsource:3")
      case Some((2, 13)) =>
        Seq("-Xsource:3-cross")
    }.toList.flatten,

    Test / sources := {
      val s = (Test / sources).value
      val exclude = Set("CsvBenchmark.scala")
      if (enableScalameter.value && virtualAxes.value.contains(VirtualAxis.jvm) && CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 2)) {
        s
      } else {
        s.filterNot(f => exclude(f.getName))
      }
    },

    testFrameworks += new TestFramework(
      "org.scalameter.ScalaMeterFramework"
    ),

    Test / parallelExecution := false,

    logBuffered := false,

    initialCommands := "\nimport com.github.tototoshi.csv._\n",

    publishMavenStyle := true,

    publishTo := (if (isSnapshot.value) None else localStaging.value),

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
    </developers>
  )
  .settings(
    doctestScalaTestVersion := Some("3.2.19")
  )
  .settings(
    Compile / javacOptions ++= {
      if (virtualAxes.value.contains(VirtualAxis.jvm)) {
        Seq("-Xlint") ++ (CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, v)) if v <= 11 && !sys.env.isDefinedAt("GITHUB_ACTION") =>
            Seq("-target", "6", "-source", "6")
          case _ =>
            Seq("-target", "8", "-source", "8")
        })
      } else {
        Nil
      }
    },
    Compile / unmanagedSourceDirectories ++= {
      if (virtualAxes.value.contains(VirtualAxis.jvm)) {
        Seq(baseDirectory.value / "src" / "main" / "java")
      } else {
        Nil
      }
    }
  )
  .jvmPlatform(scalaVersions = Seq("2.12.21", "2.13.18", "3.3.7"))
  .nativePlatform(Seq("3.3.7"))

lazy val root = project.in(file("."))
  .aggregate(scalaCsv.projectRefs: _*)
  .settings(
    publish / skip := true,
    doctestScalaTestVersion := Some("3.2.19"),
    Test / sources := Seq.empty,
    TaskKey[Unit]("checkScalariform") := {
      val diff = "git diff".!!
      if (diff.nonEmpty) {
        sys.error("Working directory is dirty!\n" + diff)
      }
    }
  )
