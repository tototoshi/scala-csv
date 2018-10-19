package com.github.tototoshi.csv

trait LineReader {
  def readLineWithTerminator(): String
  def close(): Unit
}
