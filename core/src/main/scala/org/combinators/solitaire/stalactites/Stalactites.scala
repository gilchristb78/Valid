package org.combinators.solitaire.stalactites

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.combinators.TypeNameStatistics
import org.webjars.play.WebJarsUtil




class Stalactites @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {
  lazy val repository = new Game with Moves with StalactitesColumnController with  FoundationPileController with ReservePileController with PileToPileMoves with ColumnToPileMoves {}
  lazy val Gamma = ReflectedRepository(repository)
  lazy val statistics = new TypeNameStatistics(Gamma)
  //println(statistics.overview)
  //println(statistics.warnings)
  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('increment('SolitaireVariation)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('StalactitesColumn)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('ReservePile)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('FoundationPile)))
      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove)))))
      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove)))))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToReservePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToReservePile :&: 'GenericMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove)))))
      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove)))))
      .add(Gamma.inhabit[CompilationUnit]('Move('ReservePileToReservePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ReservePileToReservePile :&: 'GenericMove, 'CompleteMove)))
}