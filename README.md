# scala-csv

## build.sbt

```scala
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"
```

## Example

### import

```scala
scala> import com.github.tototoshi.csv._
```

### Reading example

sample.csv
```
a,b,c
d,e,f
```

You can create CSVReader instance with CSVReader#open.

```scala
scala> val reader = CSVReader.open(new File("sample.csv"))
```

#### Reading all lines
```scala
scala> val reader = CSVReader.open(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@36d0c6dd

scala> reader.all()
res0: List[List[String]] = List(List(a, b, c), List(d, e, f))

scala> reader.close()
```

#### Using iterator
```scala
scala> val reader = CSVReader.open("sample.csv")
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@22d568da

scala> val it = reader.iterator
it: Iterator[Seq[String]] = non-empty iterator

scala> it.next
res0: Seq[String] = List(a, b, c)

scala> it.next
res1: Seq[String] = List(d, e, f)

scala> it.next
java.util.NoSuchElementException: next on empty iterator
        at com.github.tototoshi.csv.CSVReader$$anon$1$$anonfun$next$1.apply(CSVReader.scala:55)
        at com.github.tototoshi.csv.CSVReader$$anon$1$$anonfun$next$1.apply(CSVReader.scala:55)
        at scala.Option.getOrElse(Option.scala:108)

scala> reader.close()
```

#### Reading all lines as Stream
```scala
scala> val reader = CSVReader.open(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@7dae76b4

scala> reader.toStream
res7: Stream[List[String]] = Stream(List(a, b, c), ?)
```

#### Reading one line at a time

There a two ways available. #foreach and #readNext.

```scala
scala> val reader = CSVReader.open(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@4720a918

scala> reader.foreach(fields => println(fields))
List(a, b, c)
List(d, e, f)

scala> reader.close()
```

```scala
scala> val reader = CSVReader.open(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@4b545701

scala> reader.readNext()
res3: Option[List[String]] = Some(List(a, b, c))

scala> reader.readNext()
res4: Option[List[String]] = Some(List(d, e, f))

scala> reader.readNext()
res5: Option[List[String]] = None

scala> reader.close()
```

#### Reading a csv file with column headers

with-headers.csv
```
Foo,Bar,Baz
a,b,c
d,e,f
```

````scala
scala> val reader = CSVReader.open(new File("with-headers.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@1a64e307

scala> reader.allWithHeaders()
res0: List[Map[String,String]] = List(Map(Foo -> a, Bar -> b, Baz -> c), Map(Foo -> d, Bar -> e, Baz -> f))
````

### Writing example

#### Writing all lines with #writeAll

```scala
scala> val f = new File("out.csv")

scala> val writer = CSVWriter.open(f)
writer: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@783f77f1

scala> writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))

scala> writer.close()
```


#### Writing one line at a time with #writeRow
```scala
scala> val f = new File("out.csv")

scala> val writer = CSVWriter.open(f)
writer: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@41ad4de1

scala> writer.writeRow(List("a", "b", "c"))

scala> writer.writeRow(List("d", "e", "f"))

scala> writer.close()
```

#### Appending lines to the file that already exists
The default behavior of CSVWriter#open is overwriting.
To append lines to the file that already exists, Set the append flag true.

```scala
scala> val writer = CSVWriter.open("a.csv", append = true)
writer: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@67a84246

scala> writer.writeRow(List("4", "5", "6"))

scala> writer.close()
```

### Customizing the format

CSVReader/Writer#open takes CSVFormat implicitly.
Define your own CSVFormat when you want to change the CSV's format.

```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)

implicit object MyFormat extends DefaultCSVFormat {
  override val delimiter = '#'
}
val w = CSVWriter.open(new java.io.OutputStreamWriter(System.out))

// Exiting paste mode, now interpreting.

defined module MyFormat
w: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@6cd66afa

scala> w.writeRow(List(1, 2, 3))
"1"#"2"#"3"
```
### Changing the encoding
By default the UTF-8 is set. To change it, for example, to ISO-8859-1 you can set it in the CSVReader:
```scala
scala> val reader = CSVReader.open(filepath, "ISO-8859-1")
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@6bcb69ba
```

## Dev

```
$ git clone https://github.com/tototoshi/scala-csv.git
$ cd scala-csv
$ sbt
> test
```

## License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
