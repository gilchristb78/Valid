package org.combinators.solitaire.shared
import org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.types.Type
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared.compilation._
import org.combinators.templating.twirl.Java
import org.combinators.cls.types.syntax._


trait UnitTestCaseGeneration extends Base with shared.Moves with generic.JavaCodeIdioms with SemanticTypes {

  // generative classes for each of the required elements. This seems much more generic than worth being buried here.
  // PLEASE SIMPLIFY. TODO
  // HACK
  class SolitaireTestSuite(solitaire:Solitaire) {

    def max(c:Constraint) : Int = {
      c match {
        case andc:AndConstraint => andc.args.map(c => max(c)).sum
        case ifc:IfConstraint => max(ifc.falseBranch) + max(ifc.trueBranch)
        case notc:NotConstraint => max(notc.inner)
        case orc:OrConstraint => orc.args.map(c => max(c)).sum
        case _ => 1
      }
    }

    def flatten(cons:Constraint) : Seq[Constraint] = {
      cons match {
        case andc: AndConstraint => andc.args.flatMap(c => flatten(c))
        case ifc: IfConstraint => flatten(ifc.falseBranch) ++ flatten(ifc.trueBranch)
        case notc: NotConstraint => flatten(notc.inner)
        case orc: OrConstraint => orc.args.flatMap(c => flatten(c))
        case c: Constraint => Seq(c)
      }
    }

    val foundIndex = -1
    def nthAtomicConstraint(c:Constraint, ctr:Int, targetIndex:Int) : (Option[Constraint], Int) = {
      c match {
        case andc:AndConstraint =>
          var newctr:Int = ctr
          var found:Option[Constraint] = None

          andc.args.foreach(arg => {
            if (found.isEmpty) {
              val rc = nthAtomicConstraint(arg, newctr, targetIndex)
              if (rc._2 == foundIndex) {
                found = rc._1
              }

              newctr = rc._2
            }
          })

          if (found.isDefined) {
            (found, foundIndex)
          } else {
            (None, newctr)
          }

        case ifc:IfConstraint =>
          val fbIdx = nthAtomicConstraint(ifc.falseBranch, ctr, targetIndex)
          if (fbIdx._2 == foundIndex) {
            fbIdx
          } else {
            val tbIdx = nthAtomicConstraint(ifc.trueBranch, fbIdx._2, targetIndex)
            if (tbIdx._2 == foundIndex) {
              tbIdx
            } else {
              (None, tbIdx._2)
            }
          }

        case notc:NotConstraint =>
          nthAtomicConstraint(notc.inner, ctr, targetIndex)

        case orc:OrConstraint =>
          var newctr:Int = ctr
          var argc:Seq[Constraint] = Seq.empty
          var found:Option[Constraint] = None
          orc.args.foreach(arg => {
            if (found.isEmpty) {
              val rc = nthAtomicConstraint(arg, newctr, targetIndex)
              if (rc._2 == foundIndex) {
                found = rc._1
              }

              newctr = rc._2
            }
          })

          if (found.isDefined) {
            (found, foundIndex)
          } else {
            (None, newctr)
          }

        case c:Constraint =>
          if (ctr == targetIndex) {
            (Some(c), foundIndex)  // found
          } else {
            (None, ctr+1)
          }
      }
    }

    /**
      * Locate one constraint and negate it
      * @param c
      * @param ctr
      * @param flipIndex
      * @return
      */
    def perturb(c:Constraint, ctr:Int, flipIndex:Int) : (Constraint,Int) = {
      c match {
          case andc:AndConstraint =>
            var newctr:Int = ctr
            var argc:Seq[Constraint] = Seq.empty
            andc.args.foreach(arg => {
              val rc = perturb(arg, newctr, flipIndex)
              argc = argc :+ rc._1
              newctr = rc._2
            })

            (AndConstraint(argc : _*), newctr)

          case ifc:IfConstraint =>
            val fbIdx = perturb(ifc.falseBranch, ctr, flipIndex)
            val tbIdx = perturb(ifc.trueBranch, fbIdx._2, flipIndex)
            (IfConstraint(fbIdx._1, tbIdx._1), tbIdx._2)

          case notc:NotConstraint =>
            val rc = perturb(notc.inner, ctr, flipIndex)
            (NotConstraint(rc._1), rc._2)

          case orc:OrConstraint =>
            var newctr:Int = ctr
            var argc:Seq[Constraint] = Seq.empty
            orc.args.foreach(arg => {
              val rc = perturb(arg, newctr, flipIndex)
              argc = argc :+ rc._1
              newctr = rc._2
            })

            (OrConstraint(argc : _*), newctr)

          case c:Constraint =>
            if (ctr == flipIndex) {
              (NotConstraint(c), ctr+1)
            } else {
              (c, ctr+1)
            }
      }
    }

    def isEmptyNegative(constraint: IsEmpty, isSingle:Boolean) : Seq[Statement] = {
      //constraint.on.
      var place = "destination"
      var amount = "getValidStack()"
      if(constraint.toString.contains("Source")){
        place = "source"
        amount = "new Deck()"
      }

      Java(
        s"""
           |$place = $amount;
           |""".stripMargin).statements()
    }

    /**
      * Statements to build up either a single non-King (queen of clubs) or a stack whose bottom card is a queen.
      */
    def isKingNegative(constraint: IsKing, isSingle:Boolean) : Seq[Statement] = {
      if(isSingle){
        Java(
          s"""
             |movingCards = new Card(Card.QUEEN, Card.CLUBS);
             |""".stripMargin).statements()
      }else {
        Java(
          s"""
             |movingCards = new Stack();
             |movingCards.add(new Card(Card.QUEEN, Card.CLUBS));
             |""".stripMargin).statements()
      }
    }

    def isAceNegative(constraint: IsAce, isSingle:Boolean) : Seq[Statement] = {
      if(isSingle){
        Java(
          s"""
             |movingCards = new Card(Card.TWO, Card.CLUBS);
             |""".stripMargin).statements()
      }else {
        Java(
          s"""
             |movingCards = new Stack();
             |movingCards.add(new Card(Card.QUEEN, Card.CLUBS));
             |""".stripMargin).statements()
      }
    }

    /** Not sure what to place here... */
    def sameSuitNegative(constraint: SameSuit, isSingle:Boolean) : Seq[Statement] = {
      constraint.on.getName

      Java(
        s"""
           |movingCards = (new Card(Card.ACE, Card.SPADES));
           |""".stripMargin).statements()
    }

    def nextRankNegative(constraint: Constraint, isSingle:Boolean) :Seq[Statement] = {
      if (isSingle) {
        Java(
          s"""
             |movingCards = new Card(Card.FIVE, Card.CLUBS);
             |""".stripMargin).statements()
      } else {
        Java(
          s"""
             |movingCards = new Stack();
             |movingCards.add(new Card(Card.THREE, Card.CLUBS));
             |destination.add(new Card(Card.FIVE, Card.CLUBS));
             |""".stripMargin).statements()
      }
    }

    // TODO: Needs Work
    def notDescending(constraint: Descending) : Seq[Statement] = {
      Java(
        s"""
           |movingCards = notDescending(movingCards);
           |""".stripMargin).statements()
    }

    //For user-defined functions right now: prints name, guarantees fail case
    def showConstraint(constraint:Constraint) : Seq[Statement] = {
      Java(
        s"""
           |String user_defined_constraint = "$constraint";
           |userDefined = true;//Set to True because it's user defined
           |""".stripMargin).statements()
    }

    def getConstraintMethod(constraint:Constraint, isSingle:Boolean) : Seq[Statement] = {
      constraint match{
        case d:Descending =>
          notDescending(d)

        case a:IsAce=>
          isAceNegative(a, isSingle)

        case k:IsKing=>
          isKingNegative(k, isSingle)

        case e:IsEmpty=>
          isEmptyNegative(e, isSingle)

        case r:NextRank=>
          nextRankNegative(constraint, isSingle)

        case s:SameSuit=>
          sameSuitNegative(s, isSingle)

        case _=> showConstraint(constraint)
      }
    }

    def suitOf(s:Suit): String = {
      s match {
        case Clubs => "CLUBS"
        case Diamonds => "DIAMONDS"
        case Hearts => "HEARTS"
        case Spades => "SPADES"
      }
    }

    def rankOf(r:Rank): String = {
      r match {
        case Ace => "ACE"
        case Two => "TWO"
        case Three => "THREE"
        case Four => "FOUR"
        case Five => "FIVE"
        case Six => "SIX"
        case Seven => "SEVEN"
        case Eight => "EIGHT"
        case Nine => "NINE"
        case Ten => "TEN"
        case Jack => "JACK"
        case Queen => "QUEEN"
        case King => "KING"
      }
    }

    def sourceOf(move:Move): ContainerType = {
      move.source._1
    }

    def targetOf(move:Move): Option[ContainerType] = {
      if (move.target.isEmpty) {
        None
      } else {
        Some(move.target.get._1)
      }
    }

    def generateSetup(setup:Setup, move:Move): Seq[Statement] = {
      val source = sourceOf(move)
      val target = targetOf(move)

      var stmts = ""
      for (step <- setup.setup) {
        step match {
          case InitializeStep(target, card) =>
            val rank = rankOf(card.rank)
            val suit = suitOf(card.suit)
            stmts = stmts + s"""game.${target.name}.add(new Card(Card.$rank, Card.$suit));\n""".stripMargin

          case MovingCardStep(card) =>
            val rank = rankOf(card.rank)
            val suit = suitOf(card.suit)
            stmts = stmts + s"""Card movingCard = new Card(Card.$rank, Card.$suit);\n""".stripMargin

          case MovingCardsStep(cards) =>
            stmts += "Stack movingCards = new Stack();\n"
            for (card <- cards) {
              val rank = rankOf(card.rank)
              val suit = suitOf(card.suit)
              stmts = stmts + s"""movingCards.add(new Card(Card.$rank, Card.$suit));\n""".stripMargin
            }

          case RemoveStep(target) =>
            stmts = stmts + s"""game.${target.name}.removeAll();\n""".stripMargin
        }
      }
      Java(stmts).statements()
    }

    def apply(gen:CodeGeneratorRegistry[Expression]): CompilationUnit = {
      val pkgName = solitaire.name
      val name = solitaire.name.capitalize

      var methods:Seq[MethodDeclaration] = Seq.empty

      // go through SETUP not MOVE
      val matched = solitaire.customizedSetup
      var idx = 0

      for (setup <- solitaire.customizedSetup) {
        // must be a move
        idx = idx + 1
        val move = solitaire.moves.filter(m => sourceOf(m) == setup.source && targetOf(m) == setup.target)

        if (move.nonEmpty) {
          val m = move.head
          // generate a test case for this setup (based on the given move)...
          val source_customized_seq = generateSetup(setup, m)

          var isSingle = false
          val singleCard_logic = if(m.moveType.getClass.getSimpleName.replaceAll("[$]", "").equalsIgnoreCase("singlecard")){
            isSingle = true
            "1"
          }else{
            "movingCards.count()"
          }

          // HACK
          val dealDeck_logic = if (m.moveType.getClass.getSimpleName.equalsIgnoreCase("dealdeck")) {
            "game.tableau"
          } else {
            if (isSingle) {
              "movingCard, destination"
            } else {
              "movingCards, destination"
            }
          }


          // This represents the successful move. TODO: Must deal with moves that have NO TARGET (TBD)
          val parsedString = if (m.target.isDefined) {
              s"""
                    |@Test
                    |public void test${m.name}$idx () {
                    |  // Testing ${setup.getClass.getSimpleName}
                    |  String type = "${m.moveType.getClass.getSimpleName}";
                    |
                    |  // this is where test set-up must go.
                    |  ${source_customized_seq.mkString("\n")}
                    |
                    |  Stack source = game.${setup.sourceElement.name};
                    |  Stack destination = game.${setup.targetElement.get.name};
                    |  int ss = source.count();
                    |  int ds = destination.count();
                    |  int ms = $singleCard_logic;
                    |
                    |  ${m.name} move = new ${m.name}(source, $dealDeck_logic);
                    |
                    |  Assert.assertTrue(move.valid(game));               // Move is valid
                    |  Assert.assertTrue(move.doMove(game));              // Make move
                    |  Assert.assertEquals(destination.count(), ds+ms);   // Destination set
                    |}""".stripMargin
            } else {
              // TODO: Move with no target...
            "// TO BE REPLACED"
            }

            val method = Java(parsedString).methodDeclarations().head

            methods = methods :+ method
          }
      }
      val container = Java(s"""
               |package org.combinators.solitaire.${pkgName.toLowerCase};
               |import org.combinators.solitaire.${pkgName.toLowerCase}.model.*;
               |import ks.client.gamefactory.GameWindow;
               |import ks.common.model.*;
               |import ks.launcher.Main;
               |import org.junit.Assert;
               |import org.junit.Before;
               |import org.junit.Test;
               |
               |//Should hold falsified cases
               |public class ${name}TestCases {
               |$name game;
               |
               |private Stack getValidStack() {
               |   Stack movingCards = new Stack();
               |   for (int rank = Card.KING; rank >= Card.ACE; rank--) {
               |       movingCards.add(new Card(rank, Card.CLUBS));
               |   }
               |   return movingCards;
               |}
               |
               |private Stack notDescending(Stack stack){
               |        if (stack.empty() || stack.count() == 1) {
               |            stack.add(new Card(Card.KING, Card.CLUBS));
               |            stack.add(new Card(Card.ACE, Card.CLUBS));
               |            return stack;
               |        } else {
               |            for(int i=0;i<stack.count()-1; i++){
               |                if(stack.peek(i).getRank() != stack.peek(i+1).getRank()+1){
               |                    return stack;
               |                }
               |            }
               |            //end of loop, stack in descending order
               |            Card top = stack.get();
               |            stack.add(new Card(Card.KING, Card.CLUBS));
               |            stack.add(top);
               |            return stack;
               |        }
               |    }
               |
               |    @Before
               |    public void makeGame() {
               |        game = new $name();
               |        final GameWindow window = Main.generateWindow(game, Deck.OrderBySuit);
               |        window.setVisible(true);
               |        try{
               |          Thread.sleep(500);
               |        } catch (Exception e) {
               |          e.printStackTrace();
               |        }
               |    }
               |
               |  ${methods.mkString("\n")}
               |}""".stripMargin).compilationUnit()

      container
    }
    val semanticType: Type = constraints(constraints.generator) =>: classes("TestCases")
  }
}
