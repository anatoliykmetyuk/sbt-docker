import sbtcompat.PluginCompat._

enablePlugins(DockerPlugin)

name := "scripted-dockerfile-file"

organization := "sbtdocker"

version := "0.1.0"

// Define a Dockerfile
docker / dockerfile := Def.uncached(NativeDockerfile(file("Dockerfile")))

// Set a custom image name
docker / imageNames := Def.uncached {
  val imageName = ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value))
  Seq(imageName, imageName.copy(tag = Some("latest")))
}

val check = taskKey[Unit]("Check")

check := Def.uncached {
  val names = (docker / imageNames).value
  names.foreach { imageName =>
    val process = scala.sys.process.Process("docker", Seq("run", "--rm", imageName.toString))
    val out = process.!!
    if (out.trim != "Hello World") sys.error("Unexpected output: " + out)
  }
}
