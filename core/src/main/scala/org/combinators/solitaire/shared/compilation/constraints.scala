package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.Constraint
import domain.constraints.{AndConstraint, NotConstraint, OrConstraint}

trait ConstraintCodeGen {
  val defaultReturn: Expression => Statement = {
    (result: Expression) => Java(s"return $result").statement()
  }
  def toCode(parentGenerator: (Expression) => Statement = defaultReturn): Statement
}

class OrConstraintCodeGen(constraint: OrConstraint) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement =
    ConstraintCodeGen(constraint.getC1).toCode(leftChildResult => {
      ConstraintCodeGen(constraint.getC2).toCode(rightChildResult => {
        Java(
          s"""
             |if ($leftChildResult) {
             |  ${parentGenerator(Java("true").expression())}
             |} else if ($rightChildResult) {
             |  ${parentGenerator(Java("true").expression())}
             |} else {
             |  ${parentGenerator(Java("false").expression())}
             |}
             """.stripMargin).statement()
      })
    })
}

class AndConstraintCodeGen(constraint: AndConstraint) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement =
    ConstraintCodeGen(constraint.getC1).toCode(leftChildResult => {
      ConstraintCodeGen(constraint.getC2).toCode(rightChildResult => {
        Java(
          s"""
             |if ($leftChildResult && $rightChildResult) {
             |  ${parentGenerator(Java("true").expression())}
             |} else {
             |  ${parentGenerator(Java("false").expression())}
             |}
             """.stripMargin).statement()
      })
    })
}

class NotConstraintCodeGen(constraint: NotConstraint) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement =
    ConstraintCodeGen(constraint.getC1).toCode(childResult => {
      parentGenerator(Java(s"!($childResult)").expression())
    })
}




object ConstraintCodeGen {
  def apply(constraint: Constraint): ConstraintCodeGen =
    constraint match {
      case orConstraint: OrConstraint => new OrConstraintCodeGen(orConstraint)
      case andConstraint: AndConstraint => new AndConstraintCodeGen(andConstraint)
      case notConstraint: NotConstraint => new NotConstraintCodeGen(notConstraint)
    }
}