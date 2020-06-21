package com.task.tcp

import java.util.concurrent.TimeUnit

import com.task.ZIOSpec
import zio._
import zio.Chunk
import zio.nio.core.InetAddress
import scodec.bits._

import zio.duration._

class ServerSpec extends ZIOSpec {
  "TCP server" should {
    "send the same response on multiple identical reqPQ requests" in {
      val data = hex"00000000000000004A967027C47AE55114000000789746603E0549828CCA27E966B301A48FECE2FC".toByteBuffer

      val program = for {
        freePort <- findFreePort()
        host <- InetAddress.localHost
        server <- Server.start(host.hostname, freePort).fork
        _ <- ZIO.effect(Thread.sleep(3000))
        client <- TCPClient
          .create(freePort)
          .use { channel =>
            for {
              _ <- channel.write(Chunk.fromByteBuffer(data))
              result1 <- channel.read(1024, Duration(10, TimeUnit.SECONDS))
              _ <- channel.write(Chunk.fromByteBuffer(data))
              result2 <- channel.read(1024, Duration(10, TimeUnit.SECONDS))
          } yield (result1, result2)
        } fork

        response <- client.join
        _ <- server.interrupt
      } yield assertResult(response._1)(response._2)

      unsafeRun(program.provideLayer(logging))
    }

    "send different responses on multiple different reqPQ requests" in {
      val data1 = hex"10000000000000004A967027C47AE55114000000789746603E0549828CCA27E966B301A48FECE2FC".toByteBuffer
      val data2 = hex"20000000000000004A967027C47AE55114000000789746603E0549828CCA27E966B301A48FECE2FC".toByteBuffer

      val program = for {
        freePort <- findFreePort()
        host <- InetAddress.localHost
        server <- Server.start(host.hostname, freePort).fork
        _ <- ZIO.effect(Thread.sleep(3000))
        client <- TCPClient
          .create(freePort)
          .use { channel =>
            for {
              _ <- channel.write(Chunk.fromByteBuffer(data1))
              result1 <- channel.read(1024, Duration(10, TimeUnit.SECONDS))
              _ <- channel.write(Chunk.fromByteBuffer(data2))
              result2 <- channel.read(1024, Duration(10, TimeUnit.SECONDS))
          } yield (result1, result2)
        } fork

        response <- client.join
        _ <- server.interrupt
      } yield assert(response._1 != response._2)

      unsafeRun(program.provideLayer(logging))
    }

    "close client connection after req_DH_params" in {
      val data =
        hex"0000000000000000277A7117C97AE55140010000BEE412D73E0549828CCA27E966B301A48FECE2FCA5CF4D33F4A11EA877BA4AA57390733004494C553B0000000453911073000000216BE86C022BB4C3FE0001007BB0100A523161904D9C69FA04BC60DECFC5DD74B99995C768EB60D8716E2109BAF2D4601DAB6B09610DC11067BB89021E09471FCFA52DBD0F23204AD8CA8B012BF40A112F44695AB6C266955386114EF5211E6372227ADBD34995D3E0E5FF02EC63A43F9926878962F7C570E6A6E78BF8366AF917A5272675C46064BE62E3E202EFA8B1ADFB1C32A898C2987BE27B5F31D57C9BB963ABCB734B16F652CEDB4293CBB7C878A3A3FFAC9DBEA9DF7C67BC9E9508E111C78FC46E057F5C65ADE381D91FEE430A6B576A99BDF8551FDB1BE2B57069B1A45730618F27427E8A04720B4971EF4A9215983D68F2830C3EAA6E40385562F970D38A05C9F1246DC33438E6".toByteBuffer

      val program = for {
        freePort <- findFreePort()
        host <- InetAddress.localHost
        server <- Server.start(host.hostname, freePort).fork
        _ <- ZIO.effect(Thread.sleep(3000))
        client <- TCPClient
          .create(freePort)
          .use { channel =>
            for {
              _ <- channel.write(Chunk.fromByteBuffer(data))
          } yield ()
        } fork

        _ <- client.join
        _ <- server.interrupt
      } yield succeed

      unsafeRun(program.provideLayer(logging))
    }
  }
}
