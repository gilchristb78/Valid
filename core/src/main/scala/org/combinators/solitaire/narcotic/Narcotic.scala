package org.combinators.solitaire.narcotic

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import domain.narcotic.Domain
import org.combinators.solitaire.shared.SemanticTypes
import org.webjars.play.WebJarsUtil

// domain
import domain._

class Narcotic @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars){

  val s:Solitaire = new Domain()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(s) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](move('RemoveAllCards :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))

      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))


   lazy val results = Results.addAll(jobs.run())

}
