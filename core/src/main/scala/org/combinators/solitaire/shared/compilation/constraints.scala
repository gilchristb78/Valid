package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Constructor

import de.tu_dortmund.cs.ls14.twirl.Java
import domain.Constraint
import domain.{Card, Pile, Column}
import domain.constraints._

// need Seq[Statement] since that is dominant usage
class SymbolCombinator(c:Constraint, constraint_type:Symbol) {  // Constructor
  def apply () : Seq[Statement] = {
    val cc = ConstraintCodeGen(c)
    Seq(cc.toCode(cc.defaultReturn))
  }
  var semanticType : Type = constraint_type
}

// need Seq[Statement] ditto
class ConstructorCombinator(c:Constraint, constraint_type:Constructor, inits:Seq[Statement] = Seq.empty) {  // Constructor
  def apply () : Seq[Statement] = {
    val cc = ConstraintCodeGen(c)
    inits ++ Seq(cc.toCode(cc.defaultReturn))
  }
  var semanticType : Type = constraint_type
}

/** Default case for termination. */
trait ConstraintCodeGen {
  val defaultReturn: Expression => Statement = {
    (result: Expression) => Java(s"return $result;").statement()
  }
  def toCode(parentGenerator: (Expression) => Statement = defaultReturn): Statement
}

/** Handles the IsEmpty Constraint -- must define EmptyConstraint in the context of a move. */
class ElementEmptyCodeGen(c:ElementEmpty) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
    val exp:Expression = Java(s"""${c.getElement()}.empty()""").expression()
    parentGenerator(exp)
  }
}

class DescendingCodeGen(c:Descending) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
    val exp:Expression = Java(s"""${c.getElement()}.descending()""").expression()
    parentGenerator(exp)
  }
}

class AlternatingColorsCodeGen(c:AlternatingColors) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
    val exp:Expression = Java(s"""${c.getElement()}.alternating()""").expression()
    parentGenerator(exp)
  }
}

class OppositeColorCodeGen(c:OppositeColor) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
    val exp:Expression = Java(s"""${c.getElement1()}.oppositeColor(${c.getElement2()})""").expression()

    parentGenerator(exp)
  }
}



/** Handles the IsAce Constraint. */
class IsAceCodeGen(c:IsAce) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
    c.getType match {
      case col: Column => parentGenerator(Java(s"""${c.getElement()}.rank() == Card.ACE""").expression())
      case card: Card => parentGenerator(Java(s"""${c.getElement()}.getRank() == Card.ACE""").expression()) 
      case _ => parentGenerator(Java(s""" ${c.getType()}""").expression())
    }
  }
}

/** Handles the SameSuit Constraint. */
class SameSuitCodeGen(c:SameSuit) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
     val exp:Expression = Java(s"""${c.getElement1()}.getSuit() == ${c.getElement2()}.getSuit()""").expression()
     parentGenerator(exp)    
  }
}

/** Handles the NextRank Constraint. */
class NextRankCodeGen(c:NextRank) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
     val exp:Expression = Java(s"""${c.getElement1()}.getRank() == ${c.getElement2()}.getRank() + 1""").expression()
     parentGenerator(exp)
  }
}


class ExpressionCodeGen(c:ExpressionConstraint) extends ConstraintCodeGen {
  override def toCode(parentGenerator: (Expression) => Statement): Statement = {
     val exp:Expression = Java(s"""${c.getLHS()} ${c.getOp()} ${c.getRHS}""").expression()
     parentGenerator(exp)
  }
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
        // Organize Right first for short circuit...
        Java(
          s"""
             |if ($rightChildResult && $leftChildResult) {
             |  ${parentGenerator(Java("true").expression())}
             |} else {
             |  ${parentGenerator(Java("false").expression())}
             |}
             """.stripMargin).statement()
      })
    })
}

// eventually remove
//class NotConstraintCodeGen(constraint: NotConstraint) extends ConstraintCodeGen {
//  override def toCode(parentGenerator: (Expression) => Statement): Statement =
//    ConstraintCodeGen(constraint.getC1).toCode(childResult => {
//      parentGenerator(Java(s"!($childResult)").expression())
//    })
//}


object ConstraintCodeGen {
  def apply(constraint: Constraint): ConstraintCodeGen =
    constraint match {
      case emptyConstraint: ElementEmpty => new ElementEmptyCodeGen(emptyConstraint)
      case aceConstraint : IsAce => new IsAceCodeGen(aceConstraint)
      case nextRankConstraint : NextRank => new NextRankCodeGen(nextRankConstraint)
      case descendingConstraint : Descending => new DescendingCodeGen(descendingConstraint)
      case oppositeConstraint : OppositeColor => new OppositeColorCodeGen(oppositeConstraint)
      case alternatingConstraint : AlternatingColors => new AlternatingColorsCodeGen(alternatingConstraint)
      case sameSuitConstraint : SameSuit => new SameSuitCodeGen(sameSuitConstraint)
      case exprConstraint: ExpressionConstraint => new ExpressionCodeGen(exprConstraint)
      case orConstraint: OrConstraint => new OrConstraintCodeGen(orConstraint)
      case andConstraint: AndConstraint => new AndConstraintCodeGen(andConstraint)
 //     case notConstraint: NotConstraint => new NotConstraintCodeGen(notConstraint)
    }
}
