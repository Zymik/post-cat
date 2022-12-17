ThisBuild / name := "post-cat"

val http4sVersion = "0.23.16"
val tapirVersion = "1.2.3"
val circeVersion = "0.14.3"

lazy val databaseDependencies = Seq(
  // Start with this one
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  // And add any of these as needed
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1", // Postgres driver 42.3.1 + type mappings.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test", // ScalaTest support for typechecking statements.
  "org.flywaydb" % "flyway-core" % "9.8.3"
)

lazy val commonSettings = Seq(
  ThisBuild / version := "0.0.5",
  ThisBuild / scalaVersion := "2.13.10",
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % tapirVersion,
    "org.typelevel" %% "cats-parse" % "0.3.8",
    "org.scalactic" %% "scalactic" % "3.2.14",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    "org.scalamock" %% "scalamock" % "5.2.0" % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test
  ),

  // Ciris for configuration
  libraryDependencies += "is.cir" %% "ciris" % "3.0.0",
  libraryDependencies += "is.cir" %% "ciris-circe-yaml" % "3.0.0",
  libraryDependencies += "is.cir" %% "ciris-http4s" % "3.0.0",

  //logging
  libraryDependencies ++= Seq(
    "org.typelevel" %% "log4cats-core" % "2.5.0", // Only if you want to Support Any Backend
    "org.typelevel" %% "log4cats-slf4j" % "2.5.0",
    "org.typelevel" %% "log4cats-noop" % "2.5.0" % Test // Direct Slf4j Support - Recommended
  ),
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5",
  //Using latest lts java version
  dockerBaseImage := "openjdk:17.0.2-jdk"

)

//Common for all services
lazy val common = (project in file("common")).settings(
  libraryDependencies ++= databaseDependencies,
  commonSettings
)


//Core of application
lazy val core = (project in file("core"))
  .dependsOn(common)
  .settings(
    libraryDependencies ++= databaseDependencies,
    name := "post-cat-core",
    commonSettings,
    mainClass := Some("ru.kosolapov.ivan.postcat.core.Application")
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)

//Rest api of getting posts
lazy val rest = (project in file("api"))
  .dependsOn(
    common
  )
  .settings(
    libraryDependencies ++= databaseDependencies,
    name := "post-cat-public-api",
    commonSettings
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)

//Telegram bot
val telegramiumVersion = "7.63.0"
lazy val telegram =
  (project in file("telegram"))
    .dependsOn(common)
    .settings(
      commonSettings,
      name := "post-cat-telegram",
      libraryDependencies += "io.github.apimorphism" %% "telegramium-core" % telegramiumVersion,
      libraryDependencies += "io.github.apimorphism" %% "telegramium-high" % telegramiumVersion,
      excludeDependencies += ExclusionRule("org.slf4j", "slf4j-simple")
    )
    .enablePlugins(DockerPlugin, JavaAppPackaging)

lazy val root = (project in file(".")).aggregate(common, core, telegram).settings(commonSettings)