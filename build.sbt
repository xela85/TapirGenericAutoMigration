lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    scalaVersion := V.scala212,
    crossScalaVersions := List(V.scala213, V.scala212, V.scala211),
    organization := "com.example",
    homepage := Some(url("https://github.com/com/example")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "example-username",
        "Example Full Name",
        "example@email.com",
        url("https://example.com")
      )
    ),
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List(
      "-Yrangepos",
      "-P:semanticdb:synthetics:on"
    )
  )
)

skip in publish := true

lazy val rules = (project in file("scalafix/rules")).settings(
  moduleName := "scalafix",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val loggerDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

val tapirVersion = "0.17.0-M9"

def tapir(tapirVersion: String) = Seq(
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % Versions.sttp,
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion
)

val commonDeps = Seq(
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % Versions.sttp,
  "dev.zio" %% "zio" % Versions.zio,
  "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats,
  "org.typelevel" %% "cats-effect" % Versions.catsEffect,
  "io.swagger" % "swagger-annotations" % "1.6.2",
  "io.circe" %% "circe-generic-extras" % "0.13.0"
) ++ loggerDependencies

lazy val input = project.settings(
  skip in publish := true,
  libraryDependencies ++= commonDeps ++ tapir("0.17.0-M8")
)

lazy val output = project.settings(
  skip in publish := true,
  libraryDependencies ++= commonDeps ++ tapir("0.17.0-M9")
)

lazy val tests = project
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) := 
      compile.in(Compile).dependsOn(compile.in(input, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input, Compile).value,
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
