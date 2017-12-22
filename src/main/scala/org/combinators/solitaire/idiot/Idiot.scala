package org.combinators.solitaire.idiot

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

// domain
import domain._


class Idiot @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  val s:Solitaire = new domain.idiot.Domain()

  // semantic types are embedded/defined within the repository, so we need to
  // import them all for use.
  lazy val repository = new gameDomain(s) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

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

  lazy val results = EmptyResults().addAll(jobs.run())

}
