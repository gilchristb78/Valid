package org.combinators.solitaire.shared.python

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import domain.deal.map.MapCard
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.{Java, Python}
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
        if (mc == Source) {
          Python(s"""from_stack""")
        } else if (mc == Destination) {
          // If you make it 'self' it could work with with Narcotic 'toLeftOf' by using .x attributes
          // and it would be consistent with 'from_stack' above. However, the IsEmpty(...) depends
          // on having self.cards, so we have to make this self.cards
          Python(s"""self.cards""")
        } else if (mc == MovingCard) {
          Python(s"""cards[0]""")
        } else if (mc == MovingCards) {
          Python(s"""cards""")
        } else {
          logger.warning(s"Unable to process MoveComponent (${mc.toString}) in Python constraints.")
          Python(s"""None""") // not sure what else to do...
        }
    },

    CodeGeneratorRegistry[Python, DealComponents.type] {
      case (_:CodeGeneratorRegistry[Python], DealComponents) =>
        Python(s"""card""") // TODO: ${dc.name}
    },

    CodeGeneratorRegistry[Python, AndConstraint] {
      case (registry:CodeGeneratorRegistry[Python], and: AndConstraint) =>
        println ("And:" + and.toString)
        if (and.args.isEmpty) {
          Python(s"""True""")
        } else {
          and.args.tail.foldLeft(registry(and.args.head).get) {
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
        val inner = registry(ifCons.guard)
        val trueb = registry(ifCons.trueBranch)
        val falseb = registry(ifCons.falseBranch)

        // python evaluates left to right, and short-circuits branches. No need for ConstraintHelper class
        val str = Python(s"${trueb.get} if ${inner.get} else ${falseb.get}")

        logger.debug(s"PythonConstraints:If-inner: $str")
        Python(s"""$str""")
    },

    CodeGeneratorRegistry[Python, Truth.type] {
      case (_:CodeGeneratorRegistry[Python], Truth) =>
        Python(s"""True""")
    },

    CodeGeneratorRegistry[Python, Falsehood.type] {
      case (_:CodeGeneratorRegistry[Python], Falsehood) =>
        Python(s"""False""")
    },

    CodeGeneratorRegistry[Python, IsAce] {
      case (registry: CodeGeneratorRegistry[Python], isAce: IsAce) =>
        Python(s"""${registry(isAce.on).get}.rank == 0""")
    },

    // suits are 0..3
    CodeGeneratorRegistry[Python, IsSuit] {
      case (registry: CodeGeneratorRegistry[Python], isSuit: IsSuit) =>
        def toIndex(s:Suit):Int = {
          s match {
            case Clubs => 0
            case Diamonds => 1
            case Hearts => 2
            case Spades => 3
          }
        }
        Python(s"""${registry(isSuit.on).get}.suit == (${toIndex(isSuit.suit)})""")
    },

    /** PySolFC has Ace as 0, and King as 12. */
    CodeGeneratorRegistry[Python, IsRank] {
      case (registry: CodeGeneratorRegistry[Python], isRank: IsRank) =>
        Python(s"""${registry(isRank.on).get}.rank == (${isRank.rank.num}-1)""")
    },

    CodeGeneratorRegistry[Python, IsKing] {
      case (registry: CodeGeneratorRegistry[Python], isKing: IsKing) =>
        Python(s"""${registry(isKing.on).get}.rank == 12""")
    },


    CodeGeneratorRegistry[Python, IsFaceUp] {
      case (registry: CodeGeneratorRegistry[Python], c: IsFaceUp) =>
        Python(s"""${registry(c.on).get}.face_up""")
    },

    // TODO: HACK ADD ALL OTHER SolitaireContainerTypes....
    /** Note: This code will be invoked in a method that has game as an argument. */
    CodeGeneratorRegistry[Python, Tableau.type] {
      case (_:CodeGeneratorRegistry[Python], Tableau) =>
        Java(s"""${Tableau.name}()""").expression()
    },

    CodeGeneratorRegistry[Python, Descending] {
      case (registry: CodeGeneratorRegistry[Python], descending: Descending) =>
        Python(s"""isRankSequence(${registry(descending.on).get})""")
    },

    CodeGeneratorRegistry[Python, TopCardOf] {
      case (registry: CodeGeneratorRegistry[Python], top:TopCardOf) =>
        Python(s"""${registry(top.moveInfo).get}[-1]""")
    },

//    CodeGeneratorRegistry[Python, BottomCardOf] {
//      case (registry: CodeGeneratorRegistry[Python], bottom:BottomCardOf) =>
//        Python(s"""${registry(bottom.move).get}[0]""")
//    },

    CodeGeneratorRegistry[Python, AlternatingColors] {
      case (registry: CodeGeneratorRegistry[Python], alternating: AlternatingColors) =>
        Python(s"""isAlternateColorSequence(${registry(alternating.on).get})""")
    },

    CodeGeneratorRegistry[Python, OppositeColor] {
      case (registry: CodeGeneratorRegistry[Python], opposite:OppositeColor) =>
        Python(s"""${registry(opposite.on).get}.color != ${registry(opposite.other).get}.color""")  // TODO: FIX DOUBLE CHECK ORDER
    },

    CodeGeneratorRegistry[Python, IsEmpty] {
      case (registry: CodeGeneratorRegistry[Python], isEmpty:IsEmpty) =>
        Python(s"""len(${registry(isEmpty.on).get}) == 0""")
    },

    CodeGeneratorRegistry[Python, IsSingle] {
      case (registry: CodeGeneratorRegistry[Python], isSingle:IsSingle) =>
        Python(s"""len(${registry(isSingle.on).get}) == 1""")
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
        Python(s"""${registry(sameRank.on).get}.rank == ${registry(sameRank.other).get}.rank""")   // TODO: FIC CHECK ORDER
    },

    CodeGeneratorRegistry[Python, SameSuit] {
      case (registry: CodeGeneratorRegistry[Python], sameSuit:SameSuit) =>
        Python(s"""${registry(sameSuit.on).get}.suit == ${registry(sameSuit.other).get}.suit""")  // TODO: CHECK ORDER
    },

    CodeGeneratorRegistry[Python, OrConstraint] {
      case (registry: CodeGeneratorRegistry[Python], or: OrConstraint) =>
        logger.debug(s"PythonConstraints:Or: ${or.toString}")

        if (or.args.isEmpty) {
          Python(s"""False""")
        } else {
          or.args.tail.foldLeft(registry(or.args.head).get) {
            case (s, c) =>
              Python(s"""($s or ${registry(c).get})""")
          }
        }
    },

    CodeGeneratorRegistry[Python, NotConstraint] {
      case (registry:CodeGeneratorRegistry[Python],not:NotConstraint) =>
        Python(s"""not (${registry(not.inner).get})""")
    })


  // used for map expressions
  val mapGenerators:CodeGeneratorRegistry[Python] = CodeGeneratorRegistry.merge[Python](
    CodeGeneratorRegistry[Python, MapByRank.type] {
      case (_:CodeGeneratorRegistry[Python], MapByRank) =>
        Python(s"""card.rank""")
    },

    CodeGeneratorRegistry[Python, MapBySuit.type] {
      case (_:CodeGeneratorRegistry[Python], MapBySuit) =>
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
class MapExpressionCombinator(m:MapType) extends PythonSemanticTypes {

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
