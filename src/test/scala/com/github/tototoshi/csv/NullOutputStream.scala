package com.github.tototoshi.csv

import java.io.OutputStream

/**
 * An [[OutputStream]] that writes to nowhere
 */
case object NullOutputStream extends OutputStream {
  override def write(b: Int): Unit = {

  }
}
