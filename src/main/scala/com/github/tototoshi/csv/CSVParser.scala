package com.github.tototoshi.csv

import scala.util.parsing.combinator.{RegexParsers}
import java.io.{Reader, InputStream}


protected trait Between extends RegexParsers {

  def between[A](start: String, p: Parser[A], end: String): Parser[A] = start ~> p <~ end

  def between[A](startAndEnd: String, p: Parser[A]): Parser[A] = between(startAndEnd, p, startAndEnd)

}

class CSVParser(val separatorChar: Char = ',',
                val quoteChar: Char = '"')
  extends RegexParsers
  with Between {

  override def skipWhitespace = false

  def cr = "\r"

  def lf = "\n"

  def quote = quoteChar.toString

  def separator = separatorChar.toString

  def record = field ~ rep(separator ~> field) ^^ {
    case head ~ tail => head :: tail
  }

  def name = field

  def field = escaped | nonEscaped

  def escapedQuote = repN(2, quote) ^^ {
    _ => ""
  }

  def escaped = between(quote, rep(textData | separator | crlf | escapedQuote)) ^^ {
    _.mkString
  }

  def nonEscaped = rep(textData) ^^ {
    _.mkString
  }

  def crlf = cr | lf

  def textData = not(separator | quote | crlf) ~> """.""".r

  def parseLine(in: Input): ParseResult[List[String]] = parse(record <~ opt(crlf), in)

}
