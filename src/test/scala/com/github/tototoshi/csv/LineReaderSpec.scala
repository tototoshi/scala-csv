package com.github.tototoshi.csv

import java.io.{ UnsupportedEncodingException, FileReader, File, StringReader }

import org.scalatest.FunSpec
import org.scalatest.matchers._

class LineReaderSpec extends FunSpec with ShouldMatchers with Using {

  describe("LineReader") {

    it("should read line with nl") {
      using(new FileReader("src/test/resources/has-empty-line.csv")) { in =>
        using(new LineReader(in)) { reader =>
          reader.readLineWithTerminator() should be("a,b,c\n")
          reader.readLineWithTerminator() should be("\n")
          reader.readLineWithTerminator() should be("d,e,f")
        }
      }
    }

  }
}
