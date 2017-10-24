package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.body._

import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.{Constraint, SolitaireContainerTypes}
import domain.constraints._
import domain.constraints.movetypes.{BottomCardOf, MoveComponents, TopCardOf}

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
      case (registry:CodeGeneratorRegistry[Expression], mc:MoveComponents) => {
        Java(s"""${mc.name}""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, AndConstraint] {
      case (registry:CodeGeneratorRegistry[Expression], and: AndConstraint) => {
        println ("And:" + and.toString)
        if (and.constraints.isEmpty) {
          Java(s"""true""").expression()
        } else {
          and.constraints.tail.foldLeft(registry(and.constraints.head).get) {
            case (s, c) => {
              val inner = registry(c)
              if (inner.isEmpty) {
                Java (s"""${c.toString}""").expression()
              } else {
                Java(s"""($s && ${registry(c).get})""").expression()
              }
            }
          }
        }
      }
    },

    /** Takes advantage of () -> operator in Java 1.8. */
    CodeGeneratorRegistry[Expression, IfConstraint] {
      case (registry: CodeGeneratorRegistry[Expression], ifCons: IfConstraint) => {
        println ("If:" + ifCons.toString)

        val inner = registry(ifCons.constraint)
        val trueb = registry(ifCons.trueBranch)
        val falseb = registry(ifCons.falseBranch)

        val str =
            s"""ConstraintHelper.ifCompute (
               |${inner.get},
               |() -> ${trueb.get},
               |() -> ${falseb.get})""".stripMargin
          println ("IF-inner:" + str)
          Java(s"""$str""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, Truth] {
      case (registry:CodeGeneratorRegistry[Expression], t:Truth) => {
        Java(s"""true""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, Falsehood] {
      case (registry:CodeGeneratorRegistry[Expression], f:Falsehood) => {
        Java(s"""false""").expression()
      }
    },


    CodeGeneratorRegistry[Expression, IsAce] {
      case (registry: CodeGeneratorRegistry[Expression], isAce: IsAce) => {
        Java(s"""${registry(isAce.element).get}.getRank() == Card.ACE""").expression()
      }
    },

    CodeGeneratorRegistry[Expression, IsKing] {
      case (registry: CodeGeneratorRegistry[Expression], isKing: IsKing) => {
        Java(s"""${registry(isKing.element).get}.getRank() == Card.KING""").expression()
      }
    },


    CodeGeneratorRegistry[Expression, IsFaceUp] {
      case (registry: CodeGeneratorRegistry[Expression], c: IsFaceUp) => {
        Java(s"""${registry(c.element).get}.isFaceUp()""").expression()
      }
    },

    /** Note: This code will exist in a method that has game as an argument. */
    CodeGeneratorRegistry[Expression, SolitaireContainerTypes] {
      case (registry:CodeGeneratorRegistry[Expression], st:SolitaireContainerTypes) => {
        Java(s"""ConstraintHelper.${st.name}(game)""").expression()
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

    CodeGeneratorRegistry[Expression, IsSingle] {
      case (registry: CodeGeneratorRegistry[Expression], isSingle:IsSingle) => {
        Java(s"""${registry(isSingle.element).get}.count() == 1""").expression()
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
        println ("Or:" + or.toString)
        if (or.constraints.isEmpty) {
          Java(s"""false""").expression()
        } else {
          or.constraints.tail.foldLeft(registry(or.constraints.head).get) {
            case (s, c) => {
              Java(s"""($s || ${registry(c).get})""").expression()
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
class StatementCombinator(c:Constraint, moveSymbol:Type, inits:Seq[Statement] = Seq.empty) extends SemanticTypes { // Constructor
  def apply(generators: CodeGeneratorRegistry[Expression]): Seq[Statement] = {
    val cc3: Option[Expression] = generators(c)
    if (cc3.isEmpty) {
      println("Unable to locate:" + c)
      inits
    } else {
      inits ++ Java(s"""return ${cc3.get};""").statements()
    }
  }

  var semanticType: Type = constraints(constraints.generator) =>: move(moveSymbol, move.validStatements)
}

object generateHelper {
  /**
    * Helper method for the ConstraintHelper class
    */
  def fieldAccessHelper(name:String, fieldName:String) : MethodDeclaration = {
    Java(
      s"""|public static Stack[] $name(Solitaire game) {
          |  return getVariation(game).$fieldName;
          |}
       """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head
  }
}
