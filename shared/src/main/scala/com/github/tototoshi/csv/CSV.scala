package com.github.tototoshi.csv

object CSV {

  def read(input: String)(implicit format: CSVFormat): List[List[String]] = {
    val lineReader = new LineReader {
      val lines = input.linesIterator
      override def readLineWithTerminator(): String = lines.hasNext match {
        case true => lines.next()
        case false => null
      }

      override def close(): Unit = {} //nothing to do
    }
    new CSVRead(lineReader).all()
  }

  private def writer(action: CSVWrite => Unit)(implicit format: CSVFormat): String = {
    val stringBuilder = StringBuilder.newBuilder

    val writer = new PrintWriter {
      override def print(str: String): Unit = stringBuilder ++= str

      override def print(str: Char): Unit = stringBuilder + str

      override def checkError(): Boolean = false
    }
    val csvWrite = new CSVWrite(writer)
    action(csvWrite)
    stringBuilder.result()
  }

  def writeAll(input: Seq[Seq[String]])(implicit format: CSVFormat): String = writer(_.writeAll(input))

  def writeRow(input: Seq[String])(implicit format: CSVFormat): String = writer(_.writeRow(input))

}
