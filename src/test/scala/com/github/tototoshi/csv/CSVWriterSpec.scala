package com.github.tototoshi.csv

import java.io.{ FileOutputStream, UnsupportedEncodingException, FileWriter, File }

import org.scalatest._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CSVWriterSpec extends AnyFunSpec with Matchers with BeforeAndAfter with Using {

  // Use a temp file unique to this JVM so multiple matrix projects can run tests in parallel
  private lazy val testCsvFile = {
    val f = java.io.File.createTempFile("scalacsv-", ".csv")
    f.deleteOnExit()
    f
  }
  private def testCsvPath: String = testCsvFile.getPath

  def readFileAsString(file: String) = {
    using(io.Source.fromFile(file)) { src =>
      src.getLines().mkString("", "\n", "\n")
    }
  }

  after {
    if (testCsvFile.exists()) testCsvFile.delete()
  }

  describe("CSVWriter") {

    describe("#open") {

      it("should be constructed with OutputStream") {
        using(CSVWriter.open(new FileOutputStream(testCsvPath))) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with OutputStream and encoding") {
        using(CSVWriter.open(new FileOutputStream(testCsvPath), "UTF-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with java.io.File") {
        using(CSVWriter.open(testCsvFile)) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with filename string") {
        using(CSVWriter.open(testCsvPath)) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with filename string and encoding") {
        using(CSVWriter.open(testCsvPath, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with filename string, append flag and encoding") {
        using(CSVWriter.open(testCsvPath, false, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with file and encoding") {
        using(CSVWriter.open(testCsvFile, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should be constructed with file, append flag and encoding") {
        using(CSVWriter.open(testCsvFile, false, "utf-8")) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }
      }

      it("should throws UnsupportedEncodingException when unsupprted encoding is specified") {
        intercept[UnsupportedEncodingException] {
          using(CSVWriter.open(testCsvFile, false, "unknown")) { writer =>
            writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
          }
        }
      }

    }

    describe("#writeAll") {
      it("write all lines to file") {
        using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
          writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
        }

        val expected = """|a,b,c
        |d,e,f
        |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }

      it("writes null fields as empty strings") {
        using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
          writer.writeAll(List(List("a", null, "c"), List("d", "e", null)))
        }

        val expected = """|a,,c
        |d,e,
        |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }

      describe("When stream is already closed") {
        it("throws an Exception") {
          val writer = CSVWriter.open(testCsvPath)
          writer.close()
          intercept[java.io.IOException] {
            writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))
          }
        }
      }
    }

    describe("#writeRow") {
      it("write single line to file") {
        using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
          writer.writeRow(List("a", "b", "c"))
          writer.writeRow(List("d", "e", "f"))
        }

        val expected = """|a,b,c
                         |d,e,f
                         |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }
      it("write single line with null fieldsto file") {
        using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
          writer.writeRow(List("a", null, "c"))
          writer.writeRow(List("d", "e", null))
        }

        val expected = """|a,,c
                         |d,e,
                         |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }

      it("should escape the quoteChar with escapeChar when it is included in the field") {
        using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
          writer.writeRow(List("a", "b\"", "c"))
          writer.writeRow(List("d", "e", "f"))
        }

        val expected = "a,\"b\"\"\",c\nd,e,f\n"

        readFileAsString(testCsvPath) should be(expected)
      }

      it("should escape the quoteChar with customized escapeChar when it is included in the field and escapeChar is changed from default value") {

        object escapeCharChangedFormat extends DefaultCSVFormat {
          override val escapeChar: Char = '\\'
        }

        using(CSVWriter.open(new FileWriter(testCsvPath))(escapeCharChangedFormat)) { writer =>
          writer.writeRow(List("a", "b\"", "c"))
          writer.writeRow(List("d", "e", "f"))
        }

        val expected = """|a,"b\"",c
                        |d,e,f
                        |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }

      describe("When a field contains delimiter in it") {
        it("should escape the delimiter") {
          using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
            writer.writeRow(List("a", "b,", "c"))
            writer.writeRow(List("d", "e", "f"))
          }

          val expected = "a,\"b,\",c\nd,e,f\n"

          readFileAsString(testCsvPath) should be(expected)
        }
      }
      describe("When quoting is set to QUOTE_ALL") {
        it("should quote all fields") {
          object quoteAllFormat extends DefaultCSVFormat {
            override val quoting: Quoting = QUOTE_ALL
          }

          using(CSVWriter.open(new FileWriter(testCsvPath))(quoteAllFormat)) { writer =>
            writer.writeRow(List("a", "b", "c"))
            writer.writeRow(List("d", "e", "f"))
          }

          val expected = """|"a","b","c"
                           |"d","e","f"
                           |""".stripMargin

          readFileAsString(testCsvPath) should be(expected)
        }
      }
      describe("When quoting is set to QUOTE_NONE") {
        it("should quote no field") {
          object quoteNoneFormat extends DefaultCSVFormat {
            override val quoting: Quoting = QUOTE_NONE
          }

          using(CSVWriter.open(new FileWriter(testCsvPath))(quoteNoneFormat)) { writer =>
            writer.writeRow(List("a", "b", "c"))
            writer.writeRow(List("d", "e", "f"))
          }

          val expected = """|a,b,c
                           |d,e,f
                           |""".stripMargin

          readFileAsString(testCsvPath) should be(expected)
        }
      }
      describe("When quoting is set to QUOTE_NONNUMERIC") {
        it("should quote only nonnumeric fields") {
          object quoteNoneFormat extends DefaultCSVFormat {
            override val quoting: Quoting = QUOTE_NONNUMERIC
          }

          using(CSVWriter.open(new FileWriter(testCsvPath))(quoteNoneFormat)) { writer =>
            writer.writeRow(List("a", "b", "1"))
            writer.writeRow(List("2.0", "e", "f"))
          }

          val expected = """|"a","b",1
                            |2.0,"e","f"
                            |""".stripMargin

          readFileAsString(testCsvPath) should be(expected)
        }
      }
      describe("When a field contains cr or lf in it") {
        it("should quoted the field") {
          using(CSVWriter.open(new FileWriter(testCsvPath))) { writer =>
            writer.writeRow(List("a", "b\n", "c"))
            writer.writeRow(List("d", "e", "f"))
          }

          val expected = """|a,"b
                            |",c
                            |d,e,f
                            |""".stripMargin

          readFileAsString(testCsvPath) should be(expected)
        }
      }
      describe("When stream is already closed") {
        it("throws an Exception") {
          val writer = CSVWriter.open(testCsvPath)
          writer.close()
          intercept[java.io.IOException] {
            writer.writeRow(List("a", "b", "c"))
          }
        }
      }

    }

    describe("#flush") {
      it("flush stream") {
        using(CSVWriter.open(testCsvPath)) { writer =>
          writer.writeRow(List("a", "b", "c"))
          writer.flush()
          val content = using(CSVReader.open(testCsvPath)) { reader =>
            reader.all()
          }
          content should be(List(List("a", "b", "c")))
        }
      }
    }

    describe("When append=true") {
      it("append lines") {
        using(CSVWriter.open(testCsvPath)) { writer =>
          writer.writeRow(List("a", "b", "c"))
        }
        using(CSVWriter.open(testCsvPath, append = true)) { writer =>
          writer.writeRow(List("d", "e", "f"))
        }

        using(CSVWriter.open(testCsvFile, append = true)) { writer =>
          writer.writeRow(List("h", "i", "j"))
        }

        val expected = """|a,b,c
                          |d,e,f
                          |h,i,j
                          |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)
      }
    }

    describe("When append=false") {
      it("overwrite the file") {
        using(CSVWriter.open(testCsvPath)) { writer =>
          writer.writeRow(List("a", "b", "c"))
        }
        using(CSVWriter.open(testCsvPath, append = false)) { writer =>
          writer.writeRow(List("d", "e", "f"))
        }

        val expected = """|d,e,f
                          |""".stripMargin

        readFileAsString(testCsvPath) should be(expected)

        using(CSVWriter.open(testCsvFile, append = false)) { writer =>
          writer.writeRow(List("h", "i", "j"))
        }

        val expected2 = """|h,i,j
                           |""".stripMargin

        readFileAsString(testCsvPath) should be(expected2)
      }
    }

  }
}
