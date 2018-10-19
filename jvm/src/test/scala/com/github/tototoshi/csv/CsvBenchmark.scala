package com.github.tototoshi.csv

import org.scalameter.api._
import org.scalameter.picklers.Pickler

object CsvBenchmark extends Bench.LocalTime {

  val row = Seq("a", "1", "\"hey\"", "world")
  private[this] val sizes = Gen.exponential("size")(10000, 100000, 10)
  private[this] val quotings = {
    val quotings = Seq(QUOTE_ALL, QUOTE_MINIMAL, QUOTE_NONE, QUOTE_NONNUMERIC)

    implicit val pickler = new Pickler[Quoting] {
      override def pickle(x: Quoting): Array[Byte] = {
        x match {
          case QUOTE_ALL => Array[Byte](0)
          case QUOTE_MINIMAL => Array[Byte](1)
          case QUOTE_NONE => Array[Byte](2)
          case QUOTE_NONNUMERIC => Array[Byte](3)
        }
      }

      override def unpickle(a: Array[Byte], from: Int): (Quoting, Int) = {
        val quoting = a(0) match {
          case 0 => QUOTE_ALL
          case 1 => QUOTE_MINIMAL
          case 2 => QUOTE_NONE
          case 3 => QUOTE_NONNUMERIC
        }

        (quoting, from)
      }
    }

    Gen.enumeration("quoting")(quotings: _*)
  }

  private case class QuotingCsvFormat(override val quoting: Quoting) extends DefaultCSVFormat

  performance of "CSV" in {

    measure method "write" in {
      using(Gen.crossProduct(quotings, sizes)) in {
        kvp =>
          implicit val format = QuotingCsvFormat(kvp._1)
          val size = kvp._2

          var i = 0
          val writer = CSVWriter.open(NullOutputStream)
          while (i < size) {
            writer.writeRow(row)
            i += 1
          }
          writer.close()
      }
    }

  }

}
