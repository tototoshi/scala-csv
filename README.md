# scala-csv

A simple opencsv wrapper

## build.sbt
If you use Scala 2.9.1, 2.9.2 or 2.10.0, you can get scala-csv with the following line in your build.sbt.

```scala
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "0.4.0"
```

## Example

### import

```scala
scala> import com.github.tototoshi.csv.CSVReader
```

### Reading a csv file

sample.csv
```
a,b,c
d,e,f
```

You can create CSVReader instance with CSVReader#apply.

```scala
scala> val reader = CSVReader(new File("sample.csv"))
```

#### Reading all lines
```scala
scala> val reader = CSVReader(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@36d0c6dd

scala> reader.all()
res0: List[List[String]] = List(List(a, b, c), List(d, e, f))

scala> reader.close()
```

#### Reading all lines as Stream
```scala
scala> val reader = CSVReader(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@7dae76b4

scala> reader.toStream
res7: Stream[List[String]] = Stream(List(a, b, c), ?)
```

#### Reading one line at a time

There a two ways available. #foreach and #readNext.

```scala
scala> val reader = CSVReader(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@4720a918

scala> reader.foreach(fields => println(fields))
List(a, b, c)
List(d, e, f)

scala> reader.close()
```

```scala
scala> val reader = CSVReader(new File("sample.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@4b545701

scala> reader.readNext()
res3: Option[List[String]] = Some(List(a, b, c))

scala> reader.readNext()
res4: Option[List[String]] = Some(List(d, e, f))

scala> reader.readNext()
res5: Option[List[String]] = None

scala> reader.close()
```

### Reading a csv file with column headers

with-headers.csv
```
a,b,c
d,e,f
```

````scala
scala> val reader = CSVReader(new File("with-headers.csv"))
reader: com.github.tototoshi.csv.CSVReader = com.github.tototoshi.csv.CSVReader@1a64e307

scala> reader.allWithHeaders()
res0: List[Map[String,String]] = List(Map(Foo -> a, Bar -> b, Baz -> c), Map(Foo -> d, Bar -> e, Baz -> f))
````

### Writing example

#### import
```scala
scala> import com.github.tototoshi.csv.CSVWriter
import com.github.tototoshi.csv.CSVWriter

```

#### Writing all lines with #writeAll

```scala
scala> val f = new File("out.csv")

scala> val writer = CSVWriter(f)
writer: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@783f77f1

scala> writer.writeAll(List(List("a", "b", "c"), List("d", "e", "f")))

scala> writer.close()
```


### Writing one line at a time with #writeRow
```scala
scala> val f = new File("out.csv")

scala> val writer = CSVWriter(f)
writer: com.github.tototoshi.csv.CSVWriter = com.github.tototoshi.csv.CSVWriter@41ad4de1

scala> writer.writeRow(List("a", "b", "c"))

scala> writer.writeRow(List("d", "e", "f"))

scala> writer.close()
```

## License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
