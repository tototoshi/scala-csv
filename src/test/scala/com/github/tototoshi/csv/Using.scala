/*
* Copyright 2013 Toshiyuki Takahashi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.github.tototoshi.csv

import java.io.Closeable
import scala.io.BufferedSource

trait CanClose[A] {
  def close(a: A): Unit
}

object CanClose {
  def instance[A](f: A => Unit): CanClose[A] =
    new CanClose[A] {
      override def close(a: A) = f(a)
    }

  implicit def closeable[A <: Closeable]: CanClose[A] =
    instance(_.close())

  implicit val source: CanClose[BufferedSource] =
    instance(_.close())
}

protected trait Using {

  def using[A, R](r: R)(f: R => A)(implicit c: CanClose[R]): A =
    try { f(r) } finally { c.close(r) }

}

