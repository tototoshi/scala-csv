package com.github.tototoshi.csv

import java.io.FileWriter

import org.scalatest.{ FunSpec, BeforeAndAfter }
import org.scalatest.matchers._

import java.io.File

class CSVWriterSpec extends FunSpec with ShouldMatchers with BeforeAndAfter with Using {
  def fixture = new {

  }

  def readFileAsString(file: String) = {
    using (io.Source.fromFile(file)) { src =>
      src.getLines.mkString("", "\n", "\n")
    }
  }

  after {
    new java.io.File("test.csv").delete()
  }

  describe("CSVWriter") {

    describe ("#open") {
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

      it("should be constructed with filename string and encoding") {
        using (CSVWriter.open("test.csv", "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with filename string, append flag and encoding") {
        using (CSVWriter.open("test.csv", false, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with file and encoding") {
        using (CSVWriter.open(new File("test.csv"), "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with file, append flag and encoding") {
        using (CSVWriter.open(new File("test.csv"), false, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

    }

    it("write all lines to file") {
      using (CSVWriter.open(new FileWriter("test.csv"))) { writer =>
        writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
      }

      val expected = """|"a","b","c"
                        |"d","e","f"
                        |""".stripMargin

      readFileAsString("test.csv") should be (expected)
    }

    it("write single line to file") {
      using (CSVWriter.open(new FileWriter("test.csv"))) { writer =>
        writer.writeRow(List("a", "b", "c"))
        writer.writeRow(List("d", "e", "f"))
      }

      val expected = """|"a","b","c"
                        |"d","e","f"
                        |""".stripMargin

      readFileAsString("test.csv") should be (expected)
    }

    describe("#flush") {
      it ("flush stream") {
        using (CSVWriter.open("test.csv")) { writer =>
          writer.writeRow(List("a", "b", "c"))
          writer.flush()
          val content = using (CSVReader.open("test.csv")) { reader =>
            reader.all
          }
          content should be (List(List("a", "b", "c")))
        }
      }
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

        val expected = """|"a","b","c"
                          |"d","e","f"
                          |"h","i","j"
                          |""".stripMargin

        readFileAsString("test.csv") should be (expected)
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

        val expected = """|"d","e","f"
                          |""".stripMargin

        readFileAsString("test.csv") should be (expected)

        using (CSVWriter.open(new File("test.csv"), append = false)) { writer =>
          writer.writeRow(List("h", "i", "j"))
        }

        val expected2 = """|"h","i","j"
                          |""".stripMargin

        readFileAsString("test.csv") should be (expected2)
      }
    }

  }
}

