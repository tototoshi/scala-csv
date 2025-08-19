addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.3")

addSbtPlugin("io.github.sbt-doctest" % "sbt-doctest" % "0.11.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "1.3.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native" % "0.5.7")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "always"
