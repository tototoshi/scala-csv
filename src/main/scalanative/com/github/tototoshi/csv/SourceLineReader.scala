package com.github.tototoshi.csv

import java.io.IOException
import scala.io.Source

class SourceLineReader(source: Source) extends LineReader {

  private var buffer: Int = -1

  @throws[IOException]
  def readLineWithTerminator(): String = {
    val sb = new StringBuilder()
    var c: Int = 0

    while (c != -1) {
      if (buffer != -1) {
        c = buffer
        buffer = -1
      } else {
        c = if (source.hasNext) source.next() else -1
      }
      if (c != -1) sb.append(c.toChar)
      if (c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085') {
        c = -1
      }
      if (c == '\r') {
        buffer = if (source.hasNext) source.next() else -1
        if (buffer == '\n') {
          sb.append('\n')
          buffer = -1
        }
        c = -1
      }
    }
    if (sb.isEmpty) null else sb.toString()
  }

  @throws[IOException]
  override def close(): Unit = source.close()
}
