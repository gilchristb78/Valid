  package org.combinators.solitaire.shared

  import com.github.javaparser.ast.expr._
  import com.github.javaparser.ast.stmt._
  import de.tu_dortmund.cs.ls14.cls.types.Type
  import de.tu_dortmund.cs.ls14.cls.types.Constructor
  import de.tu_dortmund.cs.ls14.twirl.Java
  import domain.{Constraint, SolitaireContainerTypes}
  import domain.constraints._
  import domain.constraints.movetypes.{BottomCardOf, MoveComponents, TopCardOf}
  import org.combinators.solitaire.shared.constraintCodeGenerators.generators

  import scala.collection.JavaConverters._

  // Here is the default code registry for the existing Constraints

//  val codeGen = CodeGeneratorRegistry.merge[String](
//    CodeGeneratorRegistry[String, IfConstraint] {
//      case (registry: CodeGeneratorRegistry[String], ifc: IfConstraint) => "if used"
//    },
//
//    CodeGeneratorRegistry[String, Constraint] {
//      case (registry: CodeGeneratorRegistry[String], c: Constraint) => "other used"
//    }
//  )
  object constraintCodeGenerators {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, MoveComponents] {
        case (registry:CodeGeneratorRegistry[Expression], MoveComponents.Source) => {
          Java(s"""source""").expression()
        }
        case (registry:CodeGeneratorRegistry[Expression], MoveComponents.Destination) => {
          Java(s"""destination""").expression()
        }
        case (registry:CodeGeneratorRegistry[Expression], MoveComponents.MovingCard) => {
          Java(s"""movingCard""").expression()
        }
        case (registry:CodeGeneratorRegistry[Expression], MoveComponents.MovingColumn) => {
          Java(s"""movingColumn""").expression()
        }
      },

      CodeGeneratorRegistry[Expression, AndConstraint] {
        case (registry:CodeGeneratorRegistry[Expression], and: AndConstraint) => {
          if (and.constraints.isEmpty) {
            Java(s"""true""").expression()
          } else {
            and.constraints.tail.foldLeft(registry(and.constraints.head).get) {
              case (s, c) => {
                val inner = registry(c)
                if (inner.isEmpty) {
                  Java (s"""${c.toString}""").expression()
                } else {
                  Java(s"""($s) && (${registry(c).get})""").expression()
                }
              }
            }
          }
        }
      },

      CodeGeneratorRegistry[Expression, IfConstraint] {
        case (registry: CodeGeneratorRegistry[Expression], ifCons: IfConstraint) => {
          val inner = registry(ifCons.constraint)
          val trueb = registry(ifCons.trueBranch)
          val falseb = registry(ifCons.falseBranch)
          if (inner.isEmpty || trueb.isEmpty || falseb.isEmpty) {
            Java(s"""${ifCons.constraint.toString}""").expression()
          } else {
            Java(
              s"""|HelperConstraint.ifCompute (${registry(ifCons.constraint).get}
,
                |   ${registry(ifCons.trueBranch).get}
,
                |   ${registry(ifCons.falseBranch).get})""").expression()
        }
          }
      },

      CodeGeneratorRegistry[Expression, IsAce] {
        case (registry: CodeGeneratorRegistry[Expression], isAce: IsAce) => {
          Java(s"""${registry(isAce.element).get}.getRank() == Card.ACE""").expression()
        }
      },

      CodeGeneratorRegistry[Expression, SolitaireContainerTypes] {
        case (registry:CodeGeneratorRegistry[Expression], SolitaireContainerTypes.Tableau) => {
          Java(s"""tableauCards""").expression()
        }
        case (registry:CodeGeneratorRegistry[Expression], SolitaireContainerTypes.Foundation) => {
          Java(s"""foundationCards""").expression()
        }
      },

      CodeGeneratorRegistry[Expression, Descending] {
        case (registry: CodeGeneratorRegistry[Expression], descending: Descending) => {
          Java(s"""${registry(descending.base).get}.descending()""").expression()
        }
      },

      CodeGeneratorRegistry[Expression, TopCardOf] {
        case (registry: CodeGeneratorRegistry[Expression], top:TopCardOf) => {
          Java(s"""${registry(top.base).get}.peek()""").expression()
        }
      },


      CodeGeneratorRegistry[Expression, BottomCardOf] {
        case (registry: CodeGeneratorRegistry[Expression], bottom:BottomCardOf) => {
          Java(s"""${registry(bottom.base).get}.peek(0)""").expression()
        }
      },


      CodeGeneratorRegistry[Expression, AlternatingColors] {
      case (registry: CodeGeneratorRegistry[Expression], alternating: AlternatingColors) => {
        Java(s"""${registry(alternating.base).get}.alternatingColors()""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, OppositeColor] {
      case (registry: CodeGeneratorRegistry[Expression], opposite:OppositeColor) => {
        Java(s"""${registry(opposite.left).get}.oppositeColor(${registry(opposite.right).get})""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, IsEmpty] {
      case (registry: CodeGeneratorRegistry[Expression], isEmpty:IsEmpty) => {
        Java(s"""${registry(isEmpty.element).get}.empty()""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, NextRank] {
      case (registry: CodeGeneratorRegistry[Expression], nextRank:NextRank) => {
        Java(s"""${registry(nextRank.higher).get}.getRank() == ${registry(nextRank.lower).get}.getRank() + 1""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, HigherRank] {
      case (registry: CodeGeneratorRegistry[Expression], higherRank:HigherRank) => {
        Java(s"""${registry(higherRank.higher).get}.getRank() > ${registry(higherRank.lower).get}.getRank()""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, SameRank] {
      case (registry: CodeGeneratorRegistry[Expression], sameRank:SameRank) => {
        Java(s"""${registry(sameRank.left).get}.getRank() == ${registry(sameRank.right).get}.getRank()""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, SameSuit] {
      case (registry: CodeGeneratorRegistry[Expression], sameSuit:SameSuit) => {
        Java(s"""${registry(sameSuit.left).get}.getSuit() == ${registry(sameSuit.right).get}.getSuit()""").expression()
      }
    },


    CodeGeneratorRegistry[Expression, OrConstraint] {
      case (registry: CodeGeneratorRegistry[Expression], or: OrConstraint) =>
        if (or.constraints.isEmpty) {
          Java(s"""false""").expression()
        } else {
          or.constraints.tail.foldLeft(registry(or.constraints.head).get) {
            case (s, c) => {
              Java(s"""($s) || (${registry(c).get}) """).expression()
            }
          }
        }
    },

    CodeGeneratorRegistry[Expression, NotConstraint] {
      case (registry:CodeGeneratorRegistry[Expression],not:NotConstraint) =>
        Java(s"""!(${registry(not.constraint).get})""").expression();
    })
  }

  // codeGen.apply(ifc).get
  class StatementCombinator(c:Constraint, constraint_type:Constructor, inits:Seq[Statement] = Seq.empty) {  // Constructor
    def apply () : Seq[Statement] = {
      //val cc3: Option[Expression] = generators(c)
      val cc3:Option[Expression] = generators.apply(c);
      if (cc3.isEmpty) {
        println("Unable to locate:" + c);
        inits
      } else {
        inits ++ Java(s"""return ${cc3.get};""").statements()
      }
    }

    var semanticType : Type = constraint_type
  }


  //// now becomes a simple Expression since that is dominant usage
  //class SymbolCombinator(c:Constraint, constraint_type:Symbol) {  // Constructor
  //  def apply () : Expression = {
  //    ConstraintCodeGen(c).toCode()
  //  }
  //  var semanticType : Type = constraint_type
  //}
  //
  //class ConstructorCombinator(c:Constraint, constraint_type:Constructor) {  // Constructor
  //  def apply () : Expression = {
  //    ConstraintCodeGen(c).toCode()
  //  }
  //  var semanticType : Type = constraint_type
  //}






  //
  //
  ///** For Statements */
  //trait ConstraintCodeStmtGen {
  //  val defaultStatement: ConstraintStmt => Statement = {
  //    (result: ConstraintStmt) => Java(s"""System.out.println("default");""").statement()
  //  }
  //  def toCode(): Statement
  //}

  ///** Handles ReturnStmt. */
  //class ReturnStmtCodeGen(c:ReturnConstraint) extends ConstraintCodeStmtGen {
  //  override def toCode(): Statement = {
  //    val out = ConstraintCodeGen(c.getExpr).toCode()
  //
  //    val s:Statement = Java(s"""return ${out};""").statement()
  //    s
  //  }
  //}

  //
  ///** Handles IfStmt. */
  //class IfStmtCodeGen(c:IfConstraint) extends ConstraintCodeStmtGen {
  //  override def toCode(): Statement = {
  //
  //    // expression guards the true and false branches
  //    val expr = ConstraintCodeGen(c.constraint).toCode()
  //    val s_true  = ConstraintCodeStmtGen(c.trueBranch).toCode().toString()
  //    val s_false = ConstraintCodeStmtGen(c.falseBranch).toCode().toString()
  //
  //    Java(s"""
  //            |if ($expr) {
  //            |   $s_true
  //            |} else {
  //            |   $s_false
  //            |}
  //        """.stripMargin).statement()
  //  }
  //}
  //
  ///** Now all are expressions */
  //trait ConstraintCodeGen {
  //  val truth: Expression => Expression = {
  //    (result: Expression) => Java(s"true").expression()
  //  }
  //  def toCode(): Expression
  //}
  //
  //
  //
  //
  //
  //
  //object ConstraintCodeStmtGen {
  //
  //  def apply(stmt: ConstraintStmt): ConstraintCodeStmtGen =
  //    stmt match {
  //      case returnStmt: ReturnConstraint => new ReturnStmtCodeGen(returnStmt)
  //      case ifStmt: IfConstraint => new IfStmtCodeGen(ifStmt)
  //    }
  //}
