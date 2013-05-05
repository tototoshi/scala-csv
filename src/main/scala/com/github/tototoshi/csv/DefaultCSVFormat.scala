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

import au.com.bytecode.opencsv.{ CSVParser, CSVReader => JCSVReader }

trait DefaultCSVFormat extends CSVFormat {

  val separator: Char = CSVParser.DEFAULT_SEPARATOR

  val quote: Char = CSVParser.DEFAULT_QUOTE_CHARACTER

  val escape: Char = CSVParser.DEFAULT_ESCAPE_CHARACTER

  val numberOfLinesToSkip: Int = JCSVReader.DEFAULT_SKIP_LINES

  val strictQuotes: Boolean = CSVParser.DEFAULT_STRICT_QUOTES

  val ignoreLeadingWhiteSpace: Boolean = CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE

}
