package com.github.tototoshi.csv.util

import org.scalatest.FunSpec
import org.scalatest.matchers._

/**
 * Created by jdesjardins on 7/28/14.
 */
class UtilSpec extends FunSpec with ShouldMatchers {

  describe("UtilSpec") {

    it("reorder") {
      List(1, 2, 3, 4, 5).reorder(List(0, 2, 4, 1, 3)) should be equals (List(1, 3, 5, 2, 4))
      List(true, 'a', "564", 'a).reorder(List(3, 0, 1, 2)) should be equals (List('a, true, 'a', "564"))
    }
  }

}
