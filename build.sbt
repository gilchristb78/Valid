import play.sbt.PlayLayoutPlugin
import play.twirl.sbt.SbtTwirl
import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(
  version := "1.0.0-SNAPSHOT",
  organization := "org.combinators",

  javacOptions ++= Seq("-source", "8"),

  scalaVersion := "2.12.4",

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases"),
      Resolver.sbtPluginRepo("releases")
  ),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions"
  ),

  libraryDependencies ++= Seq(
    "org.combinators" %% "cls-scala" % "2.0.0-RC1",
    "org.combinators" %% "templating" % "1.0.0-RC1+4-ca285511",
    "org.combinators" %% "cls-scala-presentation-play-git" % "1.0.0-RC1+8-63d5cf0b",
    "org.scalactic" %% "scalactic" % "3.0.1" % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    // https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
    "javax.xml.bind" % "jaxb-api" % "2.3.1",
    "ch.qos.logback" % "logback-classic" % "1.2.6",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",

    guice
  )
)

lazy val root = (Project(id = "nextgen-solitaire", base = file(".")))
  .settings(commonSettings: _*)
  .enablePlugins(SbtTwirl)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    moduleName := "nextgen-solitaire",

    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(
      sourceDirectory.value / "main" / "java-templates",
      sourceDirectory.value / "main" / "python-templates",
      baseDirectory.value / "generated" / "src" / "main" / "java"
    ),
    TwirlKeys.templateFormats += ("java" -> "org.combinators.templating.twirl.JavaFormat"),
    TwirlKeys.templateFormats += ("py" -> "org.combinators.templating.twirl.PythonFormat"),
    TwirlKeys.templateImports := Seq(),
    TwirlKeys.templateImports += "org.combinators.templating.twirl.Java",
    TwirlKeys.templateImports += "org.combinators.templating.twirl.Python",
    TwirlKeys.templateImports += "com.github.javaparser.ast._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.body._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.comments._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.expr._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.stmt._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.`type`._",

    PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value,

    unmanagedJars in Compile += file("demo/standAlone.jar")
  )

