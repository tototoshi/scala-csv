package com.github.tototoshi.csv

import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.{ CSVWriter => JCSVWriter }
import java.io.Writer

class CSVWriter(writer: Writer) {

  val csvWriter = new JCSVWriter(writer)

  def close() = csvWriter.close()

  def writeAll(allLines: List[List[String]]) =
    csvWriter.writeAll(allLines.map(_.toArray))

  def writeNext(fields: List[String]) =
    csvWriter.writeNext(fields.toArray)

}
