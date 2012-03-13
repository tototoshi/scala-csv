package com.github.tototoshi.csv

import au.com.bytecode.opencsv.{ CSVReader => JCSVReader }
import java.io._
import scala.collection.JavaConversions._

class CSVReader(reader: Reader) {

  def this(file: File, enc: String = "UTF-8") =
    this(new InputStreamReader(new FileInputStream(file), enc))

  val csvReader = new JCSVReader(reader)

  def readNext(): Option[List[String]] = Option(csvReader.readNext).map(_.toList)

  def foreach(f: List[String] => Unit): Unit = {
    readNext match {
      case Some(next) => f(next); foreach(f)
      case None => ()
    }
  }

  def all(): List[List[String]] = {
    csvReader.readAll().map(_.toList).toList
  }

  def close(): Unit = csvReader.close()

}
