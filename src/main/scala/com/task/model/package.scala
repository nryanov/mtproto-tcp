package com.task

import scodec.Attempt
import scodec.bits.BitVector
import shapeless.{:+:, CNil, Inl, Inr}

package object model {
  val codec = RequestPQ.codec.:+:(RequestDHParams.codec)

  def decode(data: Array[Byte]): Attempt[Request] =
    codec.choice
      .decode(BitVector(data))
      .map(_.value match {
        case Inl(requestDHParams) => requestDHParams
        case Inr(Inl(requestPQ))  => requestPQ
      })

  def encodeResponse(responsePQ: ResponsePQ): BitVector = ResponsePQ.codec.encode(responsePQ).require
}
