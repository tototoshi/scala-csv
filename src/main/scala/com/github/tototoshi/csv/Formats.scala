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

trait DefaultCSVFormat extends CSVFormat {

  val delimiter: Char = ','

  val quoteChar: Char = '"'

  val escapeChar: Char = '"'

  val lineTerminator: String = "\r\n"

  val quoting: Quoting = QUOTE_MINIMAL

  val treatEmptyLineAsNil: Boolean = false

  val trimUnquoted: Boolean = false

}

trait TSVFormat extends CSVFormat {

  val delimiter: Char = '\t'

  val quoteChar: Char = '"'

  val escapeChar: Char = '\\'

  val lineTerminator: String = "\r\n"

  val quoting: Quoting = QUOTE_NONE

  val treatEmptyLineAsNil: Boolean = false

  val trimUnquoted: Boolean = false

}

