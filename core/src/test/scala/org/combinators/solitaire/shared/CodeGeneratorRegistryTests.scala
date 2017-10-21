package org.combinators.solitaire.shared

import domain.Constraint
import domain.constraints.{IfConstraint, OrConstraint}
import org.scalatest.FunSpec

class CodeGeneratorRegistryTests extends FunSpec {
  val codeGen = CodeGeneratorRegistry.merge[String](
    CodeGeneratorRegistry[String, IfConstraint] {
      case (registry: CodeGeneratorRegistry[String], ifc: IfConstraint) => "if used"
    },

    CodeGeneratorRegistry[String, Constraint] {
      case (registry: CodeGeneratorRegistry[String], c: Constraint) => "other used"
    }
  )

  describe("A constraint statement generator with a special case for IfConstraint") {
    val ifc: Constraint = new IfConstraint(null, null, null)
    val orc: Constraint = new OrConstraint()

    it("should use the special case for IfConstraint") {
      assert(codeGen.apply(ifc).get == "if used")
    }
    it("should use the generic case for OrConstraint") {
      assert(codeGen.apply(orc).get == "other used")
    }
    it("should find nothing for Object") {
      assert(codeGen.apply(new Object()).isEmpty)
    }
  }
}
