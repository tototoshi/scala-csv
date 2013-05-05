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

import au.com.bytecode.opencsv.{CSVReader => JCSVReader}
import java.io._
import scala.collection.JavaConversions._
import java.util.NoSuchElementException
import au.com.bytecode.opencsv
import opencsv.CSVParser

class CSVReader protected (private val underlying: JCSVReader) {

  def apply[A](f: Iterator[Seq[String]] => A): A = {
    try {
      f(this.iterator)
    } finally {
      this.close()
    }
  }

  def readNext(): Option[List[String]] = Option(underlying.readNext).map(_.toList)

  def foreach(f: Seq[String] => Unit): Unit = iterator.foreach(f)

  def iterator: Iterator[Seq[String]] = new Iterator[Seq[String]] {

    private var _next: Option[Seq[String]] = None

    def hasNext: Boolean = {
      _next match {
        case Some(row) => true
        case None => { _next = readNext;  _next.isDefined }
      }
    }

    def next(): Seq[String] = {
      _next match {
        case Some(row) => {
          val _row = row
          _next = None
          _row
        }
        case None => readNext.getOrElse(throw new NoSuchElementException("next on empty iterator"))
      }
    }

  }

  def toStream(): Stream[List[String]] =
    Stream.continually(readNext).takeWhile(_.isDefined).map(_.get)

  def all(): List[List[String]] =
    underlying.readAll().map(_.toList).toList

  def allWithHeaders(): List[Map[String, String]] = {
    readNext() map { headers =>
      val lines = all()
      lines.map(l => headers.zip(l).toMap)
    } getOrElse List()
  }

  def close(): Unit = underlying.close()

}

object CSVReader {

  @deprecated("Use #open instead", "0.5.0")
  def apply(file: File, encoding: String = "UTF-8"): CSVReader = open(file, encoding)

  @deprecated("Use #open instead", "0.5.0")
  def apply(reader: Reader): CSVReader = openFromReader(reader)

  def openFromReader(reader: Reader, separator: Char = CSVParser.DEFAULT_SEPARATOR, quote: Char = CSVParser.DEFAULT_QUOTE_CHARACTER, numberOfLinesToSkip: Int = JCSVReader.DEFAULT_SKIP_LINES): CSVReader =
    new CSVReader(new JCSVReader(reader, separator, quote, numberOfLinesToSkip))

  def openFromFile(file: File, encoding: String = "UTF-8", separator: Char = CSVParser.DEFAULT_SEPARATOR, quote: Char = CSVParser.DEFAULT_QUOTE_CHARACTER, numberOfLinesToSkip: Int = JCSVReader.DEFAULT_SKIP_LINES): CSVReader = {
    val fin = new FileInputStream(file)
    val reader = new InputStreamReader(fin, encoding)
    openFromReader(reader, separator, quote, numberOfLinesToSkip)
  }

  def openFromPath(filename: String, encoding: String = "UTF-8", separator: Char = CSVParser.DEFAULT_SEPARATOR, quote: Char = CSVParser.DEFAULT_QUOTE_CHARACTER, numberOfLinesToSkip: Int = JCSVReader.DEFAULT_SKIP_LINES) : CSVReader =
    openFromFile(new File(filename), encoding, separator, quote, numberOfLinesToSkip)

  def open(reader: Reader): CSVReader = openFromReader(reader)

  def open(file: File): CSVReader = open(file, "UTF-8")

  def open(file: File, encoding: String): CSVReader = {
    val fin = new FileInputStream(file)
    val reader = new InputStreamReader(fin, encoding)
    open(reader)
  }

  def open(file: String): CSVReader = open(new File(file), "UTF-8")

  def open(file: String, encoding: String): CSVReader = open(new File(file), encoding)
}
