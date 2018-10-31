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

class CSVWrite(protected val printWriter: PrintWriter)(implicit val format: CSVFormat) {

  private[this] val quoteMinimalSpecs = Array('\r', '\n', format.quoteChar, format.delimiter)

  def writeAll(allLines: Seq[Seq[Any]]): Unit = {
    allLines.foreach(line => writeNext(line))
    if (printWriter.checkError) {
      throw new java.io.IOException("Failed to write")
    }
  }

  private def writeNext(fields: Seq[Any]): Unit = {

    def shouldQuote(field: String, quoting: Quoting): Boolean =
      quoting match {
        case QUOTE_ALL => true
        case QUOTE_MINIMAL =>
          var i = 0
          while (i < field.length) {
            val char = field(i)
            var j = 0
            while (j < quoteMinimalSpecs.length) {
              val quoteSpec = quoteMinimalSpecs(j)
              if (quoteSpec == char) {
                return true
              }
              j += 1
            }
            i += 1
          }
          false
        case QUOTE_NONE => false
        case QUOTE_NONNUMERIC =>
          var foundDot = false
          var i = 0
          while (i < field.length) {
            val char = field(i)
            if (char == '.') {
              if (foundDot) {
                return true
              } else {
                foundDot = true
              }
            } else if (char < '0' || char > '9') {
              return true
            }
            i += 1
          }
          false
      }

    def printField(field: String): Unit =
      if (shouldQuote(field, format.quoting)) {
        printWriter.print(format.quoteChar)
        var i = 0
        while (i < field.length) {
          val char = field(i)
          if (char == format.quoteChar || (format.quoting == QUOTE_NONE && char == format.delimiter)) {
            printWriter.print(format.quoteChar)
          }
          printWriter.print(char)
          i += 1
        }
        printWriter.print(format.quoteChar)
      } else {
        printWriter.print(field)
      }

    val iterator = fields.iterator
    var hasNext = iterator.hasNext
    while (hasNext) {
      val next = iterator.next()
      if (next != null) {
        printField(next.toString)
      }
      hasNext = iterator.hasNext
      if (hasNext) {
        printWriter.print(format.delimiter)
      }
    }

    printWriter.print(format.lineTerminator)
  }

  def writeRow(fields: Seq[Any]): Unit = {
    writeNext(fields)
    if (printWriter.checkError) {
      throw new java.io.IOException("Failed to write")
    }
  }
}
