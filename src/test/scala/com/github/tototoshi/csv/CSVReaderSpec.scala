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
      using (new CSVReader(new File("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ::: fields
        }
      }
      res.mkString should be ("abcdef")
    }

    it("read CSV from file") {
      var res: List[String] = Nil
      using (new CSVReader(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader foreach { fields =>
          res = res ::: fields
        }
      }
      res.mkString should be ("abcdef")
    }

    it("read all") {
      using (new CSVReader(new FileReader("src/test/resources/simple.csv"))) { reader =>
        reader.all should be (List(List("a", "b", "c"), List("d", "e", "f")))
      }
    }

  }
}

