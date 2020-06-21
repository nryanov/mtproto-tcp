package com.task

import com.task.tcp.Server
import zio._
import zio.logging._
import zio.logging.slf4j._

object Application extends zio.App {
  val logging: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make { (_, message) =>
    message
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program().provideLayer(logging).ignore.as(0)

  private def program(): ZIO[Logging, Throwable, Unit] = for {
    _ <- log.info("Read config")
    cfg <- readConfig()
    _ <- log.info("Create TCP server")
    _ <- Server.start(cfg.host, cfg.port)
  } yield ()

  private def readConfig(): Task[Configuration] = Configuration.load()
}
