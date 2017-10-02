package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr.SimpleName
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.scalatest._

class SharedTests extends FunSpec {


  class Repo {
    // initially empty repository
  }

  val repository = new Repo with Controller {}
  val result = ReflectedRepository(repository)

  describe("Basic Tests for shared elements") {

    // access combinator directly via repository connection
    val classDef = new repository.ClassNameGenerator('SomeSymbol, "RealName")

    val augmentedResult = result.addCombinator(classDef)
    describe("when validating Move elements") {
      val inhabitants = augmentedResult.inhabit[SimpleName]('Move('SomeSymbol, 'ClassName)).interpretedTerms
      it("should find ClassName") {
        assert(!inhabitants.values.isEmpty)
        assert(inhabitants.index(0).getIdentifier == "RealName")
      }
    }
  }

}
