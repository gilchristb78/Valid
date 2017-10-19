package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import domain.Solitaire
import org.combinators.solitaire.klondike.{KlondikeDomain, controllers}
import org.scalatest._

class Helper extends FunSpec {

  /**
    * Given compilation unit, deternmine if class is present with given name.
    * @param unit
    * @param name
    */
  def containsClass(unit:CompilationUnit, name:String): Unit = {
    unit.getClassByName(name).isPresent
  }

  /**
    * Check for existence of class during inhabitation
    *
    * @param Gamma           pre-built repository
    * @param domainModel     Solitaire Domain Model instance
    * @param target          Target symbol sought for
    * @param name            Desired name of class to find.
    * @return
    */
  def singleInstance[R](Gamma:ReflectedRepository[_], domainModel:Solitaire, target:Type):R = {
    describe ("Looking for instance") {
      lazy val job = Gamma.InhabitationBatchJob[R](target)
      lazy val results = job.run()


      it ("should not be infinite") {
        assert(!results.isInfinite)
      }
      lazy val interpretedResults = results.interpretedTerms.values.flatMap(_._2)
      it ("should include exactly one result") {
        assert(interpretedResults.size == 1)
      }
//      it ("should include a class named " + name) {
//        assert(interpretedResults.head.getClassByName(name).isPresent)
//      }
      interpretedResults.head
    }

    // don't really need a function with a return value, but...
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
