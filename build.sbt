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
    "de.tu_dortmund.cs.ls14" %% "cls-scala" % "1.3.2-SNAPSHOT",
    "org.scalactic" %% "scalactic" % "3.0.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    guice
  )

)

lazy val infrastructure = (Project(id = "templating", base = file("templating")))
  .settings(commonSettings: _*)
  .enablePlugins(SbtTwirl)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    moduleName := "templating",

    libraryDependencies ++= Seq(
      "com.github.javaparser" % "javaparser-core" % "3.5.3",
      "org.apache.commons" % "commons-lang3" % "3.6",
      "com.typesafe.play" %% "twirl-api" % "1.3.7",
      "com.typesafe.play" %% "play" % "2.6.5",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.8.0.201706111038-r",
      "org.webjars" %% "webjars-play" % "2.6.1",
      "org.webjars" % "bootstrap" % "3.3.7-1"
    ),
    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(sourceDirectory.value / "main" / "html-templates")
  )

lazy val javaInfrastructure = (Project(id = "java-templating", base = file("java-templating")))
  .settings(commonSettings: _*)
  .dependsOn(infrastructure)
  .enablePlugins(SbtTwirl)
  .settings(
    moduleName := "java-templating",

    libraryDependencies ++= Seq(
      "com.github.javaparser" % "javaparser-core" % "3.4.0",
      "org.apache.commons" % "commons-lang3" % "3.6",
      "com.typesafe.play" %% "twirl-api" % "1.3.7"
    )
  )


lazy val pythonInfrastructure = (Project(id = "python-templating", base = file("python-templating")))
  .settings(commonSettings: _*)
  .dependsOn(infrastructure)
  .enablePlugins(SbtTwirl)
  .settings(
    moduleName := "python-templating",

    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-lang3" % "3.6",
      "com.typesafe.play" %% "twirl-api" % "1.3.7"
    )
  )

lazy val core = (Project(id = "nextgen-solitaire", base = file("core")))
  .settings(commonSettings: _*)
  .dependsOn(infrastructure)
  .dependsOn(javaInfrastructure)
  .dependsOn(pythonInfrastructure)
  .enablePlugins(SbtTwirl)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    moduleName := "nextgen-solitaire",

    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(
      sourceDirectory.value / "main" / "java-templates",
      sourceDirectory.value / "main" / "python-templates"
    ),
    TwirlKeys.templateFormats += ("java" -> "de.tu_dortmund.cs.ls14.twirl.JavaFormat"),
    TwirlKeys.templateFormats += ("py" -> "de.tu_dortmund.cs.ls14.twirl.PythonFormat"),
    TwirlKeys.templateImports := Seq(),
    TwirlKeys.templateImports += "de.tu_dortmund.cs.ls14.twirl.Java",
    TwirlKeys.templateImports += "de.tu_dortmund.cs.ls14.twirl.Python",
    TwirlKeys.templateImports += "com.github.javaparser.ast._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.body._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.comments._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.expr._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.stmt._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.`type`._",

    PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value
  )


lazy val root = (Project(id = "nextgen-solitaire-root", base = file(".")))
  .settings(commonSettings: _*)
  .aggregate(infrastructure, javaInfrastructure, pythonInfrastructure, core)
  .settings(
    moduleName := "nextgen-solitaire-root"
  )

