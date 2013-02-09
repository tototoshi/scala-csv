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
