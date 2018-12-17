# Spark Typeclass

## Cats typeclass instances for Apache Spark
Currently only contains algebraic (`Semigroup` / `Monoid`) and order typeclass instances for `Column` `DataFrame` and `Dataset`.
For `RDD` typeclass instances, you may wish refer to [Frameless](https://github.com/typelevel/frameless/).
(We could include them in this project in the future as well.)

This project has been compile with Spark 2.4.0 but it should compatible with any Spark 2.x on Scala 2.11.x or 2.12.x.

### SBT Setup
```scala
libraryDependencies += "io.github.yangzai" %% "spark-typeclass" % "0.1.0"
```

### Example
```Scala
import cats.data.NonEmptyList
import cats.Foldable
import cats.implicits._
import org.apache.spark.sql._

//import implicit instances to scope
import org.apache.spark.typeclass.instances._

case class StringRecord(value: String)
case class IntRecord(value: Int)

object Main {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkSession.builder
      .master("local[*]")
      .getOrCreate

    import spark.implicits._

    val ds1 = Seq(StringRecord("a")).toDS
    val ds2 = Seq(StringRecord("b")).toDS
    val df = Seq(IntRecord(1)).toDF

    //Combine examples
    ds1 |+| ds2 show()
    ds1.toDF |+| ds2.toDF |+| df show()

    //Reducible and Foldable examples
    NonEmptyList.of(ds1.toDF, ds2.toDF, df).reduce.show //only requires Semigroup instance
    Foldable[List] fold List(ds1, ds2) show() //requires Monoid instance
  }
}
```
