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
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  /*
  * Here I add subclasses, controllers, and moves.
  *   - subclasses are defined in archway/ArchwayDomain.scala,
  *   - controllers are defined in archway/controllers.scala,
  *   - moves are defined in archway/game.scala.
  */
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))

      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(column, complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](controller('AcesUpPile, complete))
      .addJob[CompilationUnit](controller('KingsDownPile, complete))
      .addJob[CompilationUnit]('AcesUpPileClass)
      .addJob[CompilationUnit]('KingsDownPileClass)
      .addJob[CompilationUnit]('AcesUpPileViewClass)
      .addJob[CompilationUnit]('KingsDownPileViewClass)
      //
      .addJob[CompilationUnit](move('ReserveToTableau :&: move.generic, complete))
      .addJob[CompilationUnit](move('ReserveToFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('TableauToFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('TableauToKingsFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('ReserveToKingsFoundation :&: move.generic, complete))

      // interesting note: We need to support release in tableau, when it comes from a reserve, but
      // since we can be having press comeo OUT of a tableau (destination: foundations) we have to
      // have move to deny the tableau to tableau.
      .addJob[CompilationUnit](move('TableauToTableau :&: move.generic, complete))

      .addJob[CompilationUnit](move('ReserveToTableau :&: move.potential, complete))
      .addJob[CompilationUnit](move('ReserveToFoundation :&: move.potential, complete))
      .addJob[CompilationUnit](move('TableauToFoundation :&: move.potential, complete))
      .addJob[CompilationUnit](move('TableauToKingsFoundation :&: move.potential, complete))
      .addJob[CompilationUnit](move('ReserveToKingsFoundation :&: move.potential, complete))



  // Find the Archway Variation.
  lazy val results = Results.addAll(jobs.run())

}
