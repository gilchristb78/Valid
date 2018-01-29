package org.combinators.solitaire.idiot

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
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

  // DOESN'T WORK. SHOULD BE ABLE TO ? What is problem with implicit parameter?
//
//  // invoke the proper one
//  lazy val targets:Seq[Constructor] = Synthesizer.allTargets(s)
//
//  // base: Always present, so can take it out
//  lazy val base1 = Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
//
//  // map each of the known targets to a specific inhabitation, and then chain together via adding
//  lazy val jobs:Seq[Gamma.InhabitationBatchJob] = targets.map(x => Gamma.InhabitationBatchJob[CompilationUnit](x))
//  lazy val results:Results = (EmptyResults().addAll(base1.run()) /: jobs)((head, next) => head.addAll(next.run()))

  lazy val base1 = Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
  lazy val results:Results = EmptyResults().addAll(base1.run())

//  lazy val jobs =
//    Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
//      .addJob[CompilationUnit](constraints(complete))
//      .addJob[CompilationUnit](controller(deck, complete))
//      .addJob[CompilationUnit](controller(column, complete))
//      .addJob[CompilationUnit](move('RemoveCard :&: move.generic, complete))
//      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
//      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
//
//      // only need potential moves for those that are DRAGGING...
//      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
//
//  lazy val results = EmptyResults().addAll(jobs.run())

}
