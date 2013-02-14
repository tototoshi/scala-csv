package com.github.tototoshi.csv

import java.io.FileWriter

import org.scalatest.{ FunSpec, BeforeAndAfter }
import org.scalatest.matchers._

import java.io.File

class CSVWriterSpec extends FunSpec with ShouldMatchers with BeforeAndAfter with Using {
  def fixture = new {

  }

  after {
    new java.io.File("test.csv").delete()
  }

  describe("CSVWriter") {

    it("should be constructed with java.io.File") {
      using (CSVWriter.open(new File("test.csv"))) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }
    }

    it("should be constructed with filename string") {
      using (CSVWriter.open("test.csv")) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }
    }

    it("write all lines to file") {
      using (CSVWriter.open(new FileWriter("test.csv"))) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }

      io.Source.fromFile("test.csv").getLines.mkString should be (""""a","b","c""d","e","f"""")
    }

    it("write single line to file") {
      using (CSVWriter.open(new FileWriter("test.csv"))) { writer =>
        writer.writeRow(List("a", "b", "c"))
        writer.writeRow(List("d", "e", "f"))
      }

      io.Source.fromFile("test.csv").getLines.mkString should be (""""a","b","c""d","e","f"""")
    }

    describe("When append=true") {
      it ("append lines") {
        using (CSVWriter.open("test.csv")) { writer =>
          writer.writeRow(List("a", "b", "c"))
        }
        using (CSVWriter.open("test.csv", append = true)) { writer =>
          writer.writeRow(List("d", "e", "f"))
        }

        using (CSVWriter.open(new File("test.csv"), append = true)) { writer =>
          writer.writeRow(List("h", "i", "j"))
        }

        io.Source.fromFile("test.csv").getLines.mkString should be (""""a","b","c""d","e","f""h","i","j"""")
      }
    }

    describe("When append=false") {
      it ("overwrite the file") {
        using (CSVWriter.open("test.csv")) { writer =>
          writer.writeRow(List("a", "b", "c"))
        }
        using (CSVWriter.open("test.csv", append = false)) { writer =>
          writer.writeRow(List("d", "e", "f"))
        }

        io.Source.fromFile("test.csv").getLines.mkString should be (""""d","e","f"""")

        using (CSVWriter.open(new File("test.csv"), append = false)) { writer =>
          writer.writeRow(List("h", "i", "j"))
        }

        io.Source.fromFile("test.csv").getLines.mkString should be (""""h","i","j"""")
      }
    }

  }
}

