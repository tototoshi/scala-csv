package com.github.tototoshi.csv

import scala.collection.JavaConversions._
import java.io._
import au.com.bytecode.opencsv.{ CSVReader => JCSVReader }

class CSVReader(reader: Reader) {

  def this(file: File, enc: String = "UTF-8") =
    this(new InputStreamReader(new FileInputStream(file), enc))

  val csvReader = new JCSVReader(reader)

  def foreach(f: List[String] => Unit): Unit = {
    var fields: Array[String] = null
    while ({ fields = csvReader.readNext(); fields } != null) {
      f(fields.toList)
    }
  }

  def all(): List[List[String]] = {
    csvReader.readAll().map(_.toList).toList
  }

  def close(): Unit = csvReader.close()

}
