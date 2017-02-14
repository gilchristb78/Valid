package org.combinators.solitaire.stalactites

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS

class Stalactites @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repository = new Game with Moves with StalactitesColumnController with  FoundationPileController with ColumnToPileMoves {}
  lazy val Gamma = ReflectedRepository(repository)

//  println (Gamma.combinators.foreach( { 
//    case (combinatorName, combinatorType) => def funct(combinatorType:Type) : Int = {
//      combinatorType match { 
//        case 
//      }
//    }
//  })
//  
  // Map[TypeName, NumberOfUsages]
  // get the usages of 'SolitaireVariation.
  //
  // then get count
  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation :&: 'IncrementConcept))
      .add(Gamma.inhabit[CompilationUnit]('Controller('StalactitesColumn)))
      //.add(Gamma.inhabit[CompilationUnit]('Controller('ReservePile)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('FoundationPile)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove)))
}