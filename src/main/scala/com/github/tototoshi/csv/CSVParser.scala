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

class CSVParserException(msg: String) extends Exception(msg)

class CSVParser(format: CSVFormat) {

  private val startsWithNewLineRegexp = """^\r\n|[\n\r\u2028\u2029\u0085]""".r

  private def startsWithNewLine(s: String): Boolean =
    (s.size > 1 && s.charAt(0) == '\r' && s.charAt(1) == '\n') ||
  (!s.isEmpty &&
      (s.charAt(0) == '\n' ||
        s.charAt(0) == '\r' ||
        s.charAt(0) == '\u2028' ||
        s.charAt(0) == '\u2029' ||
        s.charAt(0) == '\u0085'))

  private def ltrimNewLine(s: String): String =
    if (s.size > 1 && s.charAt(0) == '\r' && s.charAt(1) == '\n') {
      s.substring(2)
    } else if (!s.isEmpty &&
      (s.charAt(0) == '\n' ||
        s.charAt(0) == '\r' ||
        s.charAt(0) == '\u2028' ||
        s.charAt(0) == '\u2029' ||
        s.charAt(0) == '\u0085')) {
      s.substring(1)
    } else {
      s
    }

  private lazy val fieldRegexpForQuoted =
    ("""(?m)^[""" + format.quoteChar + """](([^""" + format.quoteChar + """]|[""" + format.quoteChar + """][""" + format.quoteChar + """])*)[""" + format.quoteChar + """]""").r

  private lazy val fieldRegexpForNonQuoted =
    if (format.escapeChar == '\\') {
      ("""(?m)^(([^ """ + format.delimiter + """\n\r\u2028\u2029\u0085]|\\[""" + format.delimiter + """])*)""").r
    } else {
      ("""(?m)^(([^""" + format.delimiter + """\n\r\u2028\u2029\u0085]|[""" + format.escapeChar + """][""" + format.delimiter + """])*)""").r
    }

  def parseLine(input: String): Option[List[String]] = {
    def getFieldRegexp(quoted: Boolean) =
      if (quoted)
        fieldRegexpForQuoted
      else
        fieldRegexpForNonQuoted

    var buf: String = input
    var fields: Vector[String] = Vector()

    try {
      while (!buf.isEmpty) {
        val quoted = buf.charAt(0) == format.quoteChar
        val fieldRegexp = getFieldRegexp(quoted)
        val m = fieldRegexp.findFirstMatchIn(buf)
        m match {
          case Some(s) => {
            val field =
              if (quoted) {
                s.group(1).replace("""""""", """"""")
              } else {
                s.group(1)
              }
            buf = buf.substring(field.size + (if (quoted) 2 /* remove quoteChar*/ else 0))
            fields :+= field
            if (buf.isEmpty) {
              // do nothing
            } else if (startsWithNewLine(buf)) {
              buf = ltrimNewLine(buf)
            } else {
              // remove delimiter
              buf = buf.substring(1)
            }
          }
          case _ => throw new CSVParserException("Failed to parse: " + buf + "...")
        }
      }
      if (fields.size == 1 && fields(0) == "") {
        if (format.treatEmptyLineAsNil) Some(Nil)
        else Some(List(""))
      } else {
        Some(fields.toList)
      }
    } catch {
      case e: CSVParserException =>
        None
    }
  }

}
