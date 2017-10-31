package org.combinators.solitaire.klondike

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.klondike.Domain
import org.webjars.play.{RequireJS, WebJarsUtil}

// domain
import domain._


class Klondike @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  /** Defined in Game trait. */
//  lazy val repositoryPre = new game {}
//  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)
//
//  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Klondike))
//  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
//  lazy val s:Solitaire = it.next()

  val s:Solitaire = new Domain()

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

  lazy val results:Results = Results.addAll(jobs.run())

}
