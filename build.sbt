import play.sbt.PlayLayoutPlugin
import play.twirl.sbt.SbtTwirl

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "org.combinators",
  
  scalaVersion := "2.12.3",

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
  ),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions"
  ),

  libraryDependencies ++= Seq(
    "de.tu_dortmund.cs.ls14" %% "cls-scala" % "1.3.1-SNAPSHOT"
  )

)

lazy val infrastructure = (Project(id = "java-templating", base = file("java-templating")))
  .settings(commonSettings: _*)
  .enablePlugins(SbtTwirl)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    moduleName := "java-templating",

    libraryDependencies ++= Seq(
      "com.github.javaparser" % "javaparser-core" % "2.5.1",
      "org.apache.commons" % "commons-lang3" % "3.6",
      "com.typesafe.play" %% "twirl-api" % "1.3.7",
      "com.typesafe.play" %% "play" % "2.6.5",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.8.0.201706111038-r",
      "org.webjars" %% "webjars-play" % "2.6.1",
      "org.webjars" % "bootstrap" % "3.3.7-1"
    ),
    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(sourceDirectory.value / "main" / "html-templates")
  )


lazy val core = (Project(id = "nextgen-solitaire", base = file("core")))
  .settings(commonSettings: _*)
  .dependsOn(infrastructure)
  .enablePlugins(SbtTwirl)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    moduleName := "nextgen-solitaire",

    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(sourceDirectory.value / "main" / "java-templates"),
    TwirlKeys.templateFormats += ("java" -> "de.tu_dortmund.cs.ls14.twirl.JavaFormat"),
    TwirlKeys.templateImports := Seq(),
    TwirlKeys.templateImports += "de.tu_dortmund.cs.ls14.twirl.Java",
    TwirlKeys.templateImports += "com.github.javaparser.ast._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.body._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.comments._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.expr._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.stmt._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.`type`._",

    PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value
  )


lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(infrastructure, core)
  .settings(
    moduleName := "nextgen-solitaire-root"
  )

