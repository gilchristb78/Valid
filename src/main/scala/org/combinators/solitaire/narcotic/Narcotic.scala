package org.combinators.solitaire.narcotic

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController}
import org.webjars.play.WebJarsUtil
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

// domain
import domain._

class Narcotic @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle){

  val s:Solitaire = new domain.narcotic.Domain()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(s) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](move('RemoveAllCards :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))

      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))


   lazy val results = EmptyResults().addAll(jobs.run())

}
