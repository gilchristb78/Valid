package org.combinators.solitaire.idiot

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


class Idiot @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {

  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('Variation('Idiot))
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new IdiotDomain(s) with Controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinators = Gamma.combinators
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('Controller('Deck))
      .addJob[CompilationUnit]('Controller('Column))
//.addJob[CompilationUnit]('Debug)
      .addJob[CompilationUnit]('Move ('RemoveSingleCard, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToColumn :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToColumn :&: 'GenericMove,   'CompleteMove))
      .addJob[CompilationUnit]('Move('DeckToColumn   :&: 'GenericMove,   'CompleteMove))

  lazy val results = Results.addAll(jobs.run())

}
