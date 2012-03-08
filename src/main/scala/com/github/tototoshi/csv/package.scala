package com.github.tototoshi

package object csv {
  def using[A, R <: { def close() }](r : R)(f : R => A) : A =
    try { f(r) } finally { r.close() }
}
