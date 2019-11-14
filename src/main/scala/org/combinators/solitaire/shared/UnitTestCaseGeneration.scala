package org.combinators.solitaire.shared
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.shared
import _root_.java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import org.combinators.cls.types.{Constructor, Type}
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared.compilation._
import org.combinators.templating.twirl.Java
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared.cls.Synthesizer.complete


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

    def perturb(c:Constraint, ctr:Int, flipIndex:Int) : (Constraint,Int) = {
      c match {
          case andc:AndConstraint => {
            var newctr:Int = ctr
            var argc:Seq[Constraint] = Seq.empty
            andc.args.foreach(arg => {
              val rc = perturb(arg, newctr, flipIndex)
              argc = argc :+ rc._1
              newctr = rc._2
            })

            (AndConstraint(argc : _*), newctr)
          }

          case ifc:IfConstraint => {
            val fbIdx = perturb(ifc.falseBranch, ctr, flipIndex)
            val tbIdx = perturb(ifc.trueBranch, fbIdx._2, flipIndex)
            (IfConstraint(fbIdx._1, tbIdx._1), tbIdx._2)
          }

          case notc:NotConstraint => {
            val rc = perturb(notc.inner, ctr, flipIndex)
            (NotConstraint(rc._1), rc._2)
          }

          case orc:OrConstraint => {
            var newctr:Int = ctr
            var argc:Seq[Constraint] = Seq.empty
            orc.args.foreach(arg => {
              val rc = perturb(arg, newctr, flipIndex)
              argc = argc :+ rc._1
              newctr = rc._2
            })

            (OrConstraint(argc : _*), newctr)
          }

          case c:Constraint => {
            if (ctr == flipIndex) {
              (NotConstraint(c), ctr+1)
            } else {
              (c, ctr+1)
            }
          }
      }
    }

    def apply(gen:CodeGeneratorRegistry[Expression]): CompilationUnit = {
      val pkgName = solitaire.name;
      val name = solitaire.name.capitalize;

      var methods:Seq[MethodDeclaration] = Seq.empty
      for (m <- solitaire.moves) {
        val constraint:Constraint = m.constraints
        val total = max(constraint)
        val targets = (1 to total).toSeq // the range of constraints
        val falsifiedConstrains:Seq[Constraint] = targets.map(idx => perturb(constraint, 0, idx)._1)

        falsifiedConstrains.foreach(c => {
          val cc3: Option[Expression] = gen(c)
          println("CC3:" + m.name + "//:"+ cc3.mkString("\n"))
        })

        val sym = Constructor(m.name)
        val source_loc = if(m.source._1.name.equalsIgnoreCase("stockcontainer")) "deck" else m.source._1.name + "[0]"
        val dealDeck_logic = if(m.moveType.getClass.getSimpleName.equalsIgnoreCase("dealdeck")) "game.tableau" else "movingCards, destination"
        //Additional assertion dependent on if move is dealdeck or not
        val additional_assertion =
          if(m.moveType.getClass.getSimpleName.equalsIgnoreCase("dealdeck"))
          "ss - game.tableau.length, game.deck.count()"
        else
          "ds + ms, destination.count()"

        val method = Java(
          s"""
             |@Test
             |public void test${m.name} () {
             |String type  = "${m.moveType.getClass.getSimpleName}";
             |Stack movingCards = getValidStack();
             |
             |game.tableau[0].removeAll();
             |game.tableau[1].removeAll();
             |
             |Stack source = game.${source_loc}; //game.tableau[0] or deck
             |Stack destination = game.${m.target.head._1.name}[1]; //game.foundation[1] or game.tableau[1]
             |int ss = source.count();
             |int ds = destination.count();
             |int ms = movingCards.count();
             |
             |${m.name} move = new ${m.name}(source, ${dealDeck_logic});
             |
             |Assert.assertTrue(move.valid(game));
             |move.doMove(game);
             |Assert.assertEquals(${additional_assertion});
             |
             |}""".stripMargin).methodDeclarations().head

        methods = methods :+ method

        var testNum = 0
        falsifiedConstrains.foreach(c => {
          val cc3: Option[Expression] = gen(c)
          val testCase = Java(
              s"""
                 |@Test
                 |public void subtest${m.name + testNum} () {
                 |String type  = "${m.moveType.getClass.getSimpleName}";
                 |Stack movingCards = getValidStack();
                 |
                 |game.tableau[0].removeAll();
                 |game.tableau[1].removeAll();
                 |
                 |Stack source = game.${source_loc}; //game.tableau[0] or deck
                 |Stack destination = game.${m.target.head._1.name}[1]; //game.foundation[1] or game.tableau[1]
                 |int ss = source.count();
                 |int ds = destination.count();
                 |int ms = movingCards.count();
                 |
                 |${m.name} move = new ${m.name}(source, ${dealDeck_logic});
                 |Assert.assertTrue(${cc3.mkString("")});
                 |
                 |}""".stripMargin).methodDeclarations().head
          testNum = testNum + 1
          methods = methods :+ testCase
        })
      }

      val container = Java(s"""|package org.combinators.solitaire.${pkgName.toLowerCase};
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
               |${name} game;
               |
               |private Stack getValidStack() {
               |   Stack movingCards = new Stack();
               |   for (int rank = Card.KING; rank >= Card.ACE; rank--) {
               |       movingCards.add(new Card(rank, Card.CLUBS));
               |   }
               |   return movingCards;
               |}
               |
               | @Before
               |    public void makeGame() {
               |        game = new ${name}();
               |        final GameWindow window = Main.generateWindow(game, Deck.OrderBySuit);
               |        window.setVisible(true);
               |        try{
               |          Thread.sleep(500);
               |        }catch(Exception e){
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

      //val combi
}
