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
import scala.util.parsing.input.CharSequenceReader
import scala.reflect.runtime.universe._

trait Error
case object EmptyFile extends Error
case class Mismatch(found: String, expected: String)
case class HeaderMismatch(headers: List[Mismatch]) extends Error {
  override def toString() =
    headers.map { case Mismatch(found, expected) =>
      s"Found: $found but expected: $expected"
    }.mkString("\n")
}
case class ValueParseError(value: String, type_ : Type) extends Error {
  override def toString() = s"Was unable to convert String: $value into value of type: $type_"
}

class CSVReader protected (private val reader: Reader)(implicit format: CSVFormat) {

  val delimiter: Char = format.delimiter
  val quoteChar: Char = format.quoteChar

  private val parser = new CSVParser(format)

  private val lineReader = new LineReader(reader)

  def readNext(): Option[List[String]] = {

    @scala.annotation.tailrec
    def parseNext(lineReader: LineReader, leftOver: Option[String] = None, failureMsg: Option[String] = None): Option[List[String]] = {

      var nextLine = lineReader.readLineWithTerminator()

      if (nextLine == null) {
        if (leftOver.isDefined) {
          throw new MalformedCSVException(failureMsg.getOrElse(""))
        } else {
          None
        }
      } else {
        val line = leftOver.getOrElse("") + nextLine
        parser.parseLine(new CharSequenceReader(line, 0)) match {
          case parser.Success(result, _) => Some(result)
          case parser.Failure(msg, _) => {
            parseNext(lineReader, Some(line), Some(msg))
          }
          case parser.Error(msg, _) => throw new CSVParserException(msg)
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

  def all(): List[List[String]] = {
    toStream().toList
  }

  def allOf[T: TypeTag]: Either[Error, List[T]] = {
    val almost = toStream().map(convertTo[T](_, typeOf[T])).toList
    val r = almost.find(_.isLeft)
    if (r.isEmpty) Right(almost.map(_.right.toOption).flatten)
    else Left(r.get.left.get)
  }

  private def convertTo[T](list: List[String], t: Type): Either[Error, T] = {
    val constructor = t.decl(termNames.CONSTRUCTOR).asMethod
    val paramTypes = constructor.typeSignatureIn(t).paramLists.flatten.map(_.typeSignature)
    require(paramTypes.forall { t =>
      t <:< typeOf[AnyVal] || t <:< typeOf[String] && !(t =:= typeOf[Unit] || t =:= typeOf[Char])
    })
    val values = paramTypes.zip(list).map { case (t, unparsed) =>
      import scala.util.Try
      val input = unparsed.trim
      def safeParse(conv: String => Any) = Try(conv(input))
      if (t =:= typeOf[String]) input
      else if (t =:= typeOf[Int]) safeParse(_.toInt).getOrElse(return Left(ValueParseError(input, t)))
      else if (t =:= typeOf[Double]) safeParse(_.toDouble).getOrElse(return Left(ValueParseError(input, t)))
      else if (t =:= typeOf[Float]) safeParse(_.toFloat).getOrElse(return Left(ValueParseError(input, t)))
      else if (t =:= typeOf[Boolean]) safeParse(_.toBoolean).getOrElse(return Left(ValueParseError(input, t)))
      else if (t =:= typeOf[Byte]) safeParse(_.toByte).getOrElse(return Left(ValueParseError(input, t)))
      else if (t =:= typeOf[Long]) safeParse(_.toLong).getOrElse(return Left(ValueParseError(input, t)))
      else {
        assert(t =:= typeOf[Short]); safeParse(_.toShort).getOrElse(return Left(ValueParseError(input, t)))
      }
    }
    val class_ = t.typeSymbol.asClass
    val mirror = runtimeMirror(getClass().getClassLoader)
    val classMirror = mirror.reflectClass(class_)
    val constructorMirror = classMirror.reflectConstructor(constructor)
    Right(constructorMirror.apply(values: _*).asInstanceOf[T])
  }

  def allWithHeaders(): List[Map[String, String]] = {
    readNext() map { headers =>
      val lines = all()
      lines.map(l => headers.zip(l).toMap)
    } getOrElse List()
  }

  def allOfWithHeaders[T: TypeTag](validate: Boolean = true, caseSensitive: Boolean = false): Either[Error, List[T]] = {
    val t = typeOf[T]
    val headers = readNext()
    if (validate) {
      headers map { headers =>
        val constructor = t.decl(termNames.CONSTRUCTOR).asMethod
        val paramNames = constructor.paramLists.flatten.map(_.name.toString.trim)
        val mismatchedHeaders = paramNames.zip(headers).filter { case (paramName, header) =>
          if (caseSensitive) paramName != header
          else paramName.toLowerCase != header.toLowerCase
        }
        if (mismatchedHeaders.isEmpty)  allOf[T]
        else Left(HeaderMismatch(mismatchedHeaders.map{ case (paramName, header) => Mismatch(header, paramName)}))
      } getOrElse Left(EmptyFile)
    }
    else if (headers.isEmpty) Left(EmptyFile) else allOf[T]
  }

  def close(): Unit = {
    lineReader.close()
    reader.close()
  }

}

object CSVReader {

  val DEFAULT_ENCODING = "UTF-8"

  def open(reader: Reader)(implicit format: CSVFormat): CSVReader =
    new CSVReader(reader)(format)

  def open(file: File)(implicit format: CSVFormat): CSVReader = {
    open(file, this.DEFAULT_ENCODING)(format)
  }

  def open(file: File, encoding: String)(implicit format: CSVFormat): CSVReader = {
    val fin = new FileInputStream(file)
    try {
      val reader = new InputStreamReader(fin, encoding)
      open(reader)(format)
    } catch {
      case e: UnsupportedEncodingException => fin.close(); throw e
    }
  }

  def open(filename: String)(implicit format: CSVFormat) : CSVReader =
    open(new File(filename), this.DEFAULT_ENCODING)(format)

  def open(filename: String, encoding: String)(implicit format: CSVFormat) : CSVReader =
    open(new File(filename), encoding)(format)

}
