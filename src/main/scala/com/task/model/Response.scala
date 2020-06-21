package com.task.model

import scodec._
import codecs._
import FieldCodecs._

sealed trait Response

/*
Parameter	        Offset,      Length in bytes      	  Value	                            Description
auth_key_id	      0,           8	                      0	                                Since message is in plain text
message_id	      8,           8	                      51E57AC91E83C801	                Server message ID
message_length	  16,          4	                      64	                              Message body length
%(resPQ)	        20,          4	                      05162463	                        resPQ constructor number from TL schema
nonce	            24,          16	                      3E0549828CCA27E966B301A48FECE2FC	Value generated by client in Step 1
server_nonce	    40,          16	                      A5CF4D33F4A11EA877BA4AA573907330	Server-generated random number
pq	              56,          12	                      17ED48941A08F981	                Single-byte prefix denoting length, an 8-byte string, and three bytes of padding
%(Vector long)	  68,          4	                      1cb5c415	                        Vector long constructor number from TL schema
count	            72,          4	                      1	                                Number of elements in key fingerprint list
fingerprints[]	  76,          8	                      c3b42b026ce86b21	                64 lower-order bits of SHA1 (server_public_key)
 */
final case class ResponsePQ(
  authKeyId: Long,
  messageId: Long,
  messageLength: Int,
  resPQ: Int,
  nonce: BigInt, // from requestPQ
  serverNonce: BigInt,
  pq: BigInt, // ascii32 + 3 bytes of padding (string pq is a representation of a natural number (in binary big endian format))
  vector: Int,
  fingerprints: Vector[BigInt]
) extends Response

object ResponsePQ {
  private val vectorConstructorNumber = 481674261
  private val resPQConstructorNumber = 85337187

  val codec: Codec[ResponsePQ] =
    (int64L :: int64L :: int32L :: int32L :: nonceCodec :: serverNonceCodec :: pqCodec :: int32L :: fingeprintsCodec).as[ResponsePQ]

  def apply(
    authKeyId: Long,
    messageId: Long,
    messageLength: Int,
    nonce: BigInt,
    serverNonce: BigInt,
    pq: BigInt,
    fingerprints: Vector[BigInt]
  ): ResponsePQ = new ResponsePQ(
    authKeyId,
    messageId,
    messageLength,
    resPQConstructorNumber,
    nonce,
    serverNonce,
    pq,
    vectorConstructorNumber,
    fingerprints
  )

  def apply(requestPQ: RequestPQ, serverNonce: BigInt, pq: BigInt, fingerprints: Vector[BigInt]): ResponsePQ = new ResponsePQ(
    requestPQ.authKeyId,
    requestPQ.messageId,
    requestPQ.messageLength,
    resPQConstructorNumber,
    requestPQ.nonce,
    serverNonce,
    pq,
    vectorConstructorNumber,
    fingerprints
  )
}