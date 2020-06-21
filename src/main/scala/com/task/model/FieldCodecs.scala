package com.task.model

import scodec._
import scodec.bits._
import codecs._

object FieldCodecs {
  val nonceCodec: Codec[BigInt] = bytes(16).xmap(v => BigInt(v.toArray), bigint => ByteVector(bigint.toByteArray))
  val serverNonceCodec: Codec[BigInt] = nonceCodec
  val newNonceCodec: Codec[BigInt] = bytes(32).xmap(v => BigInt(v.toArray), bigint => ByteVector(bigint.toByteArray))

  val pqCodec: Codec[BigInt] = paddedFixedSizeBytes(12, variableSizeBytes(int8, bytes, 0), int24.unit(0))
    .xmap(v => BigInt(v.toArray), bigint => ByteVector(bigint.toByteArray))

  val uint64L: Codec[BigInt] = bytes(8).xmap(v => BigInt(v.toArray.reverse), bigint => ByteVector(bigint.toByteArray.tail.reverse))
  val fingeprintsCodec: Codec[Vector[BigInt]] = vectorOfN(int32L, uint64L)

  val publicKeyCodec: Codec[ByteVector] = fixedSizeBytes(8, bytes).xmap(_.reverse, _.reverse)
  val encryptedDataCodec: Codec[ByteVector] = fixedSizeBytes(260, bytes)

  val pCodec: Codec[Int] = paddedFixedSizeBytes(8, variableSizeBytes(int8, int32, 0), int24.unit(0))
  val qCodec: Codec[Int] = pCodec
}
