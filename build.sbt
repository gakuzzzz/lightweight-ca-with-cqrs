val baseName = """lightweight-ca-with-cqrs"""

val baseSettings = Seq(
  organization := "jp.t2v",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.1",
)

lazy val domain = (project in file("domain"))
  .settings(baseSettings)
  .settings(name := s"$baseName-domain")

lazy val query = (project in file("query"))
  .settings(baseSettings)
  .settings(name := s"$baseName-query")
  .dependsOn(domain)

lazy val usecase = (project in file("usecase"))
  .settings(baseSettings)
  .settings(name := s"$baseName-usecase")
  .dependsOn(domain)

lazy val infra = (project in file("infra"))
  .settings(baseSettings)
  .settings(name := s"$baseName-infra")
  .dependsOn(domain, query, usecase)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(PlayScala)
  .settings(baseSettings)
  .settings(
    name := baseName,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
//    TwirlKeys.templateImports += "jp.t2v.controllers._",
//    play.sbt.routes.RoutesKeys.routesImport += "jp.t2v.binders._",
  ).dependsOn(domain, query, usecase, infra)

lazy val root = (project in file("."))
  .aggregate(webapp)
