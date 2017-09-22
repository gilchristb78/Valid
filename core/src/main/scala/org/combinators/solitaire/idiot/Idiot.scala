package org.combinators.solitaire.idiot

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.WebJarsUtil

class Idiot @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {
  lazy val repository = new Game with Moves with ColumnController {}
  lazy val Gamma = ReflectedRepository(repository)

  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation))
      //.add(Gamma.inhabit[CompilationUnit]('ShortCut))
      .add(Gamma.inhabit[CompilationUnit]('Controller('Deck)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('IdiotColumn)))
      .add(Gamma.inhabit[CompilationUnit]('MoveRemoveCards))
      .add(Gamma.inhabit[CompilationUnit]('ColumnMove))
      //.add(Gamma.inhabit[CompilationUnit]('Move('ColumnToColumn :&: 'GenericMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('DealStacks, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ResetDeck, 'CompleteMove)))
}