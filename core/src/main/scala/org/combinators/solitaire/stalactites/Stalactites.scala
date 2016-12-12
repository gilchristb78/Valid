package org.combinators.solitaire.stalactites

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS

class Stalactites @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repository = new Game {}
  lazy val Gamma = ReflectedRepository(repository)

  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation))
}