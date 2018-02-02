package org.combinators.solitaire.castle

import javax.inject.Inject

import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

// domain
import domain._

class Castle @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  val solitaire:Solitaire = new domain.castle.Domain()

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new CastleDomain(solitaire) with controllers {}


  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  lazy val controllerAddress: String = solitaire.name.toLowerCase

  //  lazy val combinatorComponents = Gamma.combinatorComponents
//  lazy val jobs =
//    Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
//      .addJob[CompilationUnit]('RowClass)
//      .addJob[CompilationUnit](constraints(complete))
//      .addJob[CompilationUnit](controller(pile, complete))
//      .addJob[CompilationUnit](controller(row, complete))
//      .addJob[CompilationUnit](move('MoveRow :&: move.generic, complete))
//      .addJob[CompilationUnit](move('BuildRow :&: move.generic, complete))
//
//      // only need potential moves for those that are DRAGGING...
//      .addJob[CompilationUnit](move('MoveRow :&: move.potentialMultipleMove, complete))
//      .addJob[CompilationUnit](move('BuildRow :&: move.potentialMultipleMove, complete))
//
//  lazy val results:Results = EmptyResults().addAll(jobs.run())


}
