package org.combinators.solitaire.bigforty

import java.nio.file.Path
import javax.inject.Inject

import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.Persistable
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._

// domain
import domain._

class BigForty @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars){
  val s:Solitaire = new domain.bigforty.Domain()

  /** Domain for BigForty defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new gameDomain(s) with controllers {}
  import repository._

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit]('WastePileClass)
      .addJob[CompilationUnit]('WastePileViewClass)
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](controller(column, complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit](controller('WastePile, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveColumn :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundationFromWaste :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
//
//      // only need potential moves for those that are DRAGGING...
      .addJob[CompilationUnit](move('MoveColumn :&: move.potentialMultipleMove, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
      .addJob[CompilationUnit](move('BuildFoundation :&: move.potentialMultipleMove, complete))
      .addJob[CompilationUnit](move('BuildFoundationFromWaste :&: move.potential, complete))

  lazy val results:Results = Results.addAll(jobs.run())





}
