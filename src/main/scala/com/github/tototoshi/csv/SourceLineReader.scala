package com.github.tototoshi.csv

import scala.io.Source

class SourceLineReader(source: Source) extends LineReader {

  private var currentChar: Int = -1

  override def readLineWithTerminator(): String = {
    val sb = new StringBuilder
    while ({
      if (!source.hasNext && currentChar == -1) {
        if (sb.isEmpty) return null
        else return sb.toString
      }
      val c: Int =
        if (currentChar > -1) {
          val ch = currentChar
          currentChar = -1
          ch
        } else {
          source.next().toInt
        }

      sb.append(c.toChar)

      if (c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085') {
        return sb.toString
      }

      if (c == '\r') {
        if (!source.hasNext) return sb.toString
        val next = source.next()
        if (next != '\n') currentChar = next
        return sb.toString
      }

      true
    }) {}
    sb.toString
  }

  override def close(): Unit = source.close()
}
