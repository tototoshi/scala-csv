package com.github.tototoshi.csv

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader

class ReaderLineReader(baseReader: Reader) extends LineReader {
  private val bufferedReader: BufferedReader = new BufferedReader(baseReader)

  @throws[IOException]
  def readLineWithTerminator(): String = {
    val sb = new StringBuilder()
    var c: Int = 0

    while (c != -1) {
      c = bufferedReader.read()
      if (c != -1) sb.append(c.toChar)
      if (c == '\n' || c == '\u2028' || c == '\u2029' || c == '\u0085') {
        c = -1
      }
      if (c == '\r') {
        bufferedReader.mark(1)
        c = bufferedReader.read()
        if (c == '\n') {
          sb.append('\n')
        } else {
          bufferedReader.reset()
        }
        c = -1
      }
    }
    if (sb.isEmpty) null else sb.toString()
  }

  @throws[IOException]
  def close(): Unit = {
    bufferedReader.close()
    baseReader.close()
  }
}
