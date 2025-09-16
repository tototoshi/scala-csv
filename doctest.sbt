doctestTestFramework := DoctestTestFramework.ScalaTest
doctestGenTests := {
  scalaBinaryVersion.value match {
    case "3" | "2.13" | "2.12" =>
      doctestGenTests.value
    case _ =>
      Nil
  }
}
