package com.task

import zio._
import zio.{BootstrapRuntime, ZLayer}
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

import java.net.ServerSocket

trait ZIOSpec extends BaseSpec with BootstrapRuntime {
  val logging: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make { (_, message) =>
    message
  }

  def findFreePort(): ZIO[Any, Throwable, Int] =
    ZIO.bracket(ZIO.effect(new ServerSocket(0)))(s => ZIO.effect(s.close()).ignore)(s => ZIO.succeed(s.getLocalPort))
}
