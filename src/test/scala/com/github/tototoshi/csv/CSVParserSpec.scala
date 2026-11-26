package com.github.tototoshi.csv

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CSVParserSpec extends AnyFunSpec with Matchers {

  describe("CSVParser.parse") {

    describe("CRLF handling in each state") {

      it("handles CRLF in the Start state (empty line terminated by CRLF)") {
        CSVParser.parse("\r\n", '\\', ',', '"') should be(Some(List("")))
      }

      it("handles CRLF in the Delimiter state (trailing empty field terminated by CRLF)") {
        CSVParser.parse("a,\r\n", '\\', ',', '"') should be(Some(List("a", "")))
      }

      it("handles CRLF in the Field state (unquoted row terminated by CRLF)") {
        CSVParser.parse("a,b,c\r\n", '\\', ',', '"') should be(Some(List("a", "b", "c")))
      }

      it("handles CRLF in the QuoteEnd state (row of quoted fields terminated by CRLF)") {
        CSVParser.parse("\"a\",\"b\"\r\n", '\\', ',', '"') should be(Some(List("a", "b")))
      }
    }
  }
}
