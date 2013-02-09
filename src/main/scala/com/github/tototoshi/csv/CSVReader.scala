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

import au.com.bytecode.opencsv.{ CSVReader => JCSVReader }
import java.io._
import scala.collection.JavaConversions._

class CSVReader protected (reader: Reader) {

  val csvReader = new JCSVReader(reader)

  def readNext(): Option[List[String]] = Option(csvReader.readNext).map(_.toList)

  def foreach(f: List[String] => Unit): Unit = {
    readNext match {
      case Some(next) => f(next); foreach(f)
      case None => ()
    }
  }

  def toStream(): Stream[List[String]] =
    Stream.continually(readNext).takeWhile(_.isDefined).map(_.get)

  def all(): List[List[String]] =
    csvReader.readAll().map(_.toList).toList

  def close(): Unit = csvReader.close()

}

object CSVReader {

  def apply(reader: Reader): CSVReader = new CSVReader(reader)

  def apply(file: File, enc: String = "UTF-8"): CSVReader = {
    val fin = new FileInputStream(file)
    val reader = new InputStreamReader(fin, enc)
    apply(reader)
  }

}
