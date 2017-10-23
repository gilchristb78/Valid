package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import domain.Solitaire
import org.scalatest._

/**
  * Defines helpers
  * @param types
  */
class Helper (types:SemanticTypes) extends FunSpec {
  import types._

  /**
    * Given compilation unit, deternmine if class is present with given name.
    * @param unit
    * @param name
    */
  def containsClass(unit:CompilationUnit, name:String): Unit = {
     assert(unit.getClassByName(name).isPresent)
  }


  def single[G](Gamma:ReflectedRepository[_], target:Type):G = {
    val inhab: Iterator[G] = Gamma.inhabit[G](target).interpretedTerms.values.flatMap(_._2).iterator

    it(target.toString + " should be non-empty") {
      assert(inhab.nonEmpty)
    }

    it(target.toString + " should be exactly 1") {
      assert(inhab.nonEmpty)
    }

    inhab.next()
  }


  /**
    * Check for existence of class during inhabitation
    *
    * @param Gamma           pre-built repository
    * @param target          Target symbol sought for
    * @return
    */  //               val inhabitants = augmentedResult.inhabit[Int]('Sense2).interpretedTerms


  def singleInstance[R](Gamma:ReflectedRepository[_], target:Type):R = {
    val results = Gamma.inhabit[R](target).interpretedTerms.values.flatMap(_._2)
    println (results)
    describe ("Looking for instance") {

      it ("should not be empty and should be 1") {
        assert(results.nonEmpty)
        assert(results.size == 1)
      }
    }

    results.head
  }

  /**
    * Check for existence of class during inhabitation for given target and type
    *
    * @param Gamma           pre-built repository
    * @param domainModel     Solitaire Domain Model instance
    * @param target          Target symbol sought for
    * @return
    */
  def checkExistenceTarget(Gamma:ReflectedRepository[_], domainModel:Solitaire, target:Type):Boolean = {
    describe ("Looking for target " + target) {
      lazy val job = Gamma.InhabitationBatchJob[Seq[Statement]](target)
      lazy val results = job.run()
      it ("should not be infinite") {
        assert(!results.isInfinite)
      }
      lazy val interpretedResults = results.interpretedTerms.values.flatMap(_._2)
      it ("should include exactly one result") {
        assert(interpretedResults.size == 1)
      }

    }

    // don't really need a function with a return value, but...
    true
  }


}
