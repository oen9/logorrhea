val app = crossProject.in(file(".")).settings(
  unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.5",
    "com.lihaoyi" %%% "upickle" % "0.4.4"
  ),
  scalaVersion := "2.12.2",
  name := "logorrhea"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.1"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.5"
  )
)

lazy val fastOptJSDev = TaskKey[Unit]("fastOptJSDev")
lazy val appJS = app.js
  .disablePlugins(RevolverPlugin)
  .enablePlugins(WorkbenchPlugin)
  .settings(
    fastOptJSDev := {

      // resources
      val targetRes = "../target/scala-2.12/classes/"
      IO.copyDirectory((resourceDirectory in Compile).value, new File(baseDirectory.value, targetRes))

      // logorrhea-fastopt.js
      val fastOptFrom = (fastOptJS in Compile).value.data
      val fastOptTo = new File(baseDirectory.value, targetRes + fastOptFrom.name)
      IO.copyFile(fastOptFrom, fastOptTo)

      // logorrhea-fastopt.js.map
      val mapFileName = fastOptFrom.name + ".map"
      val fastOptMapFrom = fastOptFrom.getParentFile / mapFileName
      val fastOptMapTo = new File(baseDirectory.value, targetRes + mapFileName)
      IO.copyFile(fastOptMapFrom, fastOptMapTo)
    }
//    ,(fastOptJS in Compile) := { // moving fastopt.js to classes/front-res/js/fastopt.js
//      val src =  (fastOptJS in Compile).value.data
//      val dest = crossTarget.value / "classes" / "front-res" / "js" / src.name
//      IO.move(src, dest)
//      Attributed.blank(dest)
//    }
  )

lazy val appJVM = app.jvm.settings(
  (resources in Compile) += (fullOptJS in (appJS, Compile)).value.data,
  (unmanagedResourceDirectories in Compile) += (resourceDirectory in (appJS, Compile)).value,
  target := baseDirectory.value / ".." / "target"
).enablePlugins(JavaAppPackaging)

disablePlugins(RevolverPlugin)
