package org.combinators.solitaire.narcotic

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import org.combinators.solitaire.shared._

// domain
import domain._

class Narcotic @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {

  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('Variation('Narcotic))
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new NarcoticDomain(s) with Controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('Controller('Deck))
      .addJob[CompilationUnit]('Controller('Pile))
      .addJob[CompilationUnit]('Move ('RemoveSingleCard, 'CompleteMove))

//    .add(Gamma.inhabit[CompilationUnit]('MoveRemoveCards))
//      .add(Gamma.inhabit[CompilationUnit]('PileMove))
//      .add(Gamma.inhabit[CompilationUnit]('Controller('NarcoticPile)))
//      .add(Gamma.inhabit[CompilationUnit]('Move('DealStacks, 'CompleteMove)))
//      .add(Gamma.inhabit[CompilationUnit]('Move('ResetDeck, 'CompleteMove)))

   lazy val results = Results.addAll(jobs.run())

}
