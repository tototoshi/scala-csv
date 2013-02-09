package com.github.tototoshi.csv

import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.{ CSVWriter => JCSVWriter }
import java.io.{ File, Writer, FileOutputStream, OutputStreamWriter }

class CSVWriter protected (writer: Writer) {

  val csvWriter = new JCSVWriter(writer)

  def close() = csvWriter.close()

  def writeAll(allLines: List[List[String]]) =
    csvWriter.writeAll(allLines.map(_.toArray))

  def writeRow(fields: List[String]) =
    csvWriter.writeNext(fields.toArray)

}

object CSVWriter {

  def apply(writer: Writer): CSVWriter = new CSVWriter(writer)

  def apply(file: File, enc: String = "UTF-8"): CSVWriter = {
    val fos = new FileOutputStream(file)
    val writer = new OutputStreamWriter(fos, enc)
    apply(writer)
  }

}
