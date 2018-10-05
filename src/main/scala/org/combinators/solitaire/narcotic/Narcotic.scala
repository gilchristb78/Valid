package org.combinators.solitaire.narcotic

import javax.inject.Inject
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.webjars.play.WebJarsUtil
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

class Narcotic @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  val solitaire:Solitaire = narcotic

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  lazy val controllerAddress: String = solitaire.name.toLowerCase

  //
//  lazy val combinatorComponents = Gamma.combinatorComponents
//  lazy val jobs =
//    Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
//      .addJob[CompilationUnit](constraints(complete))
//      .addJob[CompilationUnit](controller(deck, complete))
//      .addJob[CompilationUnit](controller(pile, complete))
//      .addJob[CompilationUnit](move('RemoveAllCards :&: move.generic, complete))
//      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
//      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
//      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
//
//      // only need potential moves for those that are DRAGGING...
//      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
//
//
//   lazy val results = EmptyResults().addAll(jobs.run())

//  lazy val controllerAddress: String = solitaire.name.toLowerCase
}
