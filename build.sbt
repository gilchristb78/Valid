
lazy val commonSettings = Seq(
  version := "1.0",
  organization := "org.combinators",
  
  scalaVersion := "2.11.8",

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
  ),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions"
  )
)

lazy val infrastructure = (Project(id = "java-templating", base = file("java-templating")))
  .settings(commonSettings: _*)
  .settings(
    moduleName := "java-templating",

    libraryDependencies ++= Seq(
      "com.github.javaparser" % "javaparser-core" % "2.5.1",
      "org.apache.commons" % "commons-lang3" % "3.4",
      "com.typesafe.play" %% "twirl-api" % "1.2.0"
    )
  )


lazy val core = (Project(id = "nextgen-solitaire", base = file("core")))
  .settings(commonSettings: _*)
  .dependsOn(infrastructure)
  .enablePlugins(SbtTwirl)
  .settings(
    moduleName := "nextgen-solitaire",

    libraryDependencies ++= Seq(
      "de.tu_dortmund.cs.ls14" %% "cls-scala" % "1.0"
    ),

    sourceDirectories in (Compile, TwirlKeys.compileTemplates) := Seq(sourceDirectory.value / "main" / "java-templates"),
    //includeFilter in (Compile) := "*",
    //target in (Compile, TwirlKeys.compileTemplates) := sourceManaged.value,
    TwirlKeys.templateFormats += ("java" -> "de.tu_dortmund.cs.ls14.twirl.JavaFormat"),
    TwirlKeys.templateImports += "de.tu_dortmund.cs.ls14.twirl.Java",
    TwirlKeys.templateImports += "com.github.javaparser.ast._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.body._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.comments._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.expr._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.stmt._",
    TwirlKeys.templateImports += "com.github.javaparser.ast.`type`._"

  )


lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(infrastructure, core)
  .settings(
    moduleName := "nextgen-solitaire-root"
  )

