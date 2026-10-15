ThisBuild / version := "2.0.1-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.18"

ThisBuild / organization := "com.github.tototoshi"

val jvmScalaVersions = Seq("2.12.21", "2.11.12", "2.10.7", "2.13.18", "3.3.8")
val nativeScalaVersions = Seq("2.12.21", "2.13.18", "3.3.8")

val scalatestVersion = "3.2.20"

lazy val scalaCsv = (projectMatrix in file("."))
  .settings(
    name := "scala-csv",

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest-funspec" % scalatestVersion % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % scalatestVersion % Test,
      if (scalaVersion.value.startsWith("2.") && isJvm.value) "org.scalacheck" %% "scalacheck" % "1.14.3" % Test
      else "org.scalacheck" %% "scalacheck" % "1.20.0" % Test
    ),

    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-language:implicitConversions"
    ),

    // the Scala Native compiler plugin fails on -Yfuture-lazy-vals
    scalacOptions ++= {
      if (!isJvm.value) Nil
      else CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12 | 13)) =>
          Seq(
            "-release:8",
          )
        case Some((2, _)) =>
          Nil
        case _ =>
          if (scalaVersion.value.startsWith("3.3.")) {
            Seq(
              "-release:11",
              "-Yfuture-lazy-vals",
            )
          } else {
            Nil
          }
      }
    },

    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
      case Some((2, v)) if v >= 11 => Seq("-Ywarn-unused")
    }.toList.flatten,

    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)){
      case Some((2, 11 | 12)) =>
        Seq("-Xsource:3")
      case Some((2, 13)) =>
        Seq("-Xsource:3-cross")
    }.toList.flatten,

    doctestTestFramework := DoctestTestFramework.ScalaTest,

    doctestScalaTestVersion := Some(scalatestVersion),

    Test / parallelExecution := false,

    logBuffered := false,

    compile / javacOptions += "-Xlint",

    compile / javacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v <= 11 && !sys.env.isDefinedAt("GITHUB_ACTION") =>
          Seq("-target", "7", "-source", "7")
        case _ =>
          Seq("-target", "8", "-source", "8")
      }
    },

    initialCommands := """
                         |import com.github.tototoshi.csv._
                       """.stripMargin,

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
    </developers>,

    Compile / unmanagedSourceDirectories += {
      val dir = CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v <= 12 =>
          "scala-2.13-"
        case _ =>
          "scala-2.13+"
      }
      (Compile / sourceDirectory).value / dir
    }
  )
  .jvmPlatform(scalaVersions = jvmScalaVersions)
  .nativePlatform(
    scalaVersions = nativeScalaVersions,
    settings = Seq(
      // the JVM build keeps the Java readers for binary compatibility with previous publications,
      // the native build uses their Scala counterparts from src/main/scalanative
      Compile / unmanagedSourceDirectories -= (Compile / javaSource).value,
      // scalatest is built against an older 0.5.x test-interface
      libraryDependencySchemes +=
        "org.scala-native" % s"test-interface_${platform.value}_${scalaBinaryVersion.value}" % VersionScheme.EarlySemVer
    )
  )

lazy val root = (project in file("."))
  .aggregate(scalaCsv.projectRefs*)
  .settings(
    Compile / sources := Nil,
    Test / sources := Nil,
    publish / skip := true,
  )

def isJvm = Def.setting(virtualAxes.value.contains(VirtualAxis.jvm))
