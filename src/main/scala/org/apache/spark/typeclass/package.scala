package org.apache.spark

import org.apache.spark.sql._
import org.apache.spark.sql.functions.lit
import cats.{Monoid, Order, Semigroup}
import cats.kernel.{BoundedSemilattice, CommutativeMonoid}
import cats.implicits._
import shapeless.|¬|

import scala.reflect.ClassTag

package object typeclass {
  object instances {
    implicit val colOrder: Order[Column] = Order.by(_.toString)

    //Note: Spark Column is not typesafe
    //This monoid is on the assumption that columns are boolean on projection.
    //Will fail on projection if columns are not boolean
    implicit val boolColAndBslUnsafe: BoundedSemilattice[Column] = new BoundedSemilattice[Column] {
      override def empty: Column = lit(true)
      override def combine(x: Column, y: Column): Column = x && y
    }

    //Note: Spark Dataframe/Dataset[Row] is not typesafe
    //If A = Row, at the very least number of cols must be the same
    // and cols at the same position must be coercible to the same type
    //No monoid as empty cannot be defined with sc.createDataFrame w/o schema
    implicit val dfUnionSemigroupUnsafe: Semigroup[DataFrame] = Semigroup.instance(_ union _)

    implicit def dsUnionMonoid[A : |¬|[Row]#λ : ClassTag : Encoder](implicit s: SparkSession)
    : Monoid[Dataset[A]] = new Monoid[Dataset[A]] {
      override def empty: Dataset[A] = s createDataset s.sparkContext.emptyRDD[A]
      override def combine(x: Dataset[A], y: Dataset[A]): Dataset[A] = x union y
    }
  }
}
