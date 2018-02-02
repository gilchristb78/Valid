package org.combinators.solitaire.freecell

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

class FreeCell @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries  {

  val solitaire:Solitaire = new domain.freeCell.Domain()

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
//  /** This needs to be defined, and it is set from Gamma. */
//  lazy val combinatorComponents = Gamma.combinatorComponents
//
//  // also make sure to synthesize inhabitation requests
//
//  // key is to get variation in place first.
//  // NOTE: How to stage these multiple times so I don't have to bundle everything up
//  // together. That is, I want to first inhabit SolitaireVariation, then go and
//  // inhabit the controllers, then inhabit all the moves, based upon the domain model.
//  lazy val jobs =
//  Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
//    .addJob[CompilationUnit](constraints(complete))
//    .addJob[CompilationUnit](controller('HomePile, complete))
//    .addJob[CompilationUnit](controller(column, complete))
//    .addJob[CompilationUnit](controller('FreePile, complete))
//    .addJob[CompilationUnit]('FreePileClass)
//    .addJob[CompilationUnit]('FreePileViewClass)
//    .addJob[CompilationUnit]('HomePileClass)
//    .addJob[CompilationUnit]('HomePileViewClass)
//    .addJob[CompilationUnit](move('ShuffleFreePile :&: move.generic, complete))
//    .addJob[CompilationUnit](move('ShuffleFreePile :&: move.potential, complete))
//    .addJob[CompilationUnit](move('PlaceColumn :&: move.generic, complete))
//    .addJob[CompilationUnit](move('PlaceColumn :&: move.potentialMultipleMove, complete))
//    .addJob[CompilationUnit](move('BuildColumn :&: move.generic, complete))
//    .addJob[CompilationUnit](move('BuildColumn :&: move.potentialMultipleMove, complete))
//    .addJob[CompilationUnit](move('BuildFreePileCard :&: move.generic, complete))
//    .addJob[CompilationUnit](move('BuildFreePileCard :&: move.potential, complete))
//    .addJob[CompilationUnit](move('PlaceFreePileCard :&: move.generic, complete))
//    .addJob[CompilationUnit](move('PlaceFreePileCard :&: move.potential, complete))
//    .addJob[CompilationUnit](move('MoveColumn :&: move.generic, complete))
//    .addJob[CompilationUnit](move('MoveColumn :&: move.potentialMultipleMove, complete))
//
//
//  lazy val results:Results = EmptyResults().addAll(jobs.run())
//
//  lazy val controllerAddress: String = solitaire.name.toLowerCase
//
//
}

