package org.combinators.solitaire

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.Solitaire
import org.combinators.solitaire.freecell.{game, gameDomain}
import org.scalatest._

class FreeCellTests extends FunSpec {

  // free-cell-stuff follows
  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply:InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation('FreeCell))
  lazy val iter:Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s:Solitaire = iter.next()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val fc_repository = new gameDomain(s)// with columnController  with pilecontroller {}
//  lazy val Gamma = {
//    val r = fc_repository.init(ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader), s)
//    println(new TypeNameStatistics(r).warnings)
//    r
//  }


  lazy val Gamma = ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader)

  describe("test FreeCell") {
    lazy val job =
      Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)

    // removes sizes and just gets the second part (i.e., the results)
    lazy val results = job.run().interpretedTerms.values.flatMap(_._2)

    describe ("when inhabiting solitaire variation") {
      it("ensure just one result") {
        assert(results.size == 1)
      }
      it("check correct class SolitaireVariation exists") {
        assert(results.head.getClassByName("FreeCell").isPresent)
      }
    }
//    job.run().    // If only one job, can omit  ._2.
//       interpretedTerms.values.flatMap(_._2).foreach {
//      inhabitant => assert (inhabitant.getChildNodes().toString())
  }




}
