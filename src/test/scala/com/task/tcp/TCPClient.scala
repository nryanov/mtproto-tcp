package com.task.tcp

import zio._
import zio.nio.channels._
import zio.nio.core._

object TCPClient {
  def create(port: Int): ZManaged[Any, Exception, AsynchronousSocketChannel] = AsynchronousSocketChannel().mapM { client =>
    for {
      host <- InetAddress.localHost
      address <- SocketAddress.inetSocketAddress(host, port)
      _ <- client.connect(address)
    } yield client
  }
}
