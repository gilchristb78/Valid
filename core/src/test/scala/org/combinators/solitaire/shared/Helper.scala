package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import domain.Solitaire
import org.combinators.solitaire.klondike.{KlondikeDomain, controllers}
import org.scalatest._

class Helper extends FunSpec {

  /**
    * Check for existence of class during inhabitation
    *
    * @param Gamma           pre-built repository
    * @param domainModel     Solitaire Domain Model instance
    * @param target          Target symbol sought for
    * @param name            Desired name of class to find.
    * @return
    */
  def checkExistence(Gamma:ReflectedRepository[_], domainModel:Solitaire, target:Type, name:String):Boolean = {
    describe ("Looking for class " + name) {
      lazy val job = Gamma.InhabitationBatchJob[CompilationUnit](target)
      lazy val results = job.run()
      it ("should not be infinite") {
        assert(!results.isInfinite)
      }
      lazy val interpretedResults = results.interpretedTerms.values.flatMap(_._2)
      it ("should include exactly one result") {
        assert(interpretedResults.size == 1)
      }
      it ("should include a class named " + name) {
        assert(interpretedResults.head.getClassByName(name).isPresent)
      }
    }

    // don't really need a function with a return value, but...
    true
  }
}
