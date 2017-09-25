package org.combinators.solitaire


import com.github.javaparser.ast.CompilationUnit
import org.scalatest._
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.interpreter._
import domain.Solitaire
import org.combinators.TypeNameStatistics
import org.combinators.solitaire.freecell.{columnController, game, gameDomain, pilecontroller}
import syntax._

class DynamicCombinatorTest extends FunSpec {
  class Repo {
    @combinator object NonSense {
      def apply: Int = 41
      val semanticType: Type = 'NonSense
    }
    @combinator object Show {
      def apply(x: Int): String = x.toString
      val semanticType = ('Sense =>: 'Sense) :&: ('NonSense =>: 'NonSense)
    }
  }

  object MakeSense {
    def apply(x: Int): Int = x + 1
    val semanticType: Type = 'NonSense =>: 'Sense
  }

  val repository = new Repo
  val result = ReflectedRepository(repository)

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


  describe("The augmented repository") {
    val augmentedResult = result.addCombinator(MakeSense)
    describe("when inhabiting NonSense") {
      val inhabitants = result.inhabit[String]('NonSense).interpretedTerms
      it("should find NonSense") {
        assert(!inhabitants.values.isEmpty)
        assert(inhabitants.index(0) == "41")
      }
    }
    describe("when inhabiting Sense") {
      val inhabitants = result.inhabit[String]('Sense).interpretedTerms
      it("should not find anything") {
        assert(inhabitants.values.isEmpty)
      }
    }
    describe("When dynamically agumented with MakeSense") {
      describe("when inhabiting NonSense") {
        val inhabitants = augmentedResult.inhabit[String]('NonSense).interpretedTerms
        describe("Should find NonSense") {
          assert(!inhabitants.values.isEmpty)
          assert(inhabitants.index(0) == "41")
        }
      }
      describe("when inhabiting Sense") {
        val inhabitants = augmentedResult.inhabit[String]('Sense).interpretedTerms
        it("should find 42") {
          assert(!inhabitants.values.isEmpty)
          assert(inhabitants.index(0) == "42")
        }
      }
    }
  }

  class IncrementCombinator(delta: Int, semantics: Type) {
    def apply(x: Int): Int = x + delta
    val semanticType = semantics
  }

  describe("The reflected repository with two IncrementCombinator instances") {
    val incOne = new IncrementCombinator(1, 'NonSense =>: 'Sense1)
    val incTwo = new IncrementCombinator(2, 'Sense1 =>: 'Sense2)
    val augmentedResult = result.addCombinator(incOne).addCombinator(incTwo)
    describe("when inhabiting Sense2") {
      val inhabitants = augmentedResult.inhabit[Int]('Sense2).interpretedTerms
      it("should find 44") {
        assert(!inhabitants.values.isEmpty)
        assert(inhabitants.index(0) == 44)
        assert(incOne(1) == 2)
      }
    }
  }

}
