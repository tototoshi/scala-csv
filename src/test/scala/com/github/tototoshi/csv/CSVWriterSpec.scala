package com.github.tototoshi.csv

import java.io.FileWriter

import org.scalatest.FunSpec
import org.scalatest.matchers._

import java.io.File

class CSVWriterSpec extends FunSpec with ShouldMatchers with Using {
  def fixture = new {

  }

  describe("CSVWriter") {

    it("should be constructed with java.io.File") {
      using (CSVWriter(new File("test.csv"))) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }
      new java.io.File("test.csv").delete()
    }

    it("write all lines to file") {
      using (CSVWriter(new FileWriter("test.csv"))) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }

      io.Source.fromFile("test.csv").getLines.mkString should be (""""a","b","c""d","e","f"""")

      new java.io.File("test.csv").delete()
    }

    it("write single line to file") {
      using (CSVWriter(new FileWriter("test.csv"))) { writer =>
        writer.writeRow(List("a", "b", "c"))
        writer.writeRow(List("d", "e", "f"))
      }

      io.Source.fromFile("test.csv").getLines.mkString should be (""""a","b","c""d","e","f"""")

      new java.io.File("test.csv").delete()
    }

  }
}

