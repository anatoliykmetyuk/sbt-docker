import sbtcompat.PluginCompat._

enablePlugins(DockerPlugin)

ThisBuild / organization := "sbtdocker"

lazy val alfa = project.in(file("alfa"))
  .settings(name := "scripted-multi-project-alfa")
  .enablePlugins(DockerPlugin)
  .settings(docker / dockerfile := Def.uncached {
    new Dockerfile {
      from("busybox")
      entryPoint("echo", "alfa")
    }
  })

lazy val bravo = project.in(file("bravo"))
  .settings(name := "scripted-multi-project-bravo")
  .enablePlugins(DockerPlugin)
  .settings(docker / dockerfile := Def.uncached {
    new Dockerfile {
      from("busybox")
      entryPoint("echo", "bravo")
    }
  })

def checkImage(imageName: ImageName, expectedOut: String): Unit = {
  val process = scala.sys.process.Process("docker", Seq("run", "--rm", imageName.toString))
  val out = process.!!
  if (out.trim != expectedOut) sys.error(s"Unexpected output ($imageName): $out")
}

val check = taskKey[Unit]("Check")

check := Def.uncached {
  checkImage((alfa / docker / imageNames).value.head, "alfa")
  checkImage((bravo / docker / imageNames).value.head, "bravo")
}
