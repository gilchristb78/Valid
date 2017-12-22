package org.combinators.solitaire.klondike

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

class Klondike @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  /** Defined in Game trait. */
//  lazy val repositoryPre = new game {}
//  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)
//
//  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Klondike))
//  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
//  lazy val s:Solitaire = it.next()

  val s:Solitaire = new domain.klondike.Domain()

  /** Domain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(s) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
    .addJob[CompilationUnit](constraints(complete))
    .addJob[CompilationUnit](controller(buildablePile, complete))
    .addJob[CompilationUnit](controller(pile, complete))
    .addJob[CompilationUnit](controller(deck, complete))
    .addJob[CompilationUnit](controller('WastePile, complete))
    .addJob[CompilationUnit]('WastePileClass)
    .addJob[CompilationUnit]('WastePileViewClass)
//
    .addJob[CompilationUnit](move('MoveColumn :&: move.generic, complete))
    .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
    .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
    .addJob[CompilationUnit](move('FlipCard :&: move.generic, complete))
    .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
    .addJob[CompilationUnit](move('BuildFoundation :&: move.generic, complete))
    .addJob[CompilationUnit](move('BuildFoundationFromWaste :&: move.generic, complete))

    .addJob[CompilationUnit](move('MoveColumn :&: move.potentialMultipleMove, complete))

  //      .addJob[CompilationUnit]('Controller('Column))

  lazy val results:Results = EmptyResults().addAll(jobs.run())

}
