package org.combinators.solitaire.castle

import javax.inject.Inject

import domain.castle.Domain
import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._

// domain
import domain._

class Castle @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val s:Solitaire = new Domain()

  /** Domain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new CastleDomain(s) with controllers {}
  import repository._

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)


  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
      .addJob[CompilationUnit]('RowClass)
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](controller('Row, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildCard :&: move.generic, complete))

      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
      .addJob[CompilationUnit](move('BuildCard :&: move.potential, complete))

  lazy val results:Results = Results.addAll(jobs.run())


}
