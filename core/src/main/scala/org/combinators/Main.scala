package org.combinators.solitaire.freecell

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit

// strange name-clash with 'controllers'. Compiles but in eclipse shows errors :)
import _root_.controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import _root_.java.nio.file._                              // overloaded so go to _root_
import com.github.javaparser.ast.stmt.Statement

class Main @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repository = new Game with ColumnMoves with PileMoves with ColumnController with PileController {}
  lazy val Gamma = ReflectedRepository(repository)

  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('Main ))
   
}
