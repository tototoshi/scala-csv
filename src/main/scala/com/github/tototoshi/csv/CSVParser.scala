/*
* Copyright 2013 Toshiyuki Takahashi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.tototoshi.csv

import scala.util.parsing.combinator.RegexParsers


protected trait Between extends RegexParsers {

  def between[A, B, C](start: Parser[A], p: Parser[B], end: Parser[C]): Parser[B] = start ~> p <~ end

  def between[A, B](startAndEnd: Parser[A], p: Parser[B]): Parser[B] = between(startAndEnd, p, startAndEnd)

}


class CSVParserException(msg: String) extends Exception(msg)

class CSVParser(format: CSVFormat)
  extends RegexParsers
  with Between {

  override def skipWhitespace = false

  def eof: Parser[String] = """\z""".r

  def newLine: Parser[String] = """\r\n|[\n\r\u2028\u2029\u0085]""".r

  def escape: Parser[String] = format.escapeChar.toString

  def quote: Parser[String] = format.quoteChar.toString

  def delimiter: Parser[String] = format.delimiter.toString

  def emptyLine: Parser[List[String]] = (newLine | eof) ^^ { _ => Nil }

  def nonEmptyLine: Parser[List[String]] = rep1sep(field, delimiter) <~ (newLine | eof)

  def record: Parser[List[String]] = if (format.treatEmptyLineAsNil) {
    emptyLine | nonEmptyLine
  } else {
    nonEmptyLine
  }

  def field: Parser[String] = format.quoting match {
    case QUOTE_NONE => {
      def textData: Parser[String] = escape ~> (""".""".r | newLine) | not(delimiter) ~> """.""".r

      rep(textData) ^^ { _.mkString }
    }
    case QUOTE_ALL | QUOTE_MINIMAL | QUOTE_NONNUMERIC => {

      def textData: Parser[String] = not(delimiter | quote | newLine) ~> """.""".r

      def escapedQuote: Parser[String] = repN(2, quote) ^^ {
        _ => format.quoteChar.toString
      }

      def escaped = between(quote, rep(textData | delimiter | newLine | escapedQuote)) ^^ {
        _.mkString
      }

      def nonEscaped = rep(textData) ^^ {
        _.mkString
      }

      escaped | nonEscaped
    }
  }

  def parseLine(in: Input): ParseResult[List[String]] = {
    parse(record, in)
  }

}
