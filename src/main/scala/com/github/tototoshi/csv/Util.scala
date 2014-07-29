package com.github.tototoshi.csv

/**
 * Created by jdesjardins on 7/28/14.
 */
package object util {

  implicit class AugmentedSeq[T](inner: Seq[T]) {
    def reorder(ordering: Seq[Int]) = {
      ordering.map(inner(_))
    }
  }

}
