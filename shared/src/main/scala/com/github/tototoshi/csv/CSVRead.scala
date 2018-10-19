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

import scala.io.Source

class CSVRead(private val lineReader: LineReader)(implicit format: CSVFormat) {

  private val parser = new CSVParser(format)

  def readNext(): Option[List[String]] = {

    @scala.annotation.tailrec
    def parseNext(lineReader: LineReader, leftOver: Option[String] = None): Option[List[String]] = {

      val nextLine = lineReader.readLineWithTerminator()
      if (nextLine == null) {
        if (leftOver.isDefined) {
          throw new MalformedCSVException("Malformed Input!: " + leftOver)
        } else {
          None
        }
      } else {
        val line = leftOver.getOrElse("") + nextLine
        parser.parseLine(line) match {
          case None => {
            parseNext(lineReader, Some(line))
          }
          case result => result
        }
      }
    }

    parseNext(lineReader)
  }

  def foreach(f: Seq[String] => Unit): Unit = iterator.foreach(f)

  def iterator: Iterator[Seq[String]] = new Iterator[Seq[String]] {

    private var _next: Option[Seq[String]] = None

    def hasNext: Boolean = {
      _next match {
        case Some(row) => true
        case None => _next = readNext(); _next.isDefined
      }
    }

    def next(): Seq[String] = {
      _next match {
        case Some(row) => {
          val _row = row
          _next = None
          _row
        }
        case None => readNext().getOrElse(throw new NoSuchElementException("next on empty iterator"))
      }
    }

  }

  def iteratorWithHeaders: Iterator[Map[String, String]] = {
    val headers = readNext()
    headers.map(headers => {
      iterator.map(line => headers.zip(line).toMap)
    }).getOrElse(Iterator())
  }

  def toStreamWithHeaders: Stream[Map[String, String]] = iteratorWithHeaders.toStream

  def toStream: Stream[List[String]] =
    Stream.continually(readNext()).takeWhile(_.isDefined).map(_.get)

  def all(): List[List[String]] = {
    toStream.toList
  }

  def allWithHeaders(): List[Map[String, String]] = {
    allWithOrderedHeaders._2
  }

  def allWithOrderedHeaders(): (List[String], List[Map[String, String]]) = {
    val headers = readNext()
    val data = headers.map(headers => {
      val lines = all()
      lines.map(l => headers.zip(l).toMap)
    })
    (headers.getOrElse(Nil), data.getOrElse(Nil))
  }

  def close(): Unit = {
    lineReader.close()
  }
}

