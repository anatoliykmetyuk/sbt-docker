lazy val scala212 = "2.12.21"
lazy val scala3   = "3.8.3"

crossScalaVersions := Seq(scala212, scala3)

LocalRootProject / name := "sbt-docker"
organization := "se.marcuslonnberg"
organizationHomepage := Some(url("https://github.com/marcus-drake"))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.1.0"),
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.12.9"
        case _      => "2.0.0-RC12"
      }
    },
    scriptedSbt := (pluginCrossBuild / sbtVersion).value,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.apache.commons" % "commons-text" % "1.15.0"
)

scalacOptions := Seq("-deprecation", "-unchecked", "-feature")

licenses := Seq("MIT License" -> url("https://github.com/marcus-drake/sbt-docker/blob/master/LICENSE"))
homepage := Some(url("https://github.com/marcus-drake/sbt-docker"))
scmInfo := Some(ScmInfo(url("https://github.com/marcus-drake/sbt-docker"), "scm:git:git://github.com:marcus-drake/sbt-docker.git"))

developers := List(
  Developer(
    "marcus-drake",
    "Marcus Drake",
    "",
    url("http://marcuslonnberg.se")
  )
)

publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some(("snapshots" at (nexus + "content/repositories/snapshots")))
  else
    Some(("releases" at (nexus + "service/local/staging/deploy/maven2")))
}

pomIncludeRepository := { _ => false }
