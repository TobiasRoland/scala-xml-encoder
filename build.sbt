inThisBuild(List(
  organization := "codes.mostly",
  homepage := Some(url("https://github.com/TobiasRoland/scala-xml-encoder")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "tobiasroland",
      "Tobias Roland",
      "tobias@mostly.codes",
      url("https://mostly.codes")
    )
  )
))


libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
libraryDependencies += "org.scalatest"          %% "scalatest" % "3.2.3" % Test
