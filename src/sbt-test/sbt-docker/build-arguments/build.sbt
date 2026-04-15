enablePlugins(DockerPlugin)

import sbt.Keys.fileConverter
import sbtcompat.PluginCompat._
import xsbti.FileConverter

name := "build-arguments"

organization := "sbtdocker"

version := "0.1.0"

// Define a Dockerfile
docker / dockerfile := Def.uncached {
  implicit val conv: FileConverter = fileConverter.value
  val jarFile = sbtcompat.PluginCompat.toFile((Compile / packageBin / Keys.`package`).value)
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse {
    sys.error("Expected exactly one main class")
  }
  val jarTarget = s"/app/${jarFile.getName}"
  val libFiles = classpath.map { attr =>
    val f = sbtcompat.PluginCompat.toFile(attr)
    f -> s"/app/${f.getName}"
  }.toMap
  val classpathString = libFiles.values.mkString(":") + ":" + jarTarget
  new Dockerfile {
    from("eclipse-temurin:17-jre")
    arg("buildArgument1")
    arg("buildArgument2")
    arg("buildArgument3", Some("default Value3"))
    env(Map(
      "buildArgument1" -> "$buildArgument1",
      "buildArgument2" -> "$buildArgument2",
      "buildArgument3" -> "$buildArgument3"
    ))

    libFiles.foreach {
      case (source, destination) =>
        copy(source, destination)
    }
    copy(jarFile, jarTarget)
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

// Set a custom image name
docker / imageNames := Def.uncached {
  val imageName = ImageName(namespace = Some(organization.value), repository = name.value, tag = Some("v" + version.value))
  Seq(imageName, imageName.copy(tag = Some("latest")))
}

docker / dockerBuildArguments := Map(
  "buildArgument1" -> "value 1",
  "buildArgument2" -> "value$2"
)

val check = taskKey[Unit]("Check")

check := Def.uncached {
  val names = (docker / imageNames).value
  names.foreach { imageName =>
    val process = scala.sys.process.Process("docker", Seq("run", "--rm", imageName.toString))
    val out = process.!!
    if (out.trim != "value 1 value$2 default Value3") sys.error("Unexpected output: " + out)
  }
}
