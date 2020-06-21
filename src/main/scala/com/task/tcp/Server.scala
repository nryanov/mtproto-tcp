package com.task.tcp

import zio._
import zio.nio.channels._
import zio.nio.core._
import zio.logging.{Logging, _}
import com.task.model._
import com.task.model.{RequestPQ, ResponsePQ}

import scala.util.Random

final class Server(socket: AsynchronousServerSocketChannel) {
  def run(): ZIO[Logging, Nothing, Unit] = for {
    _ <- socket.accept.preallocate
      .flatMap(_.use { channel =>
        for {
          _ <- log.info("Process next connection")
          _ <- doWork(channel).catchAll(ex => log.error(ex.getMessage))
        } yield ()
      }.fork)
      .forever
      .fork
  } yield ()

  private def doWork(channel: AsynchronousSocketChannel): ZIO[Logging, Exception, Nothing] = {
    def process(ref: Ref[Map[RequestPQ, ResponsePQ]]) =
      for {
        chunk <- channel.read(512)
        _ <- decode(chunk.toArray).toEither match {
          case Left(value) => log.error(value.messageWithContext)
          case Right(value) =>
            value match {
              case req: RequestPQ => processReqPQ(channel, ref, req)
              case req: RequestDHParams =>
                for {
                  _ <- log.info(s"Got request DH: $req. Trying to close the connection")
                  _ <- channel.shutdownInput
                  _ <- channel.shutdownOutput
                } yield ()
            }
        }
      } yield ()

    for {
      ref <- Ref.make[Map[RequestPQ, ResponsePQ]](Map.empty)
      r <- process(ref).whenM(channel.isOpen).forever
    } yield r
  }

  private def processReqPQ(channel: AsynchronousSocketChannel, ref: Ref[Map[RequestPQ, ResponsePQ]], requestPQ: RequestPQ) = for {
    _ <- log.info(s"Got request: $requestPQ")
    response <- generateResponse(ref, requestPQ)
    _ <- log.info(s"Response: $response")
    encoded = encodeResponse(response)
    _ <- channel.write(Chunk.fromByteBuffer(encoded.toByteBuffer))
  } yield ()

  // https://core.telegram.org/mtproto/auth_key#error-handling-lost-queries-and-responses
  private def generateResponse(ref: Ref[Map[RequestPQ, ResponsePQ]], requestPQ: RequestPQ): ZIO[Any, Nothing, ResponsePQ] = for {
    savedResponses <- ref.updateSomeAndGet {
      case saved if !saved.contains(requestPQ) =>
        val response = ResponsePQ(requestPQ, Random.nextLong(), Random.nextLong(), Vector(Random.nextLong()))
        saved + ((requestPQ, response))
    }
  } yield savedResponses(requestPQ)
}

object Server {
  def start(host: String, port: Int): ZIO[Logging, Exception, Nothing] = AsynchronousServerSocketChannel().mapM {
    socket: AsynchronousServerSocketChannel =>
      for {
        _ <- log.info("Try to bind channel to the specified address")
        _ <- SocketAddress.inetSocketAddress(host, port) >>= socket.bind
        _ <- log.info("Channel was bound successfully")
        server = new Server(socket)
        _ <- server.run()
      } yield ()
  }.useForever
}
