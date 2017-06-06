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

   // see what's in there.
//   for ((k,v) <- updated.combinators) printf("key: %s, value: %s\n", k, v)


class FreeCell @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('FreeCellVariation)
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()
  
  // FreeCellDomain is base class for the solitaire variation. Note that this class is used (essentially)
  // as a placeholder for the solitaire val, which can then be referred to anywhere as needed.
  lazy val repository = new FreeCellDomain(s) with ColumnMoves with PileMoves with ColumnController with PileController {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinators = Gamma.combinators

  // also make sure to synthesize inhabitation requests
 
  // key is to get variation in place first.
  // NOTE: How to stage these multiple times so I don't have to bundle everything up
  // together. That is, I want to first inhabit SolitaireVariation, then go and 
  // inhabit the controllers, then inhabit all the moves, based upon the domain model.
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('Controller('Column))   // FCC
      .addJob[CompilationUnit]('Controller('FreePile))
      .addJob[CompilationUnit]('Controller('HomePile))
      .addJob[CompilationUnit]('Move('ColumnToColumn :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToColumn :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToColumn :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToColumn :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToFreePile :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToFreePile :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToHomePile :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ColumnToHomePile :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToHomePile :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToHomePile :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToFreePile :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('FreePileToFreePile :&: 'GenericMove, 'CompleteMove))
      //.addJob[CompilationUnit]('RuntimeCombinatorClass)

  lazy val results = Results.addAll(jobs.run())

      // Here is how you launch directly and it gets placed into file
      //.add(Gamma.inhabit[Seq[Statement]]('Something), Paths.get("somePlace"))
      //
      
}

