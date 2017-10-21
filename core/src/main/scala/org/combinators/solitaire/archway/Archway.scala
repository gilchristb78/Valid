package org.combinators.solitaire.archway

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import domain._
import javax.inject.Inject

import domain.archway.Domain
import org.combinators.solitaire.shared._
import org.webjars.play.WebJarsUtil

/** Loads and runs the combinators to generate the Archway variation.
  *
  * @param webJars
  */
class Archway @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val solitaire = new Domain()

  lazy val repository = new ArchwayDomain(solitaire) with Controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  /*
  * Here I add subclasses, controllers, and moves.
  *   - subclasses are defined in archway/gameDomain.scala,
  *   - controllers are defined in archway/controllers.scala,
  *   - moves are defined in archway/game.scala.
  */
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation :&: 'Solvable)
      .addJob[CompilationUnit]('AcesUpPileClass)
      .addJob[CompilationUnit]('KingsDownPileClass)
      .addJob[CompilationUnit]('AcesUpPileViewClass)
      .addJob[CompilationUnit]('KingsDownPileViewClass)

      .addJob[CompilationUnit]('Controller('AcesUpPile))
      .addJob[CompilationUnit]('Controller('KingsDownPile))
      .addJob[CompilationUnit]('Controller('Column))
      .addJob[CompilationUnit]('Controller('Pile))

      /*
      * The left-hand symbols in the 'Move tuple are the real names of the classes
      * specified as strings in the `SolitaireMoveHanlder(...)` objects used in
      * ArchwayRules in game.scala.
      */
      .addJob[CompilationUnit]('Move ('ReserveToTableau :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('ReserveToFoundation :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('TableauToFoundation :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('TableauToKingsFoundation :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('ReserveToKingsFoundation :&: 'GenericMove, 'CompleteMove))

      .addJob[CompilationUnit]('Move ('ReserveToTableau :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('ReserveToFoundation :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('TableauToFoundation :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('TableauToKingsFoundation :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move ('ReserveToKingsFoundation :&: 'PotentialMove, 'CompleteMove))

      // interesting note: We need to support release in tableau, when it comes from a reserve, but
      // since we can be having press comeo OUT of a tableau (destination: foundations) we have to
      // have move to deny the tableau to tableau.
      .addJob[CompilationUnit]('Move ('TableauToTableau :&: 'GenericMove, 'CompleteMove))

  // Find the Archway Variation.
  lazy val results = Results.addAll(jobs.run())

}
