package org.combinators.solitaire

import org.scalatest._
import org.combinators.cls.types._
import org.combinators.cls.interpreter._
import syntax._

class DynamicCombinatorTest extends FunSpec {
  class Repo {
    @combinator object NonSense {
      def apply: Int = 41
      val semanticType: Type = 'NonSense
    }
    @combinator object Show {
      def apply(x: Int): String = x.toString
      val semanticType:Type = ('Sense =>: 'Sense) :&: ('NonSense =>: 'NonSense)
    }
  }

  object MakeSense {
    def apply(x: Int): Int = x + 1
    val semanticType: Type = 'NonSense =>: 'Sense
  }

  val repository = new Repo
  val result = ReflectedRepository(repository)


  describe("The augmented repository") {
    val augmentedResult = result.addCombinator(MakeSense)
    describe("when inhabiting NonSense") {
      val inhabitants = result.inhabit[String]('NonSense).interpretedTerms
      it("should find NonSense") {
        assert(inhabitants.values.nonEmpty)
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
          assert(inhabitants.values.nonEmpty)
          assert(inhabitants.index(0) == "41")
        }
      }
      describe("when inhabiting Sense") {
        val inhabitants = augmentedResult.inhabit[String]('Sense).interpretedTerms
        it("should find 42") {
          assert(inhabitants.values.nonEmpty)
          assert(inhabitants.index(0) == "42")
        }
      }
    }
  }

  class IncrementCombinator(delta: Int, semantics: Type) {
    def apply(x: Int): Int = x + delta
    val semanticType:Type = semantics
  }

  describe("The reflected repository with two IncrementCombinator instances") {
    val incOne = new IncrementCombinator(1, 'NonSense =>: 'Sense1)
    val incTwo = new IncrementCombinator(2, 'Sense1 =>: 'Sense2)
    val augmentedResult = result.addCombinator(incOne).addCombinator(incTwo)
    describe("when inhabiting Sense2") {
      val inhabitants = augmentedResult.inhabit[Int]('Sense2).interpretedTerms
      it("should find 44") {
        assert(inhabitants.values.nonEmpty)
        assert(inhabitants.index(0) == 44)
        assert(incOne(1) == 2)
      }
    }
  }
}
