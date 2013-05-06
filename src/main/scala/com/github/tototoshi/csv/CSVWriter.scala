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

import java.io._

class CSVWriter (protected val writer: Writer)(implicit val format: CSVFormat) {

  val printWriter: PrintWriter = new PrintWriter(writer)

  @deprecated("No longer supported","0.8.0")
  def apply[A](f: CSVWriter => A): A = {
    try {
      f(this)
    } finally {
      this.close()
    }
  }

  def close(): Unit = printWriter.close()

  def flush(): Unit = printWriter.flush()

  private def writeNext(fields: Seq[Any]): Unit = {

    def shouldQuote(field: String, quoting: Quoting): Boolean =
      quoting match {
        case QUOTE_ALL => true
        case QUOTE_MINIMAL => {
          List("\r", "\n", format.quoteChar.toString, format.delimiter.toString).exists(field.contains)
        }
        case QUOTE_NONE => false
        case QUOTE_NONNUMERIC => {
          if (field.forall(_.isDigit)) {
            false
          } else {
            val firstCharIsDigit = field.headOption.map(_.isDigit).getOrElse(false)
            if (firstCharIsDigit && (field.filterNot(_.isDigit) == ".")) {
              true
            } else {
              true
            }
          }
        }
      }

    def quote(field: String): String =
      if (shouldQuote(field, format.quoting)) field.mkString(format.quoteChar.toString, "", format.quoteChar.toString)
      else field

    def repeatQuoteChar(field: String): String =
      field.replace(format.quoteChar.toString, format.quoteChar.toString * 2)

    def escapeDelimiterChar(field: String): String =
      field.replace(format.delimiter.toString, format.escapeChar.toString + format.delimiter.toString)

    def show(s: Any): String = s.toString

    val renderField = {
      val escape = format.quoting match {
        case QUOTE_NONE => escapeDelimiterChar _
        case _ => repeatQuoteChar _
      }
      quote _ compose escape compose show
    }

    printWriter.print(fields.map(renderField).mkString(format.delimiter.toString))
    printWriter.print(format.lineTerminator)
  }

  def writeAll(allLines: Seq[Seq[Any]]): Unit = {
    allLines.foreach(line => writeNext(line))
    if (printWriter.checkError) {
      throw new java.io.IOException("Failed to write")
    }
  }

  def writeRow(fields: Seq[Any]): Unit = {
    writeNext(fields)
    if (printWriter.checkError) {
      throw new java.io.IOException("Failed to write")
    }
  }
}

object CSVWriter {

  @deprecated("Use #open instead", "0.5.0")
  def apply(file: File, encoding: String = "UTF-8")(implicit format: CSVFormat): CSVWriter =
    open(file, false, encoding)(defaultCSVFormat)

  @deprecated("Use #open instead", "0.5.0")
  def apply(writer: Writer): CSVWriter = open(writer)(defaultCSVFormat)

  def open(writer: Writer)(implicit format: CSVFormat): CSVWriter = {
    new CSVWriter(writer)(format)
  }

  def open(file: File)(implicit format: CSVFormat): CSVWriter = open(file, false, "UTF-8")(format)

  def open(file: File, encoding: String)(implicit format: CSVFormat): CSVWriter = open(file, false, encoding)(format)

  def open(file: File, append: Boolean)(implicit format: CSVFormat): CSVWriter = open(file, append, "UTF-8")(format)

  def open(file: File, append: Boolean, encoding: String)(implicit format: CSVFormat): CSVWriter = {
    val fos = new FileOutputStream(file, append)
    val writer = new OutputStreamWriter(fos, encoding)
    open(writer)(format)
  }

  def open(file: String)(implicit format: CSVFormat): CSVWriter = open(file, false, "UTF-8")(format)

  def open(file: String, encoding: String)(implicit format: CSVFormat): CSVWriter = open(file, false, encoding)(format)

  def open(file: String, append: Boolean)(implicit format: CSVFormat): CSVWriter = open(file, append, "UTF-8")(format)

  def open(file: String, append: Boolean, encoding: String)(implicit format: CSVFormat): CSVWriter =
    open(new File(file), append, encoding)(format)
}
