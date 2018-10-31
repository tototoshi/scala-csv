package com.github.tototoshi.csv

trait PrintWriter {
  def print(str: String)
  def print(str: Char)
  def checkError(): Boolean
}
