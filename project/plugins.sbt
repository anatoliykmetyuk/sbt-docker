dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.14.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.38.9"
)

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.0")
