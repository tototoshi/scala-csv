package com.github.tototoshi.csv

import java.io.Writer

package object typed {

  implicit class AsCsvOps[Whole](val whole: Whole)(implicit wholeAsCsv: AsCsv[Whole]) {
    def asCsvInto(writer: Writer): Unit = {
      val csvWriter = CSVWriter.open(writer)
      csvWriter.writeRow(wholeAsCsv.header)
      for (record <- wholeAsCsv.records(whole)) {
        csvWriter.writeRow(wholeAsCsv.asCsvRecord.fields(record))
      }
    }
  }
}
