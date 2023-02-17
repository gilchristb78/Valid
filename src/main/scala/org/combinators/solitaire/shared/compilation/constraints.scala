package org.combinators.solitaire.shared.compilation

import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.body._
import com.typesafe.scalalogging.LazyLogging
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared.JavaSemanticTypes
import org.combinators.solitaire.domain._

/**
  * Here is the default code registry for the existing Constraints in Solitaire domain.
  *
  * Over time, variations may introduce their own constraints, which are merged locally
  * within the variation. Should a constraint be used by multiple variations, then promote
  * to the end of this object.
  *
  * All logging through constraintCodeGenerators.logger
  */
object constraintCodeGenerators extends LazyLogging {

  def ordinal(s:Suit):Int = {
    s match {
      case Clubs => 1
      case Diamonds => 2
      case Hearts => 3
      case Spades => 4
    }
  }

  def ordinal(r:Rank):Int = {
    r match {
      case Ace   => 1
      case Two   => 2
      case Three => 3
      case Four  => 4
      case Five  => 5
      case Six   => 6
      case Seven => 7
      case Eight => 8
      case Nine  => 9
      case Ten   => 10
      case Jack  => 11
      case Queen => 12
      case King  => 13
    }
  }

  // log everything as needed.
  //val logger:LoggingAdapter = Logging.getLogger(ActorSystem("ConstraintCodeGenerators"), constraintCodeGenerators.getClass)
  logger.info("Constraint Generation logging active...")

  // used for do statements expressions
  val doGenerators:CodeGeneratorRegistry[Seq[Statement]] = CodeGeneratorRegistry.merge[Seq[Statement]](
    CodeGeneratorRegistry[Seq[Statement], Move] {
      case (_ : CodeGeneratorRegistry[Seq[Statement]], move) =>
        move.moveType match {
          case FlipCard =>
              Java(s"""|Card c = source.get();
                       |c.setFaceUp (!c.isFaceUp());
                       |source.add(c);
                       |""".stripMargin).statements()
          case SingleCard =>
              Java(s"destination.add(movingCard);").statements()
          case RemoveMultipleCards =>
              Java(s"""|for (Stack s : destinations) {
                       |  removedCards.add(s.get());
                       |}""".stripMargin).statements()
          case RemoveStack =>
            Java(
              s"""|while(!destination.empty()){
                  |  removedCards.add(destination.get());
                  |}""".stripMargin).statements()
        case RemoveSingleCard =>
              Java(s"removedCard = source.get();".stripMargin).statements()
        case ResetDeck =>
              Java(s"""|// Note destinations contain the stacks that are to
                       |// be reformed into a single deck.
                       |for (Stack s : destinations) {
                       |  while (!s.empty()) {
                       |	   source.add(s.get());
                       |  }
                       |}""".stripMargin).statements()
        case DealDeck(numCards) =>  // Must protect against attempting to deal too many cards
              val stmts:Seq[Statement] = Seq.fill(numCards)(
                Java("if (!source.empty()) { s.add (source.get()); }").statement)

              Java(s"""|for (Stack s : destinations) {
                       |  ${stmts.mkString("\n")}
                       |}""".stripMargin).statements()
        case MultipleCards =>
              Java(s"destination.push(movingCards);").statements()
        }
    }
  )

  // TODO: I really want these to be [Seq[BodyDeclaration[_]] but I get some kind of strange Scala error, so this
  // TODO: is just String until I figure out more
  val helperGenerators:CodeGeneratorRegistry[String] = CodeGeneratorRegistry.merge[String](
    CodeGeneratorRegistry[String, Move] {
      case (_ : CodeGeneratorRegistry[String], move) =>
        move.moveType match {
          case FlipCard =>
              val name = move.name
              Java(s"""|//Card movingCard;
                       |public $name(Stack from) {
                       |  this(from, from);
                       |}""".stripMargin).classBodyDeclarations().mkString("\n")
          case SingleCard =>
              val name = move.name
              Java(s"""|Card movingCard;
                       |public $name(Stack from, Card card, Stack to) {
                       |  this(from, to);
                       |  this.movingCard = card;
                       |}""".stripMargin).classBodyDeclarations().mkString("\n")
          case DealDeck(numCards) => ""
          case RemoveMultipleCards =>
              val name = move.name
              Java(s"""|java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();
                       |public $name(Stack dests[]) {
                       |  this(null, dests);
                       |}
                       |""".stripMargin).classBodyDeclarations().mkString("\n")
          case RemoveStack =>
            val name = move.name
            Java(
              s"""|java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();
                  |public $name(Stack dests) {
                  |  this(null, dests);
                  |}
                  |""".stripMargin).classBodyDeclarations().mkString("\n")
          case RemoveSingleCard =>
              val name = move.name
              Java(s"""|Card removedCard = null;
                       |public $name(Stack src) {
                       |  this(src, null);
                       |}
                       |""".stripMargin).classBodyDeclarations().mkString("\n")
          case ResetDeck =>
              Java(s"static int numReset = 1;").classBodyDeclarations().mkString("\n")

          case MultipleCards =>
                val name = move.name
                Java(s"""|Stack movingCards;
                         |int numCards;
                         |public $name(Stack from, Stack cards, Stack to) {
                         |  this(from, to);
                         |  this.movingCards = cards;
                         |  this.numCards = cards.count();
                         |}""".stripMargin).classBodyDeclarations().mkString("\n")
      }
    }
  )


//  CodeGeneratorRegistry[Expression, NextRank] {
//    case (registry: CodeGeneratorRegistry[Expression], NextRank(higher, lower, true)) =>
//      Java(s"""${registry(higher).get}.getRank() == ${registry(lower).get}.getRank() + 1""").expression()
//    case (registry: CodeGeneratorRegistry[Expression], NextRank(higher, lower, false)) =>
//      Java(s"""${registry(higher).get}.getRank() == (${registry(lower).get}.getRank() % 13) + 1""").expression()
//  },

  // used for do statements expressions
  val undoGenerators:CodeGeneratorRegistry[Seq[Statement]] = CodeGeneratorRegistry.merge[Seq[Statement]](
    CodeGeneratorRegistry[Seq[Statement], Move] {
      case (_ : CodeGeneratorRegistry[Seq[Statement]], move) =>
        move.moveType match {
          case FlipCard =>
              Java(s"""|Card c = source.get();
                       |c.setFaceUp (!c.isFaceUp());
                       |source.add(c);
                       |return true;""".stripMargin).statements()
          case SingleCard =>
              Java(s"""|source.add(destination.get());
                       |return true;""".stripMargin).statements()
          case RemoveMultipleCards =>
              Java(s"""|for (Stack s : destinations) {
                       |  s.add(removedCards.remove(0));
                       |}
                       |return true;""".stripMargin).statements()
          case RemoveStack =>
            Java(
              s"""|while(!removedCards.isEmpty()){
                  |  destination.add(removedCards.remove(0));
                  |}
                  |return true;""".stripMargin).statements()
          case RemoveSingleCard =>
              Java(s"""|source.add(removedCard);
                       |return true;""".stripMargin).statements()
          case DealDeck(numCards)  =>
              val stmts:Seq[Statement] = Seq.fill(numCards)(Java("source.add (s.get());").statement)

              Java(s"""|for (Stack s : destinations) {
                       |  ${stmts.mkString("\n")}
                       |}
                       |return true;""".stripMargin).statements()
          case ResetDeck =>
              Java("return false;").statements()

          case MultipleCards =>
              Java(s"""|destination.select(numCards);
                       |source.push(destination.getSelected());
                       |return true;""".stripMargin).statements()
        }
    }
  )

  // used for map expressions
  val mapGenerators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](
    CodeGeneratorRegistry[Expression, MapByRank.type] {
      case (_:CodeGeneratorRegistry[Expression], MapByRank) =>
        Java(s"""card.getRank() - Card.ACE""").expression()
    },

    CodeGeneratorRegistry[Expression, MapBySuit.type] {
      case (_:CodeGeneratorRegistry[Expression], MapBySuit) =>
        Java(s"""card.getSuit() - Card.CLUBS""").expression()
    },
  )

  /**
    * Fundamental generators used for expressions derived by constraints. These appear in the
    * move subclasses.
    *
    * Every variation must define a combinator like the following if they *DO NOT* add additional
    * constraints:
    *
    * {{{
    * @combinator object defaultGenerator {
    *    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    *
    *    val semanticType: Type = constraints(constraints.generator)
    * }
    * }}}
    *
    * And if a variation chooses to extend the constraint generation, then do so like so. Create
    * a local object which merges with the default expression generator
    *
    * {{{
    * object freecellCodeGenerator {
    *   val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](
    *
    *   CodeGeneratorRegistry[Expression, SufficientFree] {
    *    case (registry:CodeGeneratorRegistry[Expression], c:SufficientFree) =>
    *      val destination = registry(c.destination).get
    *      val src = registry(c.src).get
    *      val column = registry(c.column).get
    *      val reserve = registry(c.reserve).get
    *      val tableau = registry(c.tableau).get
    *      Java(s"""ConstraintHelper.sufficientFree($column, $src, $destination, $reserve, $tableau)""").expression()
    *   },
    *
    *   ).merge(constraintCodeGenerators.generators)
    * }
    *
    * @combinator object FreeCellGenerator {
    *   def apply: CodeGeneratorRegistry[Expression] = freecellCodeGenerator.generators
    *   val semanticType: Type = constraints(constraints.generator)
    * }
    * }}}
    */
  val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

    CodeGeneratorRegistry[Expression, Source.type] {
      case (_:CodeGeneratorRegistry[Expression], Source) => Java(s"source").expression()
    },
    CodeGeneratorRegistry[Expression, Destination.type] {
      case (_:CodeGeneratorRegistry[Expression], Destination) => Java(s"destination").expression()
    },
    CodeGeneratorRegistry[Expression, MovingCard.type] {
      case (_:CodeGeneratorRegistry[Expression], MovingCard) => Java(s"movingCard").expression()
    },
    CodeGeneratorRegistry[Expression, MovingCards.type] {
      case (_:CodeGeneratorRegistry[Expression], MovingCards) => Java(s"movingCards").expression()
    },

    CodeGeneratorRegistry[Expression, DealComponents.type] {
      case (_:CodeGeneratorRegistry[Expression], DealComponents) =>
        Java("card").expression()
    },

    CodeGeneratorRegistry[Expression, AndConstraint] {
      case (registry:CodeGeneratorRegistry[Expression], and: AndConstraint) =>
        logger.debug("And:" + and.toString)
        if (and.args.isEmpty) {
          Java(s"""true""").expression()
        } else {
          and.args.tail.foldLeft(registry(and.args.head).get) {
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

        val inner = registry(ifCons.guard)
        val trueb = registry(ifCons.trueBranch)
        val falseb = registry(ifCons.falseBranch)

        val str = s"""ConstraintHelper.ifCompute (
                     |${inner.get},
                     |() -> ${trueb.get},
                     |() -> ${falseb.get})""".stripMargin
        logger.debug("If-inner:" + str)
        Java(s"""$str""").expression()
    },

    CodeGeneratorRegistry[Expression, Truth.type] {
      case (_:CodeGeneratorRegistry[Expression], Truth) =>
        Java(s"true").expression()
    },

    CodeGeneratorRegistry[Expression, Falsehood.type] {
      case (_:CodeGeneratorRegistry[Expression], Falsehood) =>
        Java(s"false").expression()
    },

    CodeGeneratorRegistry[Expression, IsAce] {
      case (registry: CodeGeneratorRegistry[Expression], IsAce(on)) =>
        Java(s"${registry(on).get}.getRank() == Card.ACE").expression()
    },

    // In Kombat solitaire, Suits are: CLUBS=1, DIAMONDS=2, HEARTS=3, SPADES=4. */
    CodeGeneratorRegistry[Expression, IsSuit] {
      case (registry: CodeGeneratorRegistry[Expression], IsSuit(on,suit)) =>
        Java(s"${registry(on).get}.getSuit() == ${ordinal(suit)}").expression()
    },

    // In Kombat solitaire, Ranks are: Aces1, Two=2, ... Queen=12, King = 13. */
    CodeGeneratorRegistry[Expression, IsRank] {
      case (registry: CodeGeneratorRegistry[Expression], IsRank(on,rank)) =>
        Java(s"${registry(on).get}.getRank() == ${ordinal(rank)}").expression()
    },

    CodeGeneratorRegistry[Expression, IsKing] {
      case (registry: CodeGeneratorRegistry[Expression], IsKing(on)) =>
        Java(s"""${registry(on).get}.getRank() == Card.KING""").expression()
    },

    CodeGeneratorRegistry[Expression, IsFaceUp] {
      case (registry: CodeGeneratorRegistry[Expression], IsFaceUp(on)) =>
        Java(s"""${registry(on).get}.isFaceUp()""").expression()
    },

    /** Note: This code will be invoked in a method that has game as an argument. */
    CodeGeneratorRegistry[Expression, Tableau.type] {
      case (_:CodeGeneratorRegistry[Expression], Tableau) =>
        Java(s"""ConstraintHelper.tableau(game)""").expression()
    },

    // TODO: ADD ALL OTHERS

    CodeGeneratorRegistry[Expression, Descending] {
      case (registry: CodeGeneratorRegistry[Expression], Descending(on)) =>
        Java(s"${registry(on).get}.descending()").expression()
    },

    CodeGeneratorRegistry[Expression, TopCardOf] {
      case (registry: CodeGeneratorRegistry[Expression], TopCardOf(on)) =>
        Java(s"${registry(on).get}.peek()").expression()
    },

    CodeGeneratorRegistry[Expression, BottomCardOf] {
      case (registry: CodeGeneratorRegistry[Expression], BottomCardOf(on)) =>
        Java(s"${registry(on).get}.peek(0)").expression()
    },

    CodeGeneratorRegistry[Expression, AlternatingColors] {
      case (registry: CodeGeneratorRegistry[Expression], AlternatingColors(on)) =>
        Java(s"${registry(on).get}.alternatingColors()").expression()
    },

    CodeGeneratorRegistry[Expression, OppositeColor] {
      case (registry: CodeGeneratorRegistry[Expression], OppositeColor(on,other)) =>
        Java(s"${registry(on).get}.oppositeColor(${registry(other).get})").expression()
    },

    CodeGeneratorRegistry[Expression, SameColor] {
      case (registry: CodeGeneratorRegistry[Expression], SameColor(on,other)) =>
        Java(s"${registry(on).get}.sameColor(${registry(other).get})").expression()
    },

    CodeGeneratorRegistry[Expression, IsEmpty] {
      case (registry: CodeGeneratorRegistry[Expression], IsEmpty(on)) =>
        Java(s"""${registry(on).get}.empty()""").expression()
    },

    /** Specialized isSingle, which has to deal with redundant 'isSingle' for MovingCard. */
    CodeGeneratorRegistry[Expression, IsSingle] {
      case (registry: CodeGeneratorRegistry[Expression], IsSingle(on)) =>
        if (on.isSingleCard) {
          Java(s"""${registry(on).get} != null""").expression()
        } else {
          Java(s"""${registry(on).get}.count() == 1""").expression()
        }
    },

    CodeGeneratorRegistry[Expression, NextRank] {
      case (registry: CodeGeneratorRegistry[Expression], NextRank(higher, lower, wrapAround)) =>
        if (wrapAround) {
          Java(s"""${registry(higher).get}.getRank() == ${registry(lower).get}.getRank() + 1""").expression()
        } else {
          Java(s"""${registry(higher).get}.getRank() == (${registry(lower).get}.getRank() % 13) + 1""").expression()
        }
    },

    CodeGeneratorRegistry[Expression, HigherRank] {
      case (registry: CodeGeneratorRegistry[Expression], higherRank:HigherRank) =>
        Java(s"""${registry(higherRank.higher).get}.getRank() > ${registry(higherRank.lower).get}.getRank()""").expression()
    },

    CodeGeneratorRegistry[Expression, SameRank] {
      case (registry: CodeGeneratorRegistry[Expression], SameRank(on, other)) =>
        Java(s"""${registry(on).get}.getRank() == ${registry(other).get}.getRank()""").expression()
    },

    CodeGeneratorRegistry[Expression, SameSuit] {
      case (registry: CodeGeneratorRegistry[Expression], SameSuit(on, other)) =>
        Java(s"""${registry(on).get}.getSuit() == ${registry(other).get}.getSuit()""").expression()
    },

    CodeGeneratorRegistry[Expression, OrConstraint] {
      case (registry: CodeGeneratorRegistry[Expression], or: OrConstraint) =>
        logger.debug("Or:" + or.toString)
        if (or.args.isEmpty) {
          Java(s"""false""").expression()
        } else {
          or.args.tail.foldLeft(registry(or.args.head).get) {
            case (s, c) =>
              Java(s"""($s || ${registry(c).get})""").expression()
          }
        }
    },

    CodeGeneratorRegistry[Expression, NotConstraint] {
      case (registry:CodeGeneratorRegistry[Expression],not:NotConstraint) =>
        Java(s"""!(${registry(not.inner).get})""").expression()
    })
}


/** Provides a return (EXPR) statement. */
class StatementCombinator(c:Constraint, moveSymbol:Type) extends JavaSemanticTypes with LazyLogging {

  def apply(generators: CodeGeneratorRegistry[Expression]): Seq[Statement] = {
    val cc3: Option[Expression] = generators(c)
    if (cc3.isEmpty) {
      logger.error("CodeGeneratorRegistry: Unable to locate:" + c.toString)
      Seq.empty
    } else {
      Java(s"""return ${cc3.get};""").statements()
    }
  }

  var semanticType: Type = constraints(constraints.generator) =>: move(moveSymbol, move.validStatements)
}

/** When used, it isn't important what semantic Type is, which is why we omit it. */
class ExpressionCombinator(c:Constraint) extends JavaSemanticTypes with LazyLogging {

  def apply(generators: CodeGeneratorRegistry[Expression]): Expression = {
    val cc3: Option[Expression] = generators(c)
    if (cc3.isEmpty) {
      logger.error("ExpressionCombinator: Unable to locate:" + c.toString)
      Java("false").expression()
    } else {
      cc3.get
    }
  }
}

/** When used, it isn't important what semantic Type is, which is why we omit it. */
class MapExpressionCombinator(m:MapType) extends JavaSemanticTypes with LazyLogging {

  def apply(generators: CodeGeneratorRegistry[Expression]): Expression = {
    val cc3: Option[Expression] = generators(m)
    if (cc3.isEmpty) {
      logger.error("MapExpressionCombinator: Unable to locate:" + m.toString)
      Java("false").expression()
    } else {
      cc3.get
    }
  }
}

class SeqStatementCombinator(m:Move) extends JavaSemanticTypes with LazyLogging {
  def apply(generators: CodeGeneratorRegistry[Seq[Statement]]): Seq[Statement] = {
    val cc3: Option[Seq[Statement]] = generators(m)
    if (cc3.isEmpty) {
      logger.error("SeqStatementCombinator: Unable to locate:" + m.toString)
      val empty:Seq[Statement] = Seq.empty

      empty
    } else {
      cc3.get
    }
  }
}


// should be Seq[BodyDeclaration[_]] but can't get to compile, so make String for now
class HelperDeclarationCombinator(m:Move) extends JavaSemanticTypes with LazyLogging {
  def apply(generators: CodeGeneratorRegistry[String]): String = {
    val cc3: Option[String] = generators(m)
    if (cc3.isEmpty) {
      logger.error("HelperDeclarationCombinator: Unable to locate:" + m.toString)

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
  def helpers(sol:Solitaire) : Seq[BodyDeclaration[_]] = {
    var methods:Seq[MethodDeclaration] = Seq.empty

    for (containerType:ContainerType <- sol.structure.keySet) {
      val name = containerType.name

      containerType match {
        case StockContainer =>
          methods = methods :+ generateHelper.fieldAccessOneHelper("deck", "deck") // TODO: Diverged for decks.

        case _ =>
          methods = methods :+ generateHelper.fieldAccessHelper(name, name)
      }
    }

    methods
  }
}
