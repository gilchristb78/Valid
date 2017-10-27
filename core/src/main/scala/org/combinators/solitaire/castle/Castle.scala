package org.combinators.solitaire.castle

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.WebJarsUtil

// domain
import domain._


class Castle @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  /** Defined in Game trait. */
  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Castle))
  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = it.next()

  /** Domain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new CastleDomain(s) with controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation :&: 'Solvable)
    .addJob[CompilationUnit]('RowClass)
    .addJob[CompilationUnit]('Controller('Row))
    .addJob[CompilationUnit]('Controller('Pile))

    .addJob[CompilationUnit]('Move('MoveCard :&: 'GenericMove, 'CompleteMove))
    .addJob[CompilationUnit]('Move('BuildCard :&: 'GenericMove, 'CompleteMove))

    .addJob[CompilationUnit]('Move('MoveCard :&: 'PotentialMove, 'CompleteMove))
    .addJob[CompilationUnit]('Move('BuildCard :&: 'PotentialMove, 'CompleteMove))
//    .addJob[CompilationUnit]('WastePileClass)
//    .addJob[CompilationUnit]('WastePileViewClass)
//      .addJob[CompilationUnit]('Controller('Deck))
//      .addJob[CompilationUnit]('Controller('Column))

  lazy val results:Results = Results.addAll(jobs.run())

}
