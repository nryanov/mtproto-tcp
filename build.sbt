name := "mtproto-tcp"

lazy val zioVersion = "1.0.0-RC19-2"
lazy val zioNioVersion = "1.0.0-RC7"
lazy val zioLoggingVersion = "0.2.9"
lazy val scodecVersion = "1.11.7"
lazy val kindProjectorVersion = "0.11.0"
lazy val pureconfigVersion = "0.12.3"
lazy val logbackVersion = "1.2.3"
lazy val scalaTestVersion = "3.1.1"

lazy val buildSettings =
  Seq(scalaVersion := "2.13.2", crossScalaVersions := Seq("2.12.10", "2.13.2"))

def compilerOptions(scalaVersion: String) =
  Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xlog-implicits",
    "-Xlint",
    "-language:existentials",
    "-language:postfixOps"
  ) ++ (CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor == 12 => scala212CompilerOptions
    case Some((2, scalaMajor)) if scalaMajor == 13 => scala213CompilerOptions
  })

lazy val scala212CompilerOptions =
  Seq("-Yno-adapted-args", "-Ywarn-unused-import", "-Xfuture")

lazy val scala213CompilerOptions = Seq("-Wunused:imports")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  ),
  scalacOptions ++= compilerOptions(scalaVersion.value),
  addCompilerPlugin(
    ("org.typelevel" %% "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)
  ),
  Test / parallelExecution := false
)

lazy val allSettings = commonSettings ++ buildSettings

lazy val mtprotoTcp = project
  .in(file("."))
  .settings(allSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scodec" %% "scodec-core" % scodecVersion,
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-nio" % zioNioVersion,
      "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
      "org.scodec" %% "scodec-testkit" % scodecVersion % Test,
      "dev.zio" %% "zio-test" % zioVersion % Test
    )
  )
