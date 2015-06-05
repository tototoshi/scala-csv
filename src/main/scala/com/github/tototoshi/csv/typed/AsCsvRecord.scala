package com.github.tototoshi.csv.typed

import shapeless.Nat
import shapeless.Sized

trait AsCsvRecord[Record] {
  type RecordSize <: Nat
  def fields(record: Record): Sized[Seq[String], RecordSize]
}

object AsCsvRecord {
  type Aux[Record, N <: Nat] = AsCsvRecord[Record] { type RecordSize = N }
}