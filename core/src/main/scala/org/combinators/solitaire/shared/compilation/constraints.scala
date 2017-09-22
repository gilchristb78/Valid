package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Constructor

import de.tu_dortmund.cs.ls14.twirl.Java
import domain.{ConstraintExpr,ConstraintStmt}
import domain.{Card, Pile, Column}
import domain.Output
import domain.constraints._

// now becomes a simple Expression since that is dominant usage
class SymbolCombinator(c:ConstraintExpr, constraint_type:Symbol) {  // Constructor
  def apply () : Expression = {
    ConstraintCodeGen(c).toCode
  }
  var semanticType : Type = constraint_type
}

class ConstructorCombinator(c:ConstraintExpr, constraint_type:Constructor) {  // Constructor
  def apply () : Expression = {
    ConstraintCodeGen(c).toCode
  }
  var semanticType : Type = constraint_type
}

class StatementCombinator(c:ConstraintStmt, constraint_type:Constructor, inits:Seq[Statement] = Seq.empty) {  // Constructor
  def apply () : Seq[Statement] = {
    val cc3 = ConstraintCodeStmtGen(c) 
    inits ++ Seq(cc3.toCode())
  }
  var semanticType : Type = constraint_type
}

/** For Statements */
trait ConstraintCodeStmtGen {
  val defaultStatement: ConstraintStmt => Statement = {
    (result: ConstraintStmt) => Java(s"""System.out.println("default");""").statement()
  }
  def toCode(): Statement
}

/** Handles ReturnStmt. */
class ReturnStmtCodeGen(c:ReturnConstraint) extends ConstraintCodeStmtGen {
  override def toCode(): Statement = {
    val out = ConstraintCodeGen(c.getExpr()).toCode()

    val s:Statement = Java(s"""return ${out};""").statement()
    s
  }
}


/** Handles IfStmt. */
class IfStmtCodeGen(c:IfConstraint) extends ConstraintCodeStmtGen {
  override def toCode(): Statement = {
    def removeLF(s: String) = s.map(c => if(c == '\n') ' ' else c)

    // expression guards the true and false branches
    val expr = ConstraintCodeGen(c.getExpr()).toCode()
    val s_true  = ConstraintCodeStmtGen(c.getTrueBranch()).toCode().toString()
    val s_false = ConstraintCodeStmtGen(c.getFalseBranch()).toCode().toString()

    // PrintWriter
//    val pw = domain.Output.create("outputFile.txt")
//    pw.write("expr:" + expr + "\n")
//    pw.write("true:" + s_true + "\n")
//    pw.write("false:" + s_false + "\n")
//    pw.close

    Java(s"""
	|if ($expr) {
	|   $s_true
	|} else {
	|   $s_false
	|}
        """.stripMargin).statement()
  }
}

/** Now all are expressions */
trait ConstraintCodeGen {
  val truth: Expression => Expression = {
    (result: Expression) => Java(s"true").expression()
  }
  def toCode(): Expression
}

class ReturnTrueCodeGen (r:ReturnTrueExpression) extends ConstraintCodeGen {
  override def toCode() : Expression = {
    Java(s"""true""").expression()
  }
}

class ReturnFalseCodeGen (r:ReturnFalseExpression) extends ConstraintCodeGen {
  override def toCode() : Expression = {
    Java(s"""false""").expression()
  }
}

/** Handles the IsEmpty Constraint -- must define EmptyConstraint in the context of a move. */
class ElementEmptyCodeGen(c:ElementEmpty) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement()}.empty()""").expression()
  }
}

class DescendingCodeGen(c:Descending) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement()}.descending()""").expression()
  }
}

class AlternatingColorsCodeGen(c:AlternatingColors) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement()}.alternatingColors()""").expression()
  }
}

class OppositeColorCodeGen(c:OppositeColor) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement1()}.oppositeColor(${c.getElement2()})""").expression()

  }
}

/** Handles the IsAce Constraint which can be applied to single card or a column. */
class IsAceCodeGen(c:IsAce) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    c.getType match {
      case col: Column => Java(s"""${c.getElement()}.rank() == Card.ACE""").expression()
      case card: Card => Java(s"""${c.getElement()}.getRank() == Card.ACE""").expression() 
      case _ => Java(s""" ${c.getType()}""").expression()
    }
  }
}

/** Handles the SameSuit Constraint. */
class SameSuitCodeGen(c:SameSuit) extends ConstraintCodeGen {
  override def toCode(): Expression = {
     Java(s"""${c.getElement1()}.getSuit() == ${c.getElement2()}.getSuit()""").expression()
  }
}

/** Handles the NextRank Constraint. */
class NextRankCodeGen(c:NextRank) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement1()}.getRank() == ${c.getElement2()}.getRank() + 1""").expression()
  }
}

/** Handles the SameRank Constraint. */
class SameRankCodeGen(c:SameRank) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement1()}.getRank() == ${c.getElement2()}.getRank()""").expression()
  }
}


/** Handles the HigherRank Constraint. */
class HigherRankCodeGen(c:HigherRank) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    Java(s"""${c.getElement1()}.getRank() > ${c.getElement2()}.getRank()""").expression()
  }
}


class ExpressionCodeGen(c:ExpressionConstraint) extends ConstraintCodeGen {
  override def toCode(): Expression = {
     Java(s"""${c.getLHS()} ${c.getOp()} ${c.getRHS}""").expression()
  }
}

class BooleanExpressionCodeGen(b:BooleanExpression) extends ConstraintCodeGen {
  override def toCode(): Expression = {
     Java(s"""${b.getExpression()}""").expression()
  }

}

/**
 * These aren't doing what I want to do. Perhaps it would be simpler 
 * to make the following assumptions:
 *
 *  1. An OR can only have atomic children or AND children.
 *  2. An AND can only have atomic children or AND -- not OR 
 *
 *  3. Mutually exclusive: typically binary
 *  4. AND nodes are binary, but they are chained together
 * 
 *     AND --- C1  
            |
            -- AND --- C2
                    |
                    -- C3
 */
class OrConstraintCodeGen(constraint: OrConstraint) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    val left = ConstraintCodeGen(constraint.getC1).toCode
    val right = ConstraintCodeGen(constraint.getC2).toCode
    val exp:Expression = Java(s"""($left) || ($right)""").expression()
    exp
  }
}


// Altered based on n-ary logic?
class AndConstraintCodeGen(constraint: AndConstraint) extends ConstraintCodeGen {
  override def toCode(): Expression = {
    val left = ConstraintCodeGen(constraint.getC1).toCode
    val right = ConstraintCodeGen(constraint.getC2).toCode
    val exp:Expression = Java(s"""($left) && ($right)""").expression()
    exp
  }
}


// eventually remove
//class NotConstraintCodeGen(constraint: NotConstraint) extends ConstraintCodeGen {
//  override def toCode(parentGenerator: (Expression) => Statement): Statement =
//    ConstraintCodeGen(constraint.getC1).toCode(childResult => {
//      parentGenerator(Java(s"!($childResult)").expression())
//    })
//}
object ConstraintCodeStmtGen {

 def apply(stmt: ConstraintStmt): ConstraintCodeStmtGen =
    stmt match {
      case returnStmt: ReturnConstraint => new ReturnStmtCodeGen(returnStmt)
      case ifStmt: IfConstraint => new IfStmtCodeGen(ifStmt)
    }
}

object ConstraintCodeGen {
  def apply(constraint: ConstraintExpr): ConstraintCodeGen =
    constraint match {
      case alternatingConstraint : AlternatingColors => new AlternatingColorsCodeGen(alternatingConstraint)
      case andConstraint: AndConstraint => new AndConstraintCodeGen(andConstraint)
      case boolConstraint : BooleanExpression => new BooleanExpressionCodeGen(boolConstraint)
      case descendingConstraint : Descending => new DescendingCodeGen(descendingConstraint)
      case emptyConstraint: ElementEmpty => new ElementEmptyCodeGen(emptyConstraint)
      case exprConstraint: ExpressionConstraint => new ExpressionCodeGen(exprConstraint)
      case higherRankConstraint : HigherRank => new HigherRankCodeGen(higherRankConstraint)
      case isAceConstraint : IsAce => new IsAceCodeGen(isAceConstraint)
      case nextRankConstraint : NextRank => new NextRankCodeGen(nextRankConstraint)
      case oppositeConstraint : OppositeColor => new OppositeColorCodeGen(oppositeConstraint)
      case orConstraint: OrConstraint => new OrConstraintCodeGen(orConstraint)
      case retFalse : ReturnFalseExpression => new ReturnFalseCodeGen(retFalse)
      case retTrue : ReturnTrueExpression => new ReturnTrueCodeGen(retTrue)
      case sameRankConstraint : SameRank => new SameRankCodeGen(sameRankConstraint)
      case sameSuitConstraint : SameSuit => new SameSuitCodeGen(sameSuitConstraint)
 //     case notConstraint: NotConstraint => new NotConstraintCodeGen(notConstraint)
    }
}
