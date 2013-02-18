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

import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.{ CSVWriter => JCSVWriter }
import java.io.{ File, Writer, FileOutputStream, OutputStreamWriter }

class CSVWriter protected (writer: Writer) {

  private val underlying: JCSVWriter = new JCSVWriter(writer)

  def close(): Unit = underlying.close()

  def flush(): Unit = underlying.close()

  def writeAll(allLines: Seq[Seq[Any]]): Unit =
    underlying.writeAll(allLines.map(_.toArray.map(_.toString)))

  def writeRow(fields: Seq[Any]): Unit =
    underlying.writeNext(fields.map(_.toString).toArray)

}

object CSVWriter {

  @deprecated("Use #open instead", "0.5.0")
  def apply(file: File, enc: String = "UTF-8"): CSVWriter = open(file, false, enc)

  @deprecated("Use #open instead", "0.5.0")
  def apply(writer: Writer): CSVWriter = open(writer)

  def open(writer: Writer): CSVWriter = new CSVWriter(writer)

  def open(file: File): CSVWriter = open(file, false, "UTF-8")

  def open(file: File, enc: String): CSVWriter = open(file, false, enc)

  def open(file: File, append: Boolean): CSVWriter = open(file, append, "UTF-8")

  def open(file: File, append: Boolean, enc: String): CSVWriter = {
    val fos = new FileOutputStream(file, append)
    val writer = new OutputStreamWriter(fos, enc)
    open(writer)
  }

  def open(file: String): CSVWriter = open(file, false, "UTF-8")

  def open(file: String, enc: String): CSVWriter = open(file, false, enc)

  def open(file: String, append: Boolean): CSVWriter = open(file, append, "UTF-8")

  def open(file: String, append: Boolean, enc: String): CSVWriter = open(new File(file), append, enc)
}
