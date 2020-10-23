lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "codes.mostly",
      scalaVersion := "2.13.3"
    )),
    name := "scalatest-example"
  )

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
libraryDependencies += "org.scalatest"          %% "scalatest" % "3.2.2" % Test
