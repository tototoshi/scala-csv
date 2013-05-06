package com.github.tototoshi.csv

/**
 * Created with IntelliJ IDEA.
 * User: toshi
 * Date: 2013/05/06
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
sealed abstract trait Quoting
case object QUOTE_ALL extends Quoting
case object QUOTE_MINIMAL extends Quoting
case object QUOTE_NONE extends Quoting
case object QUOTE_NONNUMERIC extends Quoting