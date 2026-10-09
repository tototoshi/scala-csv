package com.github.tototoshi.csv

import java.io.{FileReader, StringReader}

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

    it("should read a quoted value that has cr that is not followd by nl") {
      using(Source.fromFile("src/test/resources/has-cr-quoted-value.csv")) { in =>
        using(new SourceLineReader(in)) { reader =>
          reader.readLineWithTerminator() should be("a,\"b\r")
          reader.readLineWithTerminator() should be("\",c\n")
          reader.readLineWithTerminator() should be("d,e,f\n")
        }
      }
    }

    it("should keep the LF of a CRLF pair, as ReaderLineReader does") {
      // Source.fromString is not a BufferedSource, so `using` has no CanClose
      // for it on 2.10; drain closes the reader itself.
      drain(new SourceLineReader(Source.fromString("a,b,c\r\nd,e,f\r\n"))) should be(
        List("a,b,c\r\n", "d,e,f\r\n")
      )
    }
  }

  describe("ReaderLineReader and SourceLineReader") {

    it("should agree on every combination of terminator characters") {
      allInputs.foreach { input =>
        withClue("input \"" + escape(input) + "\": ") {
          drain(new SourceLineReader(Source.fromString(input))) should be(
            drain(new ReaderLineReader(new StringReader(input)))
          )
        }
      }
    }

    // The buffer boundary is where a CRLF pair can be split across two refills,
    // so a small buffer puts every terminator against it in turn.
    it("should split identically whatever the buffer size") {
      List(2, 3, 4, 5, 8, 16).foreach { bufferSize =>
        allInputs.foreach { input =>
          val expected = drain(new ReaderLineReader(new StringReader(input)))
          withClue("input \"" + escape(input) + "\" at buffer size " + bufferSize + ": ") {
            drain(new ReaderLineReader(new StringReader(input), bufferSize)) should be(expected)
            drain(new SourceLineReader(Source.fromString(input), bufferSize)) should be(expected)
          }
        }
      }
    }

    it("should handle a line longer than the buffer") {
      val long = "x" * 100
      List(2, 8, 16).foreach { bufferSize =>
        using(new ReaderLineReader(new StringReader(long + "\r\n" + long), bufferSize)) { reader =>
          reader.readLineWithTerminator() should be(long + "\r\n")
          reader.readLineWithTerminator() should be(long)
          reader.readLineWithTerminator() should be(null)
        }
      }
    }

  }

  private def drain(reader: LineReader): List[String] = {
    val builder = List.newBuilder[String]
    var line = reader.readLineWithTerminator()
    while (line != null) {
      builder += line
      line = reader.readLineWithTerminator()
    }
    reader.close()
    builder.result()
  }

  private val alphabet = List('a', '\r', '\n', '\u2028', '\u0085')

  /** Every string of up to four characters drawn from `alphabet`. */
  private lazy val allInputs: List[String] = {
    var accumulated = List("")
    var current = List("")
    var length = 1
    while (length <= 4) {
      current = current.flatMap(prefix => alphabet.map(c => prefix + c))
      accumulated = accumulated ::: current
      length += 1
    }
    accumulated
  }

  private def escape(s: String): String = {
    val sb = new StringBuilder
    var i = 0
    while (i < s.length) {
      val c = s.charAt(i)
      if (c == '\r') sb.append("\\r")
      else if (c == '\n') sb.append("\\n")
      else if (c < ' ' || c > '~') sb.append("\\u%04x".format(c.toInt))
      else sb.append(c)
      i += 1
    }
    sb.toString
  }

}
