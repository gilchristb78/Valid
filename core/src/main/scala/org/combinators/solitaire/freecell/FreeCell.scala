package org.combinators.solitaire.freecell

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.InhabitationResult
import org.combinators.TypeNameStatistics
// strange name-clash with 'controllers'. Compiles but in eclipse shows errors :)
import _root_.controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS
import org.combinators.solitaire.shared._

// domain
import domain._

//   for ((k,v) <- updated.combinators) printf("key: %s, value: %s\n", k, v)

class FreeCell @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {
  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('FreeCell))
  lazy val it:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = it.next()
 
  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(s) with columnController with pilecontroller {}
  lazy val Gamma = {
    val r = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)
    println(new TypeNameStatistics(r).warnings)
    r
  }

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents


  // also make sure to synthesize inhabitation requests
 
  // key is to get variation in place first.
  // NOTE: How to stage these multiple times so I don't have to bundle everything up
  // together. That is, I want to first inhabit SolitaireVariation, then go and 
  // inhabit the controllers, then inhabit all the moves, based upon the domain model.
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      .addJob[CompilationUnit]('Controller('Column))   
      .addJob[CompilationUnit]('Controller('FreePile))
      .addJob[CompilationUnit]('Controller('HomePile))
      .addJob[CompilationUnit]('HomePileClass)
      .addJob[CompilationUnit]('FreePileClass)
      .addJob[CompilationUnit]('HomePileViewClass)
      .addJob[CompilationUnit]('FreePileViewClass)
      .addJob[CompilationUnit]('Move('MoveColumn :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('BuildFreePileCard  :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('PlaceColumn :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('BuildColumn :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('PlaceFreePileCard :&: 'GenericMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('ShuffleFreePile :&: 'GenericMove, 'CompleteMove))
      //.addJob[CompilationUnit]('RuntimeCombinatorClass)
//      .addJob[CompilationUnit]('Move('MoveColumn :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('BuildFreePileCard :&: 'PotentialMove, 'CompleteMove))
//      .addJob[CompilationUnit]('Move('PlaceColumn :&: 'PotentialMove, 'CompleteMove))
      .addJob[CompilationUnit]('Move('BuildColumn :&: 'PotentialMove, 'CompleteMove))
//      .addJob[CompilationUnit]('Move('PlaceFreePileCard :&: 'PotentialMove, 'CompleteMove))
//      .addJob[CompilationUnit]('Move('ShuffleFreePile :&: 'PotentialMove, 'CompleteMove))

 
  lazy val results = Results.addAll(jobs.run())

      // Here is how you launch directly and it gets placed into file
      //.add(Gamma.inhabit[Seq[Statement]]('Something), Paths.get("somePlace"))
      //
      
}

