package org.combinators.solitaire.idiot

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import de.tu_dortmund.cs.ls14.cls.interpreter.InhabitationResult
// domain
import domain._


class Idiot @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {

  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Idiot))
  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = it.next()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(s) with controllers {}
  lazy val Gamma:ReflectedRepository[gameDomain] = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation :&: 'Solvable)
      .addJob[CompilationUnit]('Controller('Deck))
      .addJob[CompilationUnit]('Controller('Column))
      .addJob[CompilationUnit]('Move('RemoveCard :&: 'GenericMove , 'CompleteMove))
      .addJob[CompilationUnit]('Move('MoveCard :&: 'GenericMove,   'CompleteMove))
      .addJob[CompilationUnit]('Move('MoveCard :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('DealDeck :&: 'GenericMove,   'CompleteMove))

  lazy val results:Results = Results.addAll(jobs.run())

}
