package org.combinators.solitaire.freecell

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java

// strange name-clash with 'controllers'. Compiles but in eclipse shows errors :)
import _root_.controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import _root_.java.nio.file._                              // overloaded so go to _root_
import com.github.javaparser.ast.stmt.Statement

// domain
import domain._

class FreeCell @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('FreeCellVariation)
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()
  //Solitaire.setInstance(s)
  
  // We can generate any number of these possibilities by inspecting the domain model.
  // This is where the mapping comes from
  
  lazy val repository = new GameDomain(s) with ColumnMoves with PileMoves with ColumnController with PileController {}
  object RuntimeCombinator {
    def apply(): CompilationUnit = Java(s"""
      package org.combinators.solitaire;
      public class IAmRuntimeCreated {}
      """).compilationUnit()
    val semanticType: Type = 'RuntimeCombinatorClass
  }
  lazy val Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader).addCombinator("RuntimeCombinator", RuntimeCombinator)
  lazy val combinators = Gamma.combinators

  // key is to get variation in place first.
  // NOTE: How to stage these multiple times so I don't have to bundle everything up
  // together. That is, I want to first inhabit SolitaireVariation, then go and 
  // inhabit the controllers, then inhabit all the moves, based upon the domain model.
  lazy val results = Results
    .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation ))
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
      .add(Gamma.inhabit[CompilationUnit]('RuntimeCombinatorClass))
      
      // Here is how you launch directly and it gets placed into file
      //.add(Gamma.inhabit[Seq[Statement]]('Something), Paths.get("somePlace"))
      //
      
      
      //.add(Gamma.inhabit[CompilationUnit]('Move('FreeCellColumnToColumn, 'CompleteMove)))
}

// any way to do partial intermediate step (i.e., 
//import com.github.javaparser.ast.body.{BodyDeclaration}
