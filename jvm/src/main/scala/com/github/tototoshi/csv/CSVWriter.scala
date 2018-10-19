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

import java.io
import java.io._

class CSVWriter(writer: java.io.PrintWriter)(implicit format: CSVFormat) extends CSVWrite(new PrintWriter {

  override def print(str: String): Unit = writer.print(str)

  override def print(str: Char): Unit = writer.print(str)

  override def checkError(): Boolean = writer.checkError()
})(format) with Closeable with Flushable {
  override def close(): Unit = writer.close()

  override def flush(): Unit = writer.flush()
}

object CSVWriter {

  def open(file: File)(implicit format: CSVFormat): CSVWriter = open(file, false, "UTF-8")(format)

  def open(file: File, encoding: String)(implicit format: CSVFormat): CSVWriter = open(file, false, encoding)(format)

  def open(file: File, append: Boolean)(implicit format: CSVFormat): CSVWriter = open(file, append, "UTF-8")(format)

  def open(fos: OutputStream)(implicit format: CSVFormat): CSVWriter = open(fos, "UTF-8")(format)

  def open(file: String)(implicit format: CSVFormat): CSVWriter = open(file, false, "UTF-8")(format)

  def open(file: String, encoding: String)(implicit format: CSVFormat): CSVWriter = open(file, false, encoding)(format)

  def open(file: String, append: Boolean)(implicit format: CSVFormat): CSVWriter = open(file, append, "UTF-8")(format)

  def open(file: String, append: Boolean, encoding: String)(implicit format: CSVFormat): CSVWriter =
    open(new File(file), append, encoding)(format)

  def open(file: File, append: Boolean, encoding: String)(implicit format: CSVFormat): CSVWriter = {
    val fos = new FileOutputStream(file, append)
    open(fos, encoding)(format)
  }

  def open(fos: OutputStream, encoding: String)(implicit format: CSVFormat): CSVWriter = {
    try {
      val writer = new OutputStreamWriter(fos, encoding)
      open(writer)(format)
    } catch {
      case e: UnsupportedEncodingException => fos.close(); throw e
    }
  }

  def open(writer: Writer)(implicit format: CSVFormat): CSVWriter = {
    new CSVWriter(new io.PrintWriter(writer))(format)
  }
}
