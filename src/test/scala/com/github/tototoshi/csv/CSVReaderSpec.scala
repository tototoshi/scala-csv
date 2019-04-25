package com.github.tototoshi.csv

import java.io.{ UnsupportedEncodingException, FileReader, File, StringReader }

import org.scalatest._

class CSVReaderSpec extends FunSpec with Matchers with Using {

  def fixture = new {

  }

  describe("CSVReader") {

    it("should be constructed with java.io.File") {
      var res: List[String] = Nil
      using(CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString should be("abcdef")
    }

    it("should be constructed with filename") {
      var res: List[String] = Nil
      using(CSVReader.open("src/test/resources/simple.csv")) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString should be("abcdef")
    }

    it("should be constructed with CSVFormat") {
      implicit object format extends DefaultCSVFormat {
        override val delimiter: Char = '#'
        override val quoteChar: Char = '$'
      }

      using(CSVReader.open("src/test/resources/hash-separated-dollar-quote.csv")(format)) { reader =>
        {
          val map = reader.allWithHeaders()
          map(0)("Foo ") should be("a")
        }
      }

      using(CSVReader.open("src/test/resources/hash-separated-dollar-quote.csv", "utf-8")(format)) { reader =>
        val map = reader.allWithHeaders()
        map(0)("Foo ") should be("a")
      }
    }

    it("should throws UnsupportedEncodingException when unsupprted encoding is specified") {
      intercept[UnsupportedEncodingException] {
        using(CSVReader.open("src/test/resources/hash-separated-dollar-quote.csv", "unknown")) { reader =>
          val map = reader.allWithHeaders()
          map(0)("Foo ") should be("a")
        }
      }
    }

    it("should be able to read an empty line") {
      using(CSVReader.open("src/test/resources/has-empty-line.csv", "utf-8")) { reader =>
        val lines = reader.all()
        lines(1) should be(List(""))
      }

      val format = new DefaultCSVFormat {
        override val treatEmptyLineAsNil = true
      }
      using(CSVReader.open("src/test/resources/has-empty-line.csv", "utf-8")(format)) { reader =>
        val lines = reader.all()
        lines(1) should be(Nil)
      }
    }

    it("should be able to read empty fields") {
      using(CSVReader.open("src/test/resources/has-empty-fields-and-no-eol.csv", "utf-8")) { reader =>
        val lines = reader.all()
        lines(0) should be(List("a", "", "b", "", "c", ""))
      }
    }

    it("read simple CSV from file") {
      var res: List[String] = Nil
      using(CSVReader.open(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString should be("abcdef")
    }

    it("read simple CSV string") {
      val csvString = "a,b,c\nd,e,f\n"
      var res: List[String] = Nil
      using(CSVReader.open(new StringReader(csvString))) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString should be("abcdef")
    }

    it("issue #22") {
      var res: List[String] = Nil
      using(CSVReader.open("src/test/resources/issue22.csv")) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
    }

    it("issue #32") {
      var res: List[String] = Nil
      using(CSVReader.open("src/test/resources/issue32.csv")(new DefaultCSVFormat {
        override val escapeChar: Char = '\\'
      })) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
    }

    it("should read csv file whose escape char is backslash") {
      var res: List[String] = Nil
      implicit val format = new DefaultCSVFormat {
        override val escapeChar: Char = '\\'
      }
      using(CSVReader.open("src/test/resources/backslash-escape.csv")(format)) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res should be(List("field1", "field2", "field3 says, \"escaped with backslash\""))
    }

    it("read simple CSV file with empty quoted fields") {
      var res: List[String] = Nil
      using(CSVReader.open("src/test/resources/issue30.csv")) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString(",") should be("h1,h2,h3,a1,,a3,b1,b2,b3")
    }

    it("should read a file starting with BOM") {
      var res: List[String] = Nil
      using(CSVReader.open("src/test/resources/bom.csv")) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res should be(List("a", "b", "c"))
    }

    it("should be throw exception against malformed input") {
      intercept[MalformedCSVException] {
        using(CSVReader.open(new FileReader("src/test/resources/malformed.csv"))) { reader =>
          reader.all()
        }
      }
    }

    it("read CSV file including escaped fields") {
      var res: List[String] = Nil
      using(CSVReader.open(new FileReader("src/test/resources/escape.csv"))) { reader =>
        reader foreach { fields =>
          res = res ++ fields
        }
      }
      res.mkString should be("""abcd"ef""")
    }

    it("should correctly parse fields with line breaks enclosed in double quotes") {
      var res: List[Seq[String]] = Nil
      using(CSVReader.open(new FileReader("src/test/resources/line-breaks.csv"))) { reader =>
        reader foreach { fields =>
          res = res :+ fields
        }
      }
      res(0) should be(List("a", "b\nb", "c"))
      res(1) should be(List("\nd", "e", "f"))
    }

    it("read TSV from file") {
      implicit val format = new TSVFormat {}
      var res: List[Seq[String]] = Nil
      using(CSVReader.open(new FileReader("src/test/resources/simple.tsv"))(format)) { reader =>
        reader.foreach { fields =>
          res = res ::: fields :: Nil
        }
      }
      res(0) should be(List("a", "b", "c"))
      res(1) should be(List("d", "e", "f"))
    }

    it("has #toStream") {
      using(CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        val stream = reader.toStream
        stream.drop(1).head.mkString should be("def")
      }
    }

    it("has #readNext") {
      using(CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        reader.readNext()
        reader.readNext.get.mkString should be("def")
      }
    }

    it("has #all") {
      using(CSVReader.open(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader.all should be(List(List("a", "b", "c"), List("d", "e", "f")))
      }
    }

    describe("iterator fetched from #iterator") {

      it("has #hasNext") {
        using(CSVReader.open("src/test/resources/simple.csv")) { reader =>
          val it = reader.iterator
          it.hasNext should be(true)
          it.hasNext should be(true)
          it.hasNext should be(true)
          it.foreach(x => ()) // consume
          it.hasNext should be(false)
        }
      }

      describe("#next") {
        it("should return the next line") {
          using(CSVReader.open("src/test/resources/simple.csv")) { reader =>
            val it = reader.iterator
            it.next should be(List("a", "b", "c"))
            it.next should be(List("d", "e", "f"))
          }
        }
        it("should throw NoSuchElementException") {
          using(CSVReader.open("src/test/resources/simple.csv")) { reader =>
            val it = reader.iterator
            it.next
            it.next
            intercept[java.util.NoSuchElementException] {
              it.next
            }
          }
        }
      }

      it("iterate all lines") {
        var lineCount = 0
        using(CSVReader.open("src/test/resources/simple.csv")) { reader =>
          val it = reader.iterator
          it.foreach { line => lineCount += 1 }
        }
        lineCount should be(2)
      }

      describe("When the file to be parsed is huge") {
        it("should iterate all lines without any trouble") {
          val tmpfile: File = File.createTempFile("csv", "test")
          tmpfile.deleteOnExit()
          using(new java.io.PrintWriter(tmpfile)) { writer =>
            (1 to 100000).foreach { i =>
              writer.println(i)
            }
          }
          var count = 0
          using(CSVReader.open(tmpfile)) { reader =>
            reader.foreach { row =>
              count += 1
            }
          }
          count should be(100000)
        }
      }
    }

    describe("#iteratorWithHeaders") {
      describe("When the file is empty") {
        it("returns an empty list") {
          using(CSVReader.open(new FileReader("src/test/resources/empty.csv"))) { reader =>
            reader.iteratorWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has only one line") {
        it("returns an empty list") {
          using(CSVReader.open(new FileReader("src/test/resources/only-header.csv"))) { reader =>
            reader.iteratorWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has many lines") {
        it("returns a List of Map[String, String]") {
          using(CSVReader.open(new FileReader("src/test/resources/with-headers.csv"))) { reader =>
            val iterator = reader.iteratorWithHeaders
            iterator.next() should be(Map("Foo" -> "a", "Bar" -> "b", "Baz" -> "c"))
            iterator.next() should be(Map("Foo" -> "d", "Bar" -> "e", "Baz" -> "f"))
          }
        }
      }
    }

    describe("#allHeaders") {
      describe("When the file is empty") {
        it("returns an empty list") {
          using(CSVReader.open(new FileReader("src/test/resources/empty.csv"))) { reader =>
            reader.allWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has only one line") {
        it("returns an empty list") {
          using(CSVReader.open(new FileReader("src/test/resources/only-header.csv"))) { reader =>
            reader.allWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has many lines") {
        it("returns a List of Map[String, String]") {
          using(CSVReader.open(new FileReader("src/test/resources/with-headers.csv"))) { reader =>
            reader.allWithHeaders should be(List(Map("Foo" -> "a", "Bar" -> "b", "Baz" -> "c"), Map("Foo" -> "d", "Bar" -> "e", "Baz" -> "f")))
          }
        }
      }
    }

    describe("#allOrderedHeaders") {
      describe("When the file is empty") {
        it("returns an empty list") {
          using(CSVReader.open(new FileReader("src/test/resources/empty.csv"))) { reader =>
            reader.allWithOrderedHeaders() should be((Nil, Nil))
          }
        }
      }
      describe("When the file has only header line") {
        it("returns only header names") {
          using(CSVReader.open(new FileReader("src/test/resources/only-header.csv"))) { reader =>
            reader.allWithOrderedHeaders should be((List("foo", "bar"), Nil))
          }
        }
      }
      describe("When the file has many headers and many lines") {
        it("returns header names in order and data") {
          using(CSVReader.open(new FileReader("src/test/resources/with-headers.csv"))) { reader =>
            val result = reader.allWithOrderedHeaders()
            result._1 should be(List("Foo", "Bar", "Baz"))
            result._2 should be(List(Map("Foo" -> "a", "Bar" -> "b", "Baz" -> "c"), Map("Foo" -> "d", "Bar" -> "e", "Baz" -> "f")))
          }
        }
      }
    }
  }
}
