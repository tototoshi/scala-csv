package com.github.tototoshi.csv

import java.io.Closeable
import java.io.IOException

trait LineReader extends Closeable {
  @throws[IOException]
  def readLineWithTerminator(): String
}
