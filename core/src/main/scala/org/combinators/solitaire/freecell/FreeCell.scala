package org.combinators.solitaire.freecell

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit

// strange name-clash with 'controllers'. Compiles but in eclipse shows errors :)
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS

class FreeCell @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repository = new Game with ColumnMoves with PileMoves with ColumnController with PileController {}
  lazy val Gamma = ReflectedRepository(repository)

  lazy val combinators = Gamma.combinators
  lazy val results =
    Results
      .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation ))
//      .add(Gamma.inhabit[CompilationUnit]('ShortCut))
      .add(Gamma.inhabit[CompilationUnit]('Controller('FreeCellColumn)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('FreePile)))
      .add(Gamma.inhabit[CompilationUnit]('Controller('HomePile)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToColumn :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToColumn :&: 'GenericMove, 'CompleteMove)))
      
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToColumn :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToColumn :&: 'GenericMove, 'CompleteMove)))
      
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToFreePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToFreePile :&: 'GenericMove, 'CompleteMove)))
      
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToHomePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToHomePile :&: 'GenericMove, 'CompleteMove)))
      
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToHomePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToHomePile :&: 'GenericMove, 'CompleteMove)))
      
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToFreePile :&: 'PotentialMove, 'CompleteMove)))
      .add(Gamma.inhabit[CompilationUnit]('Move('FreePileToFreePile :&: 'GenericMove, 'CompleteMove)))

      //
      
      
      //.add(Gamma.inhabit[CompilationUnit]('Move('FreeCellColumnToColumn, 'CompleteMove)))
}

// any way to do partial intermediate step (i.e., 
//import com.github.javaparser.ast.body.{BodyDeclaration}
