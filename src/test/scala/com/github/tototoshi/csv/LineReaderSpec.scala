package com.github.tototoshi.csv

import java.io.FileReader

import org.scalatest._
import org.scalatest.funspec.AnyFunSpec

import scala.io.Source
import org.scalatest.matchers.should.Matchers

class LineReaderSpec extends AnyFunSpec with Matchers with Using {

  describe("ReaderLineReader") {

    it("should read line with nl") {
      using(new FileReader("src/test/resources/has-empty-line.csv")) { in =>
        using(new ReaderLineReader(in)) { reader =>
          reader.readLineWithTerminator() should be("a,b,c\n")
          reader.readLineWithTerminator() should be("\n")
          reader.readLineWithTerminator() should be("d,e,f")
        }
      }
    }

  }

  describe("SourceLineReader") {

    it("should read line with nl") {
      using(Source.fromFile("src/test/resources/has-empty-line.csv")) { in =>
        using(new SourceLineReader(in)) { reader =>
          reader.readLineWithTerminator() should be("a,b,c\n")
          reader.readLineWithTerminator() should be("\n")
          reader.readLineWithTerminator() should be("d,e,f")
        }
      }
    }

  }
}
