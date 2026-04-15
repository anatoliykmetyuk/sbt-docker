import sbtcompat.PluginCompat._

enablePlugins(DockerPlugin)

name := "scripted-multi-configurations"
organization := "sbtdocker"


lazy val Alfa = config("Alfa")
inConfig(Alfa)(sbtdocker.DockerSettings.baseDockerSettings)

Alfa / docker / imageNames := Def.uncached(Seq(
  ImageName("sbtdocker/scripted-multi-configurations-alfa")
))
Alfa / docker / target := target.value / "docker-alfa"
Alfa / docker / dockerfile := Def.uncached {
  new Dockerfile {
    from("busybox")
    entryPoint("echo", "alfa")
  }
}


lazy val Bravo = config("Bravo")
inConfig(Bravo)(sbtdocker.DockerSettings.baseDockerSettings)

Bravo / docker / imageNames := Def.uncached(Seq(
  ImageName("sbtdocker/scripted-multi-configurations-bravo")
))
Bravo / docker / target := target.value / "docker-bravo"
Bravo / docker / dockerfile := Def.uncached {
  new Dockerfile {
    from("busybox")
    entryPoint("echo", "bravo")
  }
}


def checkImage(imageName: ImageName, expectedOut: String): Unit = {
  val process = scala.sys.process.Process("docker", Seq("run", "--rm", imageName.toString))
  val out = process.!!
  if (out.trim != expectedOut) sys.error(s"Unexpected output (${imageName.toString}): $out")
}

val check = taskKey[Unit]("Check")

check := Def.uncached {
  checkImage((Alfa / docker / imageNames).value.head, "alfa")
  checkImage((Bravo / docker / imageNames).value.head, "bravo")
}
