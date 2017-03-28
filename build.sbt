name := "householdexpenses"

version := "1.0"

lazy val `householdexpenses` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test, filters )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies += "org.apache.commons" % "commons-csv" % "1.3"

libraryDependencies ++= Seq(
  "org.webjars" %%  "webjars-play"  % "2.5.0",
  "org.webjars" %   "bootstrap"     % "3.1.1-2",
  "org.webjars" %   "jquery"        % "2.2.4"
)

libraryDependencies ++= Seq(
  "net.cakesolutions" %% "scala-kafka-client"      % "0.10.2.0",
  "net.cakesolutions" %% "scala-kafka-client-akka" % "0.10.2.0"
)

maintainer in Docker := "Willem Meints <willem.meints@gmail.com>"

dockerExposedPorts in Docker := Seq(9000)