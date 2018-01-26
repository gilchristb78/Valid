package org.combinators.solitaire.shared.compilation

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.body._
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import domain._
import domain.constraints._
import domain.constraints.movetypes.{BottomCardOf, MoveComponents, TopCardOf}
import org.combinators.cls.types.syntax._
import domain.deal.DealComponents
import domain.deal.map.{MapByRank, MapBySuit, MapCard}
import domain.moves._
import org.combinators.solitaire.shared.JavaSemanticTypes

import scala.collection.JavaConverters._
/**
  * Here is the default code registry for the existing Constraints in Solitaire domain.
  *
  * Over time, variations may introduce their own constraints, which are merged locally
  * within the variation. Should a constraint be used by multiple variations, then promote
  * to the end of this object.
  *
  * All logging through constraintCodeGenerators.logger
  */
object constraintCodeGenerators  {

  // log everything as needed.
  val logger:LoggingAdapter = Logging.getLogger(ActorSystem("ConstraintCodeGenerators"), constraintCodeGenerators.getClass)
  logger.info("Constraint Generation logging active...")

  // Halfway there.
  // TODO: Right now the doGenerator/undoGenerator/helperGenerator are non extensible. However, if
  // TODO: you look at how the constraints were done, there is a way to fold it in. future work.

  //  EnhancedMove
  //  Score ** new one
  //
  // used for do statements expressions
  val doGenerators:CodeGeneratorRegistry[Seq[Statement]] = CodeGeneratorRegistry.merge[Seq[Statement]](
    CodeGeneratorRegistry[Seq[Statement], FlipCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:FlipCardMove) =>
        Java(s"""|Card c = source.get();
                 |c.setFaceUp (!c.isFaceUp());
                 |source.add(c);
                 |""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], SingleCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:SingleCardMove) =>
        Java(s"""destination.add(movingCard);""").statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RemoveMultipleCardsMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RemoveMultipleCardsMove) =>
        Java(s"""|for (Stack s : destinations) {
                 |  removedCards.add(s.get());
                 |}""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RemoveSingleCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RemoveSingleCardMove) =>
        Java(s"""removedCard = source.get();""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], ResetDeckMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:ResetDeckMove) =>
        Java(s"""|// Note destinations contain the stacks that are to
                 |// be reformed into a single deck.
                 |for (Stack s : destinations) {
                 |  while (!s.empty()) {
                 |	source.add(s.get());
                 |  }
                 |}""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], DeckDealMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:DeckDealMove) =>
        Java(s"""|for (Stack s : destinations) {
                 |  s.add (source.get());
                 |}""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], DeckDealNCardsMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], move:DeckDealNCardsMove) =>
        var stmts:String = ""
        for (_ <- 1 to move.numToDeal) {
          stmts = stmts + "s.add (source.get());\n"
        }

        Java(s"""|for (Stack s : destinations) {
                 |  $stmts
                 |}""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], ColumnMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:ColumnMove) =>
        Java(s"""destination.push(movingColumn);""").statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RowMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RowMove) =>
        Java(s"""destination.push(movingRow);""").statements()
    },

    // First example of a nested move. Total points comes from the number of cards field
    //  CodeGeneratorRegistry[Seq[Statement], Score] {
    //    case (registry:CodeGeneratorRegistry[Seq[Statement]], m:Score) =>
    //      val head = Java(s"""!(${registry(m.base).get})""").statements()
    //      val tail = if (m.isSingleCardMove) {
    //         Java(s"""game.updateScore(+1);""").statements()
    //      } else {
    //         // not sure how to discover total number of moved cards without recursively generating it. awkward
    //      }
    //
    //      head ++ tail
    //    },
  )

  // I really want these to be [Seq[BodyDeclaration[_]] but I get some kind of strange Scala error, so this
  // is just String untl I figure out more
  val helperGenerators:CodeGeneratorRegistry[String] = CodeGeneratorRegistry.merge[String](
    CodeGeneratorRegistry[String, FlipCardMove] {
      case (_ : CodeGeneratorRegistry[String], m:FlipCardMove) =>
        val name = new SimpleName(m.getName)
        Java(s"""|//Card movingCard;
                 |public $name(Stack from) {
                 |  this(from, from);
                 |}""".stripMargin).classBodyDeclarations().mkString("\n")
    },

    CodeGeneratorRegistry[String, SingleCardMove] {
      case (_:CodeGeneratorRegistry[String], m:SingleCardMove) =>
        val name = new SimpleName(m.getName)
        Java(s"""|Card movingCard;
                 |public $name(Stack from, Card card, Stack to) {
                 |  this(from, to);
                 |  this.movingCard = card;
                 |}""".stripMargin).classBodyDeclarations().mkString("\n")
    },

    CodeGeneratorRegistry[String, DeckDealMove] {
      case (_:CodeGeneratorRegistry[String], _:DeckDealMove) => ""
    },

    CodeGeneratorRegistry[String, RemoveMultipleCardsMove] {
      case (_:CodeGeneratorRegistry[String], m:RemoveMultipleCardsMove) =>
      val name = new SimpleName(m.getName)
      Java(s"""|java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();
               |public $name(Stack dests[]) {
               |  this(null, dests);
               |}
               |""".stripMargin).classBodyDeclarations().mkString("\n")
    },

    CodeGeneratorRegistry[String, RemoveSingleCardMove] {
      case (_:CodeGeneratorRegistry[String], m:RemoveSingleCardMove) =>
      val name = new SimpleName(m.getName)
      Java(s"""|Card removedCard = null;
               |public $name(Stack src) {
               |  this(src, null);
               |}
               |""".stripMargin).classBodyDeclarations().mkString("\n")
    },

    CodeGeneratorRegistry[String, ResetDeckMove] {
      case (_:CodeGeneratorRegistry[String], _:ResetDeckMove) => ""
    },

    CodeGeneratorRegistry[String, ColumnMove] {
      case (_:CodeGeneratorRegistry[String], m:ColumnMove) =>
      val name = new SimpleName(m.getName)
      Java(s"""|Column movingColumn;
               |int numInColumn;
               |public $name(Stack from, Column cards, Stack to) {
               |  this(from, to);
               |  this.movingColumn = cards;
               |  this.numInColumn = cards.count();
               |}""".stripMargin).classBodyDeclarations().mkString("\n")
    },

    CodeGeneratorRegistry[String, RowMove] {
      case (_:CodeGeneratorRegistry[String], m:RowMove) =>
      val name = new SimpleName(m.getName)
      Java(s"""|Column movingRow;
               |int numInColumn;   // conform to superclass usage.
               |public $name(Stack from, Column cards, Stack to) {
               |  this(from, to);
               |  this.movingRow = cards;
               |  this.numInColumn = cards.count();
               |}""".stripMargin).classBodyDeclarations().mkString("\n")
    },

  )


  // used for do statements expressions
  val undoGenerators:CodeGeneratorRegistry[Seq[Statement]] = CodeGeneratorRegistry.merge[Seq[Statement]](
    CodeGeneratorRegistry[Seq[Statement], FlipCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:FlipCardMove) =>
        Java(s"""|Card c = source.get();
                 |c.setFaceUp (!c.isFaceUp());
                 |source.add(c);
                 |return true;""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], SingleCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:SingleCardMove) =>
        Java(s"""|source.add(destination.get());
                 |return true;""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RemoveMultipleCardsMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RemoveMultipleCardsMove) =>
        Java(s"""|for (Stack s : destinations) {
                 |  s.add(removedCards.remove(0));
                 |}
                 |return true;""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RemoveSingleCardMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RemoveSingleCardMove) =>
        Java(s"""|source.add(removedCard);
                 |return true;""".stripMargin).statements()

    },

    CodeGeneratorRegistry[Seq[Statement], DeckDealMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:DeckDealMove) =>
        Java(s"""|for (Stack s : destinations) {
                 |  source.add(s.get());
                 |}
                 |return true;""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], DeckDealNCardsMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], move:DeckDealNCardsMove) =>
        var stmts:String = ""
        for (_ <- 1 to move.numToDeal) {
          stmts = stmts + "source.add (s.get());\n"
        }

        Java(s"""|for (Stack s : destinations) {
                 |  $stmts
                 |}
                 |return true;""".stripMargin).statements()
    },

    CodeGeneratorRegistry[Seq[Statement], ResetDeckMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:ResetDeckMove) =>
        Java("return false;").statements()
    },


    CodeGeneratorRegistry[Seq[Statement], ColumnMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:ColumnMove) =>
        Java(s"""|destination.select(numInColumn);
                 |source.push(destination.getSelected());
                 |return true;""".stripMargin)
          .statements()
    },

    CodeGeneratorRegistry[Seq[Statement], RowMove] {
      case (_:CodeGeneratorRegistry[Seq[Statement]], _:RowMove) =>
        Java(s"""|destination.select(numInColumn);     // conform to superclass usage
                 |source.push(destination.getSelected());
                 |return true;""".stripMargin)
          .statements()
    },

  )


  // used for map expressions
  val mapGenerators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](
    CodeGeneratorRegistry[Expression, MapByRank] {
      case (_:CodeGeneratorRegistry[Expression], _:MapByRank) =>
        Java(s"""card.getRank() - Card.ACE""").expression()
    },

    CodeGeneratorRegistry[Expression, MapBySuit] {
      case (_:CodeGeneratorRegistry[Expression], _:MapBySuit) =>
        Java(s"""card.getSuit() - Card.CLUBS""").expression()
    },

  )

  val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

    CodeGeneratorRegistry[Expression, MoveComponents] {
      case (_:CodeGeneratorRegistry[Expression], mc:MoveComponents) =>
        Java(s"""${mc.name}""").expression()
    },

    CodeGeneratorRegistry[Expression, DealComponents] {
      case (_:CodeGeneratorRegistry[Expression], dc:DealComponents) =>
        Java(s"""${dc.name}""").expression()
    },

    CodeGeneratorRegistry[Expression, AndConstraint] {
      case (registry:CodeGeneratorRegistry[Expression], and: AndConstraint) =>
        logger.debug("And:" + and.toString)
        if (and.constraints.isEmpty) {
          Java(s"""true""").expression()
        } else {
          and.constraints.tail.foldLeft(registry(and.constraints.head).get) {
            case (s, c) =>
              val inner = registry(c)
              if (inner.isEmpty) {
                Java (s"""${c.toString}""").expression()
              } else {
                Java(s"""($s && ${inner.get})""").expression()
              }
          }
        }
    },

    /** Takes advantage of () -> operator in Java 1.8. */
    CodeGeneratorRegistry[Expression, IfConstraint] {
      case (registry: CodeGeneratorRegistry[Expression], ifCons: IfConstraint) =>
        logger.debug("If:" + ifCons.toString)

        val inner = registry(ifCons.constraint)
        val trueb = registry(ifCons.trueBranch)
        val falseb = registry(ifCons.falseBranch)

        val str = s"""ConstraintHelper.ifCompute (
                     |${inner.get},
                     |() -> ${trueb.get},
                     |() -> ${falseb.get})""".stripMargin
        logger.debug("If-inner:" + str)
        Java(s"""$str""").expression()
    },

    CodeGeneratorRegistry[Expression, Truth] {
      case (_:CodeGeneratorRegistry[Expression], _:Truth) =>
        Java(s"""true""").expression()
    },

    CodeGeneratorRegistry[Expression, Falsehood] {
      case (_:CodeGeneratorRegistry[Expression], _:Falsehood) =>
        Java(s"""false""").expression()
    },

    CodeGeneratorRegistry[Expression, IsAce] {
      case (registry: CodeGeneratorRegistry[Expression], isAce: IsAce) =>
        Java(s"""${registry(isAce.element).get}.getRank() == Card.ACE""").expression()
    },

    // In Kombat solitaire, Suits are: CLUBS=1, DIAMONDS=2, HEARTS=3, SPADES=4. */
    CodeGeneratorRegistry[Expression, IsSuit] {
      case (registry: CodeGeneratorRegistry[Expression], isSuit: IsSuit) =>
        Java(s"""${registry(isSuit.element).get}.getSuit() == ${isSuit.suit.value()}""").expression()
    },

    // In Kombat solitaire, Ranks are: Aces1, Two=2, ... Queen=12, King = 13. */
    CodeGeneratorRegistry[Expression, IsRank] {
      case (registry: CodeGeneratorRegistry[Expression], isRank: IsRank) =>
        Java(s"""${registry(isRank.element).get}.getRank() == ${isRank.rank.value()}""").expression()
    },

    CodeGeneratorRegistry[Expression, IsKing] {
      case (registry: CodeGeneratorRegistry[Expression], isKing: IsKing) =>
        Java(s"""${registry(isKing.element).get}.getRank() == Card.KING""").expression()
    },

    CodeGeneratorRegistry[Expression, IsFaceUp] {
      case (registry: CodeGeneratorRegistry[Expression], c: IsFaceUp) =>
        Java(s"""${registry(c.element).get}.isFaceUp()""").expression()
    },

    /** Note: This code will be invoked in a method that has game as an argument. */
    CodeGeneratorRegistry[Expression, SolitaireContainerTypes] {
      case (_:CodeGeneratorRegistry[Expression], st:SolitaireContainerTypes) =>
        Java(s"""ConstraintHelper.${st.name}(game)""").expression()
    },

    CodeGeneratorRegistry[Expression, Descending] {
      case (registry: CodeGeneratorRegistry[Expression], descending: Descending) =>
        Java(s"""${registry(descending.base).get}.descending()""").expression()
    },

    CodeGeneratorRegistry[Expression, TopCardOf] {
      case (registry: CodeGeneratorRegistry[Expression], top:TopCardOf) =>
        Java(s"""${registry(top.base).get}.peek()""").expression()
    },

    CodeGeneratorRegistry[Expression, BottomCardOf] {
      case (registry: CodeGeneratorRegistry[Expression], bottom:BottomCardOf) =>
        Java(s"""${registry(bottom.base).get}.peek(0)""").expression()
    },

    CodeGeneratorRegistry[Expression, AlternatingColors] {
      case (registry: CodeGeneratorRegistry[Expression], alternating: AlternatingColors) =>
        Java(s"""${registry(alternating.base).get}.alternatingColors()""").expression()
    },

    CodeGeneratorRegistry[Expression, OppositeColor] {
      case (registry: CodeGeneratorRegistry[Expression], opposite:OppositeColor) =>
        Java(s"""${registry(opposite.left).get}.oppositeColor(${registry(opposite.right).get})""").expression()
    },

    CodeGeneratorRegistry[Expression, IsEmpty] {
      case (registry: CodeGeneratorRegistry[Expression], isEmpty:IsEmpty) =>
        Java(s"""${registry(isEmpty.element).get}.empty()""").expression()
    },

    /** Specialized isSingle, which has to deal with redundant 'isSingle' for MovingCard. */
    CodeGeneratorRegistry[Expression, IsSingle] {
      case (registry: CodeGeneratorRegistry[Expression], isSingle:IsSingle) =>
        if (isSingle.element.isSingleCard) {
          Java(s"""${registry(isSingle.element).get} != null""").expression()
        } else {
          Java(s"""${registry(isSingle.element).get}.count() == 1""").expression()
        }
    },

    CodeGeneratorRegistry[Expression, NextRank] {
      case (registry: CodeGeneratorRegistry[Expression], nextRank:NextRank) =>
        Java(s"""${registry(nextRank.higher).get}.getRank() == ${registry(nextRank.lower).get}.getRank() + 1""").expression()
    },

    CodeGeneratorRegistry[Expression, HigherRank] {
      case (registry: CodeGeneratorRegistry[Expression], higherRank:HigherRank) =>
        Java(s"""${registry(higherRank.higher).get}.getRank() > ${registry(higherRank.lower).get}.getRank()""").expression()
    },

    CodeGeneratorRegistry[Expression, SameRank] {
      case (registry: CodeGeneratorRegistry[Expression], sameRank:SameRank) =>
        Java(s"""${registry(sameRank.left).get}.getRank() == ${registry(sameRank.right).get}.getRank()""").expression()
    },

    CodeGeneratorRegistry[Expression, SameSuit] {
      case (registry: CodeGeneratorRegistry[Expression], sameSuit:SameSuit) =>
        Java(s"""${registry(sameSuit.left).get}.getSuit() == ${registry(sameSuit.right).get}.getSuit()""").expression()
    },

    CodeGeneratorRegistry[Expression, OrConstraint] {
      case (registry: CodeGeneratorRegistry[Expression], or: OrConstraint) =>
        logger.debug("Or:" + or.toString)
        if (or.constraints.isEmpty) {
          Java(s"""false""").expression()
        } else {
          or.constraints.tail.foldLeft(registry(or.constraints.head).get) {
            case (s, c) =>
              Java(s"""($s || ${registry(c).get})""").expression()
          }
        }
    },

    CodeGeneratorRegistry[Expression, NotConstraint] {
      case (registry:CodeGeneratorRegistry[Expression],not:NotConstraint) =>
        Java(s"""!(${registry(not.constraint).get})""").expression()
    })
}


/** Provides a return (EXPR) statement. */
class StatementCombinator(c:Constraint, moveSymbol:Type) extends JavaSemanticTypes {

  def apply(generators: CodeGeneratorRegistry[Expression]): Seq[Statement] = {
    val cc3: Option[Expression] = generators(c)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("CodeGeneratorRegistry: Unable to locate:" + c.toString)
      Seq.empty
    } else {
      Java(s"""return ${cc3.get};""").statements()
    }
  }

  var semanticType: Type = constraints(constraints.generator) =>: move(moveSymbol, move.validStatements)
}

/** When used, it isn't important what semantic Type is, which is why we omit it. */
class ExpressionCombinator(c:Constraint) extends JavaSemanticTypes {

  def apply(generators: CodeGeneratorRegistry[Expression]): Expression = {
    val cc3: Option[Expression] = generators(c)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("ExpressionCombinator: Unable to locate:" + c.toString)
      Java("false").expression()
    } else {
      cc3.get
    }
  }
}

/** When used, it isn't important what semantic Type is, which is why we omit it. */
class MapExpressionCombinator(m:MapCard) extends JavaSemanticTypes {

  def apply(generators: CodeGeneratorRegistry[Expression]): Expression = {
    val cc3: Option[Expression] = generators(m)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("MapExpressionCombinator: Unable to locate:" + m.toString)
      Java("false").expression()
    } else {
      cc3.get
    }
  }
}

class SeqStatementCombinator(m:Move) extends JavaSemanticTypes {
  def apply(generators: CodeGeneratorRegistry[Seq[Statement]]): Seq[Statement] = {
    val cc3: Option[Seq[Statement]] = generators(m)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("SeqStatementCombinator: Unable to locate:" + m.toString)
      val empty:Seq[Statement] = Seq.empty

      empty
    } else {
      cc3.get
    }
  }
}


// should be Seq[BodyDeclaration[_]] but can't get to compile, so make String for now
class HelperDeclarationCombinator(m:Move) extends JavaSemanticTypes {
  def apply(generators: CodeGeneratorRegistry[String]): String = {
    val cc3: Option[String] = generators(m)
    if (cc3.isEmpty) {
      constraintCodeGenerators.logger.error("HelperDeclarationCombinator: Unable to locate:" + m.toString)

      ""
    } else {
      cc3.get
    }
  }
}

object generateHelper {
  /**
    * Helper method for the ConstraintHelper class
    */
  def fieldAccessHelper(name:String, fieldName:String) : MethodDeclaration = {
    Java(s"""|public static Stack[] $name(Solitaire game) {
             |  return getVariation(game).$fieldName;
             |}
          """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head
  }

  def fieldAccessOneHelper(name:String, fieldName:String) : MethodDeclaration = {
    Java(s"""|public static Stack $name(Solitaire game) {
             |  return getVariation(game).$fieldName;
             |}
          """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head
  }


  /**
    * Generate all helpers for the given solitaire domain.
    *
    * @param sol   Solitaire variation
    */
  def helpers(sol:Solitaire) : Seq[MethodDeclaration] = {
    var methods:Seq[MethodDeclaration] = Seq.empty
    for (containerType:ContainerType <- sol.structure.keySet.asScala) {
      val container = sol.structure.get(containerType)
      val name = containerType.getName

      container match {
        case _: Stock =>
          methods = methods :+ generateHelper.fieldAccessOneHelper(name, name)

        case _ =>
          methods = methods :+ generateHelper.fieldAccessHelper(name, name)
      }
    }

    methods
  }
}
