package org.apache.spark

import org.apache.spark.sql._
import cats.{Monoid, Semigroup}
import shapeless.|¬|

import scala.reflect.ClassTag

package object typeclass {
  object instances {
    //Unsafe for Dataframe/Dataset[Row]
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
