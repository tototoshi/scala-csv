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
import java.util.NoSuchElementException

import scala.io.Source

object CSVReader {

  val DEFAULT_ENCODING = "UTF-8"

  def open(source: Source)(implicit format: CSVFormat): CSVRead = new CSVRead(new SourceLineReader(source))(format)

  def open(reader: Reader)(implicit format: CSVFormat): CSVRead = new CSVRead(new ReaderLineReader(reader))(format)

  def open(file: File)(implicit format: CSVFormat): CSVRead = {
    open(file, this.DEFAULT_ENCODING)(format)
  }

  def open(file: File, encoding: String)(implicit format: CSVFormat): CSVRead = {
    val fin = new FileInputStream(file)
    try {
      open(new InputStreamReader(fin, encoding))(format)
    } catch {
      case e: UnsupportedEncodingException => fin.close(); throw e
    }
  }

  def open(filename: String)(implicit format: CSVFormat): CSVRead =
    open(new File(filename), this.DEFAULT_ENCODING)(format)

  def open(filename: String, encoding: String)(implicit format: CSVFormat): CSVRead =
    open(new File(filename), encoding)(format)

}
