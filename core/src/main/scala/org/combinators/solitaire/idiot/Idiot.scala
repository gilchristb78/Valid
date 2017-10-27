package org.combinators.solitaire.idiot

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import domain.idiot.Domain
import org.webjars.play.WebJarsUtil

// domain
import domain._


class Idiot @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val s:Solitaire = new Domain()

  // semantic types are embedded/defined within the repository, so we need to
  // import them all for use.
  lazy val repository = new gameDomain(s) with controllers {}
  import repository._
  lazy val Gamma:ReflectedRepository[gameDomain] = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit](controller(column, complete))
      .addJob[CompilationUnit](move('RemoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))

      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))

  lazy val results:Results = Results.addAll(jobs.run())

}
