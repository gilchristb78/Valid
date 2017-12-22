package org.combinators.solitaire.shared.python

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import org.combinators.cls.types.Type
import domain.{Constraint, SolitaireContainerTypes}
import domain.constraints._
import domain.constraints.movetypes.{BottomCardOf, MoveComponents, TopCardOf}
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Python
import domain.deal.DealComponents
import domain.deal.map.{MapByRank, MapBySuit, MapCard}
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry

object constraintCodeGenerators  {

  // log everything as needed.
  val logger:LoggingAdapter = Logging.getLogger(ActorSystem("ConstraintCodeGenerators"), constraintCodeGenerators.getClass)
  logger.info("Constraint Generation logging active...")

  val generators:CodeGeneratorRegistry[Python] = CodeGeneratorRegistry.merge[Python](

    CodeGeneratorRegistry[Python, MoveComponents] {
      case (_:CodeGeneratorRegistry[Python], mc:MoveComponents) =>

        // following is based on this common API from PySolFC RowStack and FoundationStack
        // def acceptsCards(self, from_stack, cards):
        if (mc == MoveComponents.Source) {
          Python(s"""from_stack""")
        } else if (mc == MoveComponents.Destination) {
          // If you make it 'self' it could work with with Narcotic 'toLeftOf' by using .x attributes
          // and it would be consistent with 'from_stack' above. However, the IsEmpty(...) depends
          // on having self.cards, so we have to make this self.cards
          Python(s"""self.cards""")
        } else if (mc == MoveComponents.MovingCard) {
          Python(s"""cards[0]""")
        } else if (mc == MoveComponents.MovingColumn) {
          Python(s"""cards""")
        } else if (mc == MoveComponents.MovingRow) {
          Python(s"""cards""")
        } else {
          logger.warning(s"Unable to process MoveComponent (${mc.name}) in Python constraints.")
          Python(s"""None""") // not sure what else to do...
        }
    },

    CodeGeneratorRegistry[Python, DealComponents] {
      case (_:CodeGeneratorRegistry[Python], dc:DealComponents) =>
        Python(s"""${dc.name}""")
    },

    CodeGeneratorRegistry[Python, AndConstraint] {
      case (registry:CodeGeneratorRegistry[Python], and: AndConstraint) =>
        println ("And:" + and.toString)
        if (and.constraints.isEmpty) {
          Python(s"""True""")
        } else {
          and.constraints.tail.foldLeft(registry(and.constraints.head).get) {
            case (s, c) =>
              val inner = registry(c)
              if (inner.isEmpty) {
                Python (s"""${c.toString}""")
              } else {
                Python(s"""($s and ${inner.get})""")
              }
          }
        }
    },

    /** Takes advantage of () -> operator in Java 1.8. */
    CodeGeneratorRegistry[Python, IfConstraint] {
      case (registry: CodeGeneratorRegistry[Python], ifCons: IfConstraint) =>
        logger.debug(s"PythonConstraints:If: ${ifCons.toString}")
        val inner = registry(ifCons.constraint)
        val trueb = registry(ifCons.trueBranch)
        val falseb = registry(ifCons.falseBranch)

        // python evaluates left to right, and short-circuits branches. No need for ConstraintHelper class
        val str = Python(s"${trueb.get} if ${inner.get} else ${falseb.get}")

        logger.debug(s"PythonConstraints:If-inner: $str")
        Python(s"""$str""")
    },

    CodeGeneratorRegistry[Python, Truth] {
      case (_:CodeGeneratorRegistry[Python], _:Truth) =>
        Python(s"""True""")
    },

    CodeGeneratorRegistry[Python, Falsehood] {
      case (_:CodeGeneratorRegistry[Python], _:Falsehood) =>
        Python(s"""False""")
    },

    CodeGeneratorRegistry[Python, IsAce] {
      case (registry: CodeGeneratorRegistry[Python], isAce: IsAce) =>
        Python(s"""${registry(isAce.element).get}.rank == 0""")
    },

    // suits are 0..3
    CodeGeneratorRegistry[Python, IsSuit] {
      case (registry: CodeGeneratorRegistry[Python], isSuit: IsSuit) =>
        Python(s"""${registry(isSuit.element).get}.suit == (${isSuit.suit.value}-1)""")
    },

    /** PySolFC has Ace as 0, and King as 12. */
    CodeGeneratorRegistry[Python, IsRank] {
      case (registry: CodeGeneratorRegistry[Python], isRank: IsRank) =>
        Python(s"""${registry(isRank.element).get}.rank == (${isRank.rank.value}-1)""")
    },

    CodeGeneratorRegistry[Python, IsKing] {
      case (registry: CodeGeneratorRegistry[Python], isKing: IsKing) =>
        Python(s"""${registry(isKing.element).get}.rank == 12""")
    },


    CodeGeneratorRegistry[Python, IsFaceUp] {
      case (registry: CodeGeneratorRegistry[Python], c: IsFaceUp) =>
        Python(s"""${registry(c.element).get}.face_up""")
    },

    /** Access via helper method of same name. */
    CodeGeneratorRegistry[Python, SolitaireContainerTypes] {
      case (_:CodeGeneratorRegistry[Python], st:SolitaireContainerTypes) =>
        Python(s"""${st.name}()""")
    },

    CodeGeneratorRegistry[Python, Descending] {
      case (registry: CodeGeneratorRegistry[Python], descending: Descending) =>
        Python(s"""isRankSequence(${registry(descending.base).get})""")
    },

    CodeGeneratorRegistry[Python, TopCardOf] {
      case (registry: CodeGeneratorRegistry[Python], top:TopCardOf) =>
        Python(s"""${registry(top.base).get}[-1]""")
    },

    CodeGeneratorRegistry[Python, BottomCardOf] {
      case (registry: CodeGeneratorRegistry[Python], bottom:BottomCardOf) =>
        Python(s"""${registry(bottom.base).get}[0]""")
    },

    CodeGeneratorRegistry[Python, AlternatingColors] {
      case (registry: CodeGeneratorRegistry[Python], alternating: AlternatingColors) =>
        Python(s"""isAlternateColorSequence(${registry(alternating.base).get})""")
    },

    CodeGeneratorRegistry[Python, OppositeColor] {
      case (registry: CodeGeneratorRegistry[Python], opposite:OppositeColor) =>
        Python(s"""${registry(opposite.left).get}.color != ${registry(opposite.right).get}.color""")
    },

    CodeGeneratorRegistry[Python, IsEmpty] {
      case (registry: CodeGeneratorRegistry[Python], isEmpty:IsEmpty) =>
        Python(s"""len(${registry(isEmpty.element).get}) == 0""")
    },

    CodeGeneratorRegistry[Python, IsSingle] {
      case (registry: CodeGeneratorRegistry[Python], isSingle:IsSingle) =>
        Python(s"""len(${registry(isSingle.element).get}) == 1""")
    },

    CodeGeneratorRegistry[Python, NextRank] {
      case (registry: CodeGeneratorRegistry[Python], nextRank:NextRank) =>
        Python(s"""${registry(nextRank.higher).get}.rank == ${registry(nextRank.lower).get}.rank + 1""")
    },

    CodeGeneratorRegistry[Python, HigherRank] {
      case (registry: CodeGeneratorRegistry[Python], higherRank:HigherRank) =>
        Python(s"""${registry(higherRank.higher).get}.rank > ${registry(higherRank.lower).get}.rank""")
    },

    CodeGeneratorRegistry[Python, SameRank] {
      case (registry: CodeGeneratorRegistry[Python], sameRank:SameRank) =>
        Python(s"""${registry(sameRank.left).get}.rank == ${registry(sameRank.right).get}.rank""")
    },

    CodeGeneratorRegistry[Python, SameSuit] {
      case (registry: CodeGeneratorRegistry[Python], sameSuit:SameSuit) =>
        Python(s"""${registry(sameSuit.left).get}.suit == ${registry(sameSuit.right).get}.suit""")
    },

    CodeGeneratorRegistry[Python, OrConstraint] {
      case (registry: CodeGeneratorRegistry[Python], or: OrConstraint) =>
        logger.debug(s"PythonConstraints:Or: ${or.toString}")

        if (or.constraints.isEmpty) {
          Python(s"""False""")
        } else {
          or.constraints.tail.foldLeft(registry(or.constraints.head).get) {
            case (s, c) =>
              Python(s"""($s or ${registry(c).get})""")
          }
        }
    },

    CodeGeneratorRegistry[Python, NotConstraint] {
      case (registry:CodeGeneratorRegistry[Python],not:NotConstraint) =>
        Python(s"""not (${registry(not.constraint).get})""")
    })


  // used for map expressions
  val mapGenerators:CodeGeneratorRegistry[Python] = CodeGeneratorRegistry.merge[Python](
    CodeGeneratorRegistry[Python, MapByRank] {
      case (_:CodeGeneratorRegistry[Python], _:MapByRank) =>
        Python(s"""card.rank""")
    },

    CodeGeneratorRegistry[Python, MapBySuit] {
      case (_:CodeGeneratorRegistry[Python], _:MapBySuit) =>
        Python(s"""card.suit""")
    },

  )

}

// Before this was for a move() Java SemanticType. For now, placeholder with tpe
class ConstraintExpander(c:Constraint, tpe:Type) extends PythonSemanticTypes {
  def apply(generators: CodeGeneratorRegistry[Python]): Python = {
    val cc3: Option[Python] = generators(c)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.warning(s"ConstraintExpander: Unable to locate: ${c.toString}")
      Python("None") // not sure what to do
    } else {
      Python(s"""${cc3.get}""")
    }
  }

  var semanticType: Type = constraints(constraints.generator) =>: tpe
}



/** When used, it isn't important what semantic Type is, which is why we omit it. */
class MapExpressionCombinator(m:MapCard) extends PythonSemanticTypes {

  def apply(generators: CodeGeneratorRegistry[Python]): Python = {
    val cc3: Option[Python] = generators(m)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("MapExpressionCombinator: Unable to locate:" + m.toString)
      Python("false")
    } else {
      cc3.get
    }
  }
}
