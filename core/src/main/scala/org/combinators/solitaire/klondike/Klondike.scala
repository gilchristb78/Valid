package org.combinators.solitaire.klondike

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.{RequireJS, WebJarsUtil}

// domain
import domain._


class Klondike @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  /** Defined in Game trait. */
  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Klondike))
  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = it.next()

  /** Domain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(s) with controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
    .addJob[CompilationUnit]('WastePileClass)
    .addJob[CompilationUnit]('WastePileViewClass)
//      .addJob[CompilationUnit]('Controller('Deck))
//      .addJob[CompilationUnit]('Controller('Column))

  lazy val results:Results = Results.addAll(jobs.run())

}
