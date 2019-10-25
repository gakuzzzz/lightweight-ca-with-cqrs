val baseName = """lightweight-ca-with-cqrs"""

val baseSettings = Seq(
  organization := "jp.t2v",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.1",
  libraryDependencies ++= Seq(
    "org.scalacheck"           %% "scalacheck"                       % "1.14.1"                    % Test,
    "ch.qos.logback"            % "logback-classic"                  % "1.2.3",
    "jp.ne.opt"                %% "chronoscala"                      % "0.3.2",
  ),
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  javaOptions in Test += "-Dfile.encoding=UTF-8",
)

lazy val domain = (project in file("domain"))
  .settings(baseSettings)
  .settings(
    name := s"$baseName-domain",
    libraryDependencies ++= Seq(
      "org.mindrot"               % "jbcrypt"                          % "0.3m",
    )
  )

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
  .settings(
    name := s"$baseName-infra",
    libraryDependencies ++= Seq(
      "org.scalikejdbc"          %% "scalikejdbc"                      % "3.3.5",
      "org.scalikejdbc"          %% "scalikejdbc-config"               % "3.3.5",
      "org.scalikejdbc"          %% "scalikejdbc-syntax-support-macro" % "3.3.5",
      "org.typelevel"            %% "cats-core"                        % "2.0.0",
    )
  )
  .dependsOn(domain, query, usecase)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(PlayScala)
  .settings(baseSettings)
  .settings(
    name := baseName,
    libraryDependencies ++= Seq(
      "org.scalatestplus.play"   %% "scalatestplus-play"               % "4.0.3"                     % Test,
      "org.scalikejdbc"          %% "scalikejdbc-play-initializer"     % "2.7.1-scalikejdbc-3.3",
      "org.scalikejdbc"          %% "scalikejdbc-play-dbapi-adapter"   % "2.7.1-scalikejdbc-3.3",
      "org.scalikejdbc"          %% "scalikejdbc-play-fixture"         % "2.7.1-scalikejdbc-3.3",
      "jp.t2v"                   %% "play2-auth"                       % "play-2.7.x-auth-0.15-RC1",
      "jp.t2v"                   %% "play2-auth-test"                  % "play-2.7.x-auth-0.15-RC1"  % Test,
      "com.softwaremill.macwire" %% "macros"                           % "2.3.3"                     % Provided,
      "com.softwaremill.macwire" %% "util"                             % "2.3.3",
      "com.h2database"            % "h2"                               % "1.4.199",
      "org.flywaydb"             %% "flyway-play"                      % "5.3.3",
      "com.dripower"             %% "play-circe"                       % "2712.0",
      "io.circe"                 %% "circe-core"                       % "0.12.3",
      "io.circe"                 %% "circe-generic"                    % "0.12.3",
      "io.circe"                 %% "circe-parser"                     % "0.12.3",
      cacheApi,
      caffeine
    )
//    TwirlKeys.templateImports += "jp.t2v.controllers._",
//    play.sbt.routes.RoutesKeys.routesImport += "jp.t2v.binders._",
  ).dependsOn(domain, query, usecase, infra)

lazy val root = (project in file("."))
  .aggregate(webapp)
