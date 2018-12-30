
name := "AkkaLearn"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
   "com.typesafe.akka"     %% "akka-http"       % "10.1.5",
   "com.typesafe.akka"     %% "akka-actor"      % "2.5.18",
   "com.typesafe.akka"     %% "akka-stream"     % "2.5.18",

   "de.heikoseeberger"     %% "akka-http-circe" % "1.22.0",

   "io.circe"              %% "circe-core"      % "0.9.3",
   "io.circe"              %% "circe-generic"   % "0.9.3",
   "io.circe"              %% "circe-parser"    % "0.9.3",

   "org.tpolecat"          %% "doobie-core"     % "0.6.0",
   "org.tpolecat"          %% "doobie-hikari"   % "0.6.0",
   "org.tpolecat"          %% "doobie-postgres" % "0.6.0",

   "org.typelevel"         %% "cats-core"       % "1.5.0",
   "org.typelevel"         %% "cats-effect"     % "1.0.0",

   "com.github.pureconfig" %% "pureconfig"      % "0.10.1",

   "ch.megard"             %% "akka-http-cors"  % "0.3.2",
   "ch.qos.logback"        %  "logback-classic" % "1.1.3" % Runtime
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)