package org.combinators.solitaire.castle

import javax.inject.Inject

import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

// domain
import domain._

class Castle @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  val s:Solitaire = new domain.castle.Domain()

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
      .addJob[CompilationUnit](controller(row, complete))
      .addJob[CompilationUnit](move('MoveRow :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildRow :&: move.generic, complete))

      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveRow :&: move.potentialMultipleMove, complete))
      .addJob[CompilationUnit](move('BuildRow :&: move.potentialMultipleMove, complete))

  lazy val results:Results = EmptyResults().addAll(jobs.run())


}
