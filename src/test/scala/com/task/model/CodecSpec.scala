package com.task.model

import scodec.{Attempt, CodecSuite, DecodeResult}
import scodec.bits._

class CodecSpec extends CodecSuite {
  "RequestPQ codec" should {
    "decode request" in {
      /*
      TCP packet number | Data
      0000 | 00 00 00 00 00 00 00 00 4A 96 70 27 C4 7A E5 51
      0010 | 14 00 00 00 78 97 46 60 3E 05 49 82 8C CA 27 E9
      0020 | 66 B3 01 A4 8F EC E2 FC
       */

      val data: BitVector = hex"00000000000000004A967027C47AE55114000000789746603E0549828CCA27E966B301A48FECE2FC".bits

      assertResult(
        Attempt.successful(
          DecodeResult(
            RequestPQ(0, 5901257869632771658L, 20, 1615239032, BigInt("82439588182091944552818451753334530812")),
            BitVector.empty
          )
        )
      )(RequestPQ.codec.decode(data))
    }
  }

  "ResponsePQ codec" should {
    "encode response" in {
      /*
      TCP packet number | Data
      0000 | 00 00 00 00 00 00 00 00 01 C8 83 1E C9 7A E5 51
      0010 | 40 00 00 00 63 24 16 05 3E 05 49 82 8C CA 27 E9
      0020 | 66 B3 01 A4 8F EC E2 FC A5 CF 4D 33 F4 A1 1E A8
      0030 | 77 BA 4A A5 73 90 73 30 08 17 ED 48 94 1A 08 F9
      0040 | 81 00 00 00 15 C4 B5 1C 01 00 00 00 21 6B E8 6C
      0050 | 02 2B B4 C3
       */

      val expected: BitVector =
        hex"000000000000000001C8831EC97AE55140000000632416053E0549828CCA27E966B301A48FECE2FCA5CF4D33F4A11EA877BA4AA5739073300817ED48941A08F98100000015C4B51C01000000216BE86C022BB4C3".bits

      val response = ResponsePQ(
        0,
        5901257890957871105L,
        64,
        85337187,
        BigInt("82439588182091944552818451753334530812"),
        BigInt("-119883376304825741476995835887086439632"),
        BigInt("1724114033281923457"),
        481674261,
        Vector(BigInt("14101943622620965665"))
      )

      assertResult(expected)(ResponsePQ.codec.encode(response).require)
    }
  }

  "RequestDHParams codec" should {
    "decode request" in {
      /*
      TCP packet number | Data
      0000 | 00 00 00 00 00 00 00 00 27 7A 71 17 C9 7A E5 51
      0010 | 40 01 00 00 BE E4 12 D7 3E 05 49 82 8C CA 27 E9
      0020 | 66 B3 01 A4 8F EC E2 FC A5 CF 4D 33 F4 A1 1E A8
      0030 | 77 BA 4A A5 73 90 73 30 04 49 4C 55 3B 00 00 00
      0040 | 04 53 91 10 73 00 00 00 21 6B E8 6C 02 2B B4 C3
      0050 | FE 00 01 00 7B B0 10 0A 52 31 61 90 4D 9C 69 FA
      0060 | 04 BC 60 DE CF C5 DD 74 B9 99 95 C7 68 EB 60 D8
      0070 | 71 6E 21 09 BA F2 D4 60 1D AB 6B 09 61 0D C1 10
      0080 | 67 BB 89 02 1E 09 47 1F CF A5 2D BD 0F 23 20 4A
      0090 | D8 CA 8B 01 2B F4 0A 11 2F 44 69 5A B6 C2 66 95
      00A0 | 53 86 11 4E F5 21 1E 63 72 22 7A DB D3 49 95 D3
      00B0 | E0 E5 FF 02 EC 63 A4 3F 99 26 87 89 62 F7 C5 70
      00C0 | E6 A6 E7 8B F8 36 6A F9 17 A5 27 26 75 C4 60 64
      00D0 | BE 62 E3 E2 02 EF A8 B1 AD FB 1C 32 A8 98 C2 98
      00E0 | 7B E2 7B 5F 31 D5 7C 9B B9 63 AB CB 73 4B 16 F6
      00F0 | 52 CE DB 42 93 CB B7 C8 78 A3 A3 FF AC 9D BE A9
      0100 | DF 7C 67 BC 9E 95 08 E1 11 C7 8F C4 6E 05 7F 5C
      0110 | 65 AD E3 81 D9 1F EE 43 0A 6B 57 6A 99 BD F8 55
      0120 | 1F DB 1B E2 B5 70 69 B1 A4 57 30 61 8F 27 42 7E
      0130 | 8A 04 72 0B 49 71 EF 4A 92 15 98 3D 68 F2 83 0C
      0140 | 3E AA 6E 40 38 55 62 F9 70 D3 8A 05 C9 F1 24 6D
      0150 | C3 34 38 E6
       */

      val expected =
        hex"0000000000000000277A7117C97AE55140010000BEE412D73E0549828CCA27E966B301A48FECE2FCA5CF4D33F4A11EA877BA4AA57390733004494C553B0000000453911073000000216BE86C022BB4C3FE0001007BB0100A523161904D9C69FA04BC60DECFC5DD74B99995C768EB60D8716E2109BAF2D4601DAB6B09610DC11067BB89021E09471FCFA52DBD0F23204AD8CA8B012BF40A112F44695AB6C266955386114EF5211E6372227ADBD34995D3E0E5FF02EC63A43F9926878962F7C570E6A6E78BF8366AF917A5272675C46064BE62E3E202EFA8B1ADFB1C32A898C2987BE27B5F31D57C9BB963ABCB734B16F652CEDB4293CBB7C878A3A3FFAC9DBEA9DF7C67BC9E9508E111C78FC46E057F5C65ADE381D91FEE430A6B576A99BDF8551FDB1BE2B57069B1A45730618F27427E8A04720B4971EF4A9215983D68F2830C3EAA6E40385562F970D38A05C9F1246DC33438E6".bits

      val data = RequestDHParams(
        0,
        5901257890839231015L,
        320,
        -686627650,
        BigInt("82439588182091944552818451753334530812"),
        BigInt("-119883376304825741476995835887086439632"),
        1229739323,
        1402015859,
        hex"c3b42b026ce86b21",
        hex"fe0001007bb0100a523161904d9c69fa04bc60decfc5dd74b99995c768eb60d8716e2109baf2d4601dab6b09610dc11067bb89021e09471fcfa52dbd0f23204ad8ca8b012bf40a112f44695ab6c266955386114ef5211e6372227adbd34995d3e0e5ff02ec63a43f9926878962f7c570e6a6e78bf8366af917a5272675c46064be62e3e202efa8b1adfb1c32a898c2987be27b5f31d57c9bb963abcb734b16f652cedb4293cbb7c878a3a3ffac9dbea9df7c67bc9e9508e111c78fc46e057f5c65ade381d91fee430a6b576a99bdf8551fdb1be2b57069b1a45730618f27427e8a04720b4971ef4a9215983d68f2830c3eaa6e40385562f970d38a05c9f1246dc33438e6"
      )

      assertResult(expected)(RequestDHParams.codec.encode(data).require)
    }
  }
//
//  "InnerData codec" should {
//    "decode encrypted data" in {
//      val publicKey = hex"c3b42b026ce86b21"
//      val encryptedData =
//        hex"fe0001007bb0100a523161904d9c69fa04bc60decfc5dd74b99995c768eb60d8716e2109baf2d4601dab6b09610dc11067bb89021e09471fcfa52dbd0f23204ad8ca8b012bf40a112f44695ab6c266955386114ef5211e6372227adbd34995d3e0e5ff02ec63a43f9926878962f7c570e6a6e78bf8366af917a5272675c46064be62e3e202efa8b1adfb1c32a898c2987be27b5f31d57c9bb963abcb734b16f652cedb4293cbb7c878a3a3ffac9dbea9df7c67bc9e9508e111c78fc46e057f5c65ade381d91fee430a6b576a99bdf8551fdb1be2b57069b1a45730618f27427e8a04720b4971ef4a9215983d68f2830c3eaa6e40385562f970d38a05c9f1246dc33438e6"
//
//      // Use RSA/NONE/NoPadding as algorithm and BouncyCastle as crypto provider
//      val asymmetricCipher = Cipher.getInstance("RSA/ECB/NoPadding");
//
//      // assume, that publicKeyBytes contains a byte array representing
//      // your public key
//      val publicKeySpec = new X509EncodedKeySpec(publicKey.toArray);
//
//      Security.getProviders().foreach(p => println(p.getName));
//
////      val keyFactory = KeyFactory.getInstance("SunRsaSign");
////      val key = keyFactory.generatePublic(publicKeySpec);
//      val key = RSAPublicKeyImpl.newKey(publicKeySpec.getEncoded)
//
//      // initialize your cipherSunX509
//      asymmetricCipher.init(Cipher.DECRYPT_MODE, key);
//      // asuming, cipherText is a byte array containing your encrypted message
//      val decrypted = asymmetricCipher.doFinal(encryptedData.toArray);
//
//      println(decrypted)
//    }
//  }
}
