package com.github.tototoshi.csv

import java.io.{ BufferedReader, Reader }

class ReaderLineReader(reader: Reader) extends LineReader {

  private val bufferedReader = new BufferedReader(reader)

  override def readLineWithTerminator(): String = {
    val sb = new StringBuilder
    while ({
      val c = bufferedReader.read()

      if (c == -1) {
        if (sb.isEmpty) return null
        else return sb.toString
      }

      sb.append(c.toChar)

      if (c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085') {
        return sb.toString
      }

      if (c == '\r') {
        bufferedReader.mark(1)
        val next = bufferedReader.read()
        if (next == -1) {
          return sb.toString
        } else if (next == '\n') {
          sb.append('\n')
        } else {
          bufferedReader.reset()
        }
        return sb.toString
      }

      true
    }) {}
    sb.toString
  }

  override def close(): Unit = {
    bufferedReader.close()
    reader.close()
  }
}
