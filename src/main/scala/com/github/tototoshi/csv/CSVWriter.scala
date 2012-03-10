package com.github.tototoshi.csv

import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.{ CSVWriter => JCSVWriter }
import java.io.{ File, Writer, FileOutputStream, OutputStreamWriter }

class CSVWriter(writer: Writer) {

  def this(file: File, enc: String = "UTF-8") =
    this(new OutputStreamWriter(new FileOutputStream(file), enc))

  val csvWriter = new JCSVWriter(writer)

  def close() = csvWriter.close()

  def writeAll(allLines: List[List[String]]) =
    csvWriter.writeAll(allLines.map(_.toArray))

  def writeRow(fields: List[String]) =
    csvWriter.writeNext(fields.toArray)

}
