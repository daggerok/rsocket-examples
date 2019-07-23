name := "scala-rsocket"

version := "0.1"

scalaVersion := "2.13.0"

resolvers ++= Seq(
  Resolver.jcenterRepo
)

//lazy val rSocketVersion = "0.10.3"
lazy val rSocketVersion = "1.0.0-RC1"
libraryDependencies ++= Seq(
  "io.rsocket"      % "rsocket-core"            % rSocketVersion,
  "io.rsocket"      % "rsocket-transport-netty" % rSocketVersion,
  "io.rsocket"      % "rsocket-transport-local" % rSocketVersion,
  "io.rsocket"      % "rsocket-examples"        % "0.10.3",
)

lazy val commonSettings = Seq(
  organization := "com.github.daggerok",
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
