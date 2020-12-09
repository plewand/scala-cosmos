package org.cosmos.scala

import org.cosmos.scala.JsonEncoderDerivation.{jsonProd, jsonSum, summonTypeClassInstances}

import scala.compiletime.{constValue, erasedValue, summonInline}
import scala.deriving._
import scala.reflect._


// All JSON encoders derive from this class.
trait JsonEncoder[T] {
  def asJson(encoded: T): String
}


// Instances for builtin types used in the project.
object JsonEncoderInstances {

  given JsonEncoder[String] {
    def asJson(encoded: String): String = "\"" + encoded + "\""
  }

  given JsonEncoder[Double] {
    def asJson(encoded: Double): String = s"$encoded"
  }

  given[T] (using elemEncoder: JsonEncoder[T]) as JsonEncoder[Seq[T]] {
    def asJson(v: Seq[T]): String = "[" + v.map(elemEncoder.asJson).mkString(", ") + "]"
  }

}


// It uses automatic type class derivation to encode responses to JSON.
object JsonEncoder {

  import JsonEncoderDerivation._

  // To use automatic type class derivation, the companion object must export 'derived'.
  inline given derived[T](using m: Mirror.Of[T]) as JsonEncoder[T] = {
    val encoders = summonTypeClassInstances[m.MirroredElemTypes]
    inline m match {
      case s: Mirror.SumOf[T] => jsonSum(s, encoders)
      case p: Mirror.ProductOf[T] => jsonProd(p, encoders)
    }
  }

  def toStringEncoder[T](convert: T => String)(using stringEncoder: JsonEncoder[String]): JsonEncoder[T] = {
    return new JsonEncoder[T] {
      override def asJson(encoded: T): String = stringEncoder.asJson(convert(encoded))
    }
  }

  def toDoubleEncoder[T](convert: T => Double)(using doubleEncoder: JsonEncoder[Double]): JsonEncoder[T] = {
    return new JsonEncoder[T] {
      override def asJson(encoded: T): String = doubleEncoder.asJson(convert(encoded))
    }
  }
}

object JsonEncoderDerivation {
  // Import givens explicitly.
  import JsonEncoderInstances.given

  // The mirror of a sum provides ordinal method that is used to obtain the encoder.
  def jsonSum[T](s: Mirror.SumOf[T], encoders: List[JsonEncoder[_]]): JsonEncoder[T] =
    new JsonEncoder[T] {
      def asJson(encoded: T): String = {
        val index = s.ordinal(encoded)
        encode(encoders(index))(encoded)
      }
    }

  // It needs to be inline because of summonProductElemNames internals.
  inline def jsonProd[T](s: Mirror.ProductOf[T], encoders: List[JsonEncoder[_]]): JsonEncoder[T] = {
    val elemNames = summonProductElemNames[s.MirroredElemLabels]
    new JsonEncoder[T] {
      def asJson(encoded: T): String = {
        if (encoders.nonEmpty) {
          "{" + iterator(encoded)
            .zip(elemNames)
            .zip(encoders.iterator)
            .map { case ((enc, name), encoder) => "\"" + name + "\":" + encode(encoder)(enc) }.mkString(",") + "}"
        } else {
          summon[JsonEncoder[String]].asJson(encoded.toString)
        }
      }
    }
  }

  // Collect all types needed to derive the type class.
  inline def summonTypeClassInstances[T <: Tuple]: List[JsonEncoder[_]] = inline erasedValue[T] match {
    case _: EmptyTuple => Nil
    case _: (t *: ts) => summonInline[JsonEncoder[t]] :: summonTypeClassInstances[ts]
  }

  // Thanks to mirrors it is possible to get product names on derivation time.
  private inline def summonProductElemNames[T <: Tuple]: List[String] = inline erasedValue[T] match {
    case _: EmptyTuple => Nil
    case _: (t *: ts) => constValue[t].toString :: summonProductElemNames[ts]
  }

  private def iterator[T](p: T) = p.asInstanceOf[Product].productIterator

  private def encode(elem: JsonEncoder[_])(encoded: Any): String =
    elem.asInstanceOf[JsonEncoder[Any]].asJson(encoded)
}
