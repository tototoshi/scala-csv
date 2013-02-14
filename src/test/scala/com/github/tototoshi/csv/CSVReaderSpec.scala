package com.github.tototoshi.csv

import java.io.{ FileReader, File }

import org.scalatest.FunSpec
import org.scalatest.matchers._

class CSVReaderSpec extends FunSpec with ShouldMatchers with Using {
  def fixture = new {

  }

  describe("CSVSpec") {

    it("should be constructed with java.io.File") {
      var res: List[String] = Nil
      using (CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ::: fields
        }
      }
      res.mkString should be ("abcdef")
    }

    it("should be constructed with filename") {
      var res: List[String] = Nil
      using (CSVReader.open("src/test/resources/simple.csv")) { reader =>
        reader foreach { fields =>
          res = res ::: fields
        }
      }
      res.mkString should be ("abcdef")
    }

    it("read CSV from file") {
      var res: List[String] = Nil
      using (CSVReader.open(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ::: fields
        }
      }
      res.mkString should be ("abcdef")
    }

    it("has #toStream") {
      using (CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        val stream = reader.toStream
        stream.drop(1).head.mkString should be ("def")
      }
    }

    it("has #readNext") {
      using (CSVReader.open(new File("src/test/resources/simple.csv"))) { reader =>
        reader.readNext()
        reader.readNext.get.mkString should be ("def")
      }
    }

    it("has #all") {
      using (CSVReader.open(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader.all should be (List(List("a", "b", "c"), List("d", "e", "f")))
      }
    }

    describe("#allHeaders") {
      describe("When the file is empty") {
        using (CSVReader.open(new FileReader("src/test/resources/empty.csv"))) { reader =>
          reader.allWithHeaders should be('empty)
        }
      }
      describe("When the file has only one line") {
        using (CSVReader.open(new FileReader("src/test/resources/only-header.csv"))) { reader =>
          reader.allWithHeaders should be('empty)
        }
      }
      describe("When the file has many lines") {
        using (CSVReader.open(new FileReader("src/test/resources/with-headers.csv"))) { reader =>
          reader.allWithHeaders should be (List(Map("Foo" -> "a", "Bar" -> "b", "Baz" -> "c"), Map("Foo" -> "d", "Bar" -> "e", "Baz" ->  "f")))
        }
      }
    }

  }
}

