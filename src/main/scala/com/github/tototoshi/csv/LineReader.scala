package com.github.tototoshi.csv

import java.io.{ Closeable, IOException }

trait LineReader extends Closeable {

  @throws[IOException]
  def readLineWithTerminator(): String
}
