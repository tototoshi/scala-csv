package com.github.tototoshi.csv.typed

import shapeless.{ Sized, Nat }

trait AsCsv[Whole] {
  type HeaderSize <: Nat
  def header: Sized[Seq[String], HeaderSize]

  type Record
  def asCsvRecord: AsCsvRecord[Record] { type RecordSize = HeaderSize }

  def records(whole: Whole): Seq[Record]
}
