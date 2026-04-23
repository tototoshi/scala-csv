package com.github.tototoshi.csv

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CSVParserSpec extends AnyFunSpec with Matchers {

  describe("CSVParser.parse") {

    describe("when the escape char appears in an unquoted field and is not followed by a special char") {

      it("preserves the original escape char (default format)") {
        CSVParser.parse("ab\"c,d", '"', ',', '"') should be(Some(List("ab\"c", "d")))
      }

      it("preserves the original escape char when escape char differs from quote char") {
        CSVParser.parse(",|x", '|', ',', '"') should be(Some(List("", "|x")))
      }
    }
  }
}
