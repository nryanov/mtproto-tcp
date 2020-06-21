package com.task

import zio.Task
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class Configuration(host: String, port: Int)

object Configuration {
  def load(): Task[Configuration] = Task.effect(ConfigSource.default.loadOrThrow[Configuration])

}
