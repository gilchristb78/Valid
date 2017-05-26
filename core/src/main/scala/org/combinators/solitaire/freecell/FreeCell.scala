package org.combinators.solitaire.freecell

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import com.github.javaparser.ast.stmt.Statement
// strange name-clash with 'controllers'. Compiles but in eclipse shows errors :)
import _root_.controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import org.combinators.solitaire.shared._

// domain
import domain._

class AnotherCombinator(idx:Int)  {
        def apply() : CompilationUnit = {
           Java(s"""package org.combinators.solitaire;
                    public class SecondOne$idx {}""".stripMargin).compilationUnit()
        }

        val semanticType: Type = 'SecondOne
      }

// all I care about is that this is a SolitaireDomain extension
// object sample {
//  def initCombinators[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
//      ReflectedRepository[G] = {
//    val f = s.getFoundation()
//    val it = f.iterator()
//    var idx = 0
//    var updated = gamma
//    while (it.hasNext()) {
//      val p = it.next()
//      idx = idx + 1 
     
     // de-duplication eliminates this from working... 
//     val newCombinator = new AnotherCombinator(idx)
 
//     updated = updated.addCombinator(newCombinator)
 
//     println (p.toString() + ":" + idx)
//    } 

 

   // see what's in there.
//   for ((k,v) <- updated.combinators) printf("key: %s, value: %s\n", k, v)

   //updated 
//  updated 
//  }
//}

class FreeCell @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('FreeCellVariation)
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()
  
  // FreeCellDomain is base class for the solitaire variation. Note that this class is used (essentially)
  // as a placeholder for the solitaire val, which can then be referred to anywhere as needed.
  lazy val repository = new FreeCellDomain(s) with ColumnMoves with PileMoves with ColumnController with PileController {}
  //lazy val Gamma = sample.initCombinators(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinators = Gamma.combinators

  // also make sure to synthesize inhabitation requests
 
  // key is to get variation in place first.
  // NOTE: How to stage these multiple times so I don't have to bundle everything up
  // together. That is, I want to first inhabit SolitaireVariation, then go and 
  // inhabit the controllers, then inhabit all the moves, based upon the domain model.
  lazy val results = Results
    .add(Gamma.inhabit[CompilationUnit]('SolitaireVariation ))
    .add(Gamma.inhabit[CompilationUnit]('Controller('FreeCellColumn)))
    .add(Gamma.inhabit[CompilationUnit]('Controller('FreePile)))
    .add(Gamma.inhabit[CompilationUnit]('Controller('HomePile)))
    .add(Gamma.inhabit[CompilationUnit]('Hack22))

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
//      .add(Gamma.inhabit[CompilationUnit]('SecondOne))
      
      // Here is how you launch directly and it gets placed into file
      //.add(Gamma.inhabit[Seq[Statement]]('Something), Paths.get("somePlace"))
      //
      
      
      //.add(Gamma.inhabit[CompilationUnit]('Move('FreeCellColumnToColumn, 'CompleteMove)))
}

// any way to do partial intermediate step (i.e., 
//import com.github.javaparser.ast.body.{BodyDeclaration}
