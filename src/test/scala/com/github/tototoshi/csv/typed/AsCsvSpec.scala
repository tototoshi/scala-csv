package com.github.tototoshi.csv.typed

import java.io.StringWriter

import com.github.tototoshi.csv.Using
import org.scalatest._
import shapeless.Sized
import shapeless.Nat._2

class AsCsvSpec extends FunSpec with ShouldMatchers with Using {
  case class Report(name: String, entries: Seq[Entry])

  case class Entry(someField: String, someOtherField: Int)

  object Report {
    implicit val reportAsCsv: AsCsv[Report] = new AsCsv[Report] {
      type HeaderSize = _2
      def header: Sized[Seq[String], HeaderSize] = {
        Sized("somefield", "someotherfield")
      }

      type Record = Entry
      lazy val asCsvRecord = implicitly[AsCsvRecord[Record] { type RecordSize = HeaderSize }]

      def records(report: Report): Seq[Record] = report.entries
    }
  }

  object Entry {
    implicit val entryAsCsvRecord: AsCsvRecord.Aux[Entry, _2] = new AsCsvRecord[Entry] {
      type RecordSize = _2

      def fields(entry: Entry): Sized[Seq[String], RecordSize] = {
        Sized(entry.someField, entry.someOtherField.toString)
      }
    }
  }

  describe("AsCsv") {
    it("should serialize values to CSV as dictated in their type class instances") {
      val report = Report("irrelevant", Seq(
        Entry("jan", 104),
        Entry("feb", 98),
        Entry("mar", 110)
      ))

      val stringWriter = new StringWriter
      report.asCsvInto(stringWriter)
      stringWriter.close()

      println(stringWriter.toString)

      stringWriter.toString should equal("somefield,someotherfield\r\njan,104\r\nfeb,98\r\nmar,110\r\n")
    }
  }
}
