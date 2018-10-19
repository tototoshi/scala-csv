package com.github.tototoshi.csv

trait CSVFormat extends Serializable {

  val delimiter: Char

  val quoteChar: Char

  val escapeChar: Char

  val lineTerminator: String

  val quoting: Quoting

  val treatEmptyLineAsNil: Boolean

}
