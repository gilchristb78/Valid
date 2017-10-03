package org.combinators.solitaire.narcotic

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.WebJarsUtil

// domain
import domain._

class Narcotic @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  lazy val repositoryPre:game = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('Narcotic))
  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = it.next()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository:gameDomain = new gameDomain(s) with controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('Controller('Deck))
        .addJob[CompilationUnit]('Controller('Pile))

      // To define RemoveFourCards, observe all parts that are necessary.
      .addJob[CompilationUnit]('Move('RemoveAllCards :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('DealDeck :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('MoveCard :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('MoveCard :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ResetDeck :&: 'GenericMove, 'CompleteMove))

   lazy val results = Results.addAll(jobs.run())

}
