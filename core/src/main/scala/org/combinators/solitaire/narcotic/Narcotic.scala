package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController

class Narcotic extends InhabitationController {
  lazy val repository = new Game with Moves {}
  lazy val Gamma = ReflectedRepository(repository)

  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation))
      .add(Gamma.inhabit[CompilationUnit]('Controller('Deck)))
      .add(Gamma.inhabit[CompilationUnit]('MoveRemoveCards))
      .add(Gamma.inhabit[CompilationUnit]('PileMove))
      .add(Gamma.inhabit[CompilationUnit]('Controller('NarcoticPile)))
      .add(Gamma.inhabit[CompilationUnit]('Move('DealStacks, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ResetDeck, 'CompleteMove)))
}