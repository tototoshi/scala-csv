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

    describe("iterator fetched from #iterator") {

      it ("has #hasNext") {
        using (CSVReader.open("src/test/resources/simple.csv")) { reader =>
          val it = reader.iterator
          it.hasNext should be (true)
          it.hasNext should be (true)
          it.hasNext should be (true)
          it.foreach(x => ()) // cosume
          it.hasNext should be (false)
        }
      }

      describe ("#next") {
        it ("should return the next line") {
          using (CSVReader.open("src/test/resources/simple.csv")) { reader =>
            val it = reader.iterator
            it.next should be (List("a", "b", "c"))
            it.next should be (List("d", "e", "f"))
          }
        }
        it ("should throw NoSuchElementException") {
          using (CSVReader.open("src/test/resources/simple.csv")) { reader =>
            val it = reader.iterator
            it.next
            it.next
            intercept[java.util.NoSuchElementException] {
              it.next
            }
          }
        }
      }

      it ("iterate all lines") {
        var lineCount = 0
        using (CSVReader.open("src/test/resources/simple.csv")) { reader =>
          val it = reader.iterator
          it.foreach { line => lineCount += 1 }
        }
        lineCount should be (2)
      }
    }
    describe("#allHeaders") {
      describe("When the file is empty") {
        it("returns an empty list") {
          using (CSVReader.open(new FileReader("src/test/resources/empty.csv"))) { reader =>
            reader.allWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has only one line") {
        it("returns an empty list") {
          using (CSVReader.open(new FileReader("src/test/resources/only-header.csv"))) { reader =>
            reader.allWithHeaders should be('empty)
          }
        }
      }
      describe("When the file has many lines") {
        it("returns a List of Map[String, String]") {
          using (CSVReader.open(new FileReader("src/test/resources/with-headers.csv"))) { reader =>
            reader.allWithHeaders should be (List(Map("Foo" -> "a", "Bar" -> "b", "Baz" -> "c"), Map("Foo" -> "d", "Bar" -> "e", "Baz" ->  "f")))
          }
        }
      }
    }

  }
}

