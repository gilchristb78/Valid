package org.combinators.solitaire.shared
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.shared
import _root_.java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
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
    def apply(): CompilationUnit = {
      val pkgName = solitaire.name;
      val name = solitaire.name.capitalize;

      var methods:Seq[MethodDeclaration] = Seq.empty
      for (m <- solitaire.moves) {
        val sym = Constructor(m.name)
        val source_loc = if(m.source._1.name.equalsIgnoreCase("stockcontainer")) "deck" else m.source._1.name + "[0]"
        val dealDeck_logic = if(m.moveType.getClass.getSimpleName.equalsIgnoreCase("dealdeck")) "game.tableau" else "movingStack, dest"
        //Additional assertion dependent on if move is dealdeck or not
        val additional_assertion =
          if(m.moveType.getClass.getSimpleName.equalsIgnoreCase("dealdeck"))
          "ss - game.tableau.length, game.deck.count()"
        else
          "ds + ms, dest.count()"

        val method = Java(
          s"""
             |@Test
             |public void test${m.name} () {
             |String type  = "${m.moveType.getClass.getSimpleName}";
             |Stack movingStack = getValidStack();
             |
             |game.tableau[0].removeAll();
             |game.tableau[1].removeAll();
             |
             |Stack source = game.${source_loc}; //game.tableau[0] or deck
             |Stack dest = game.${m.target.head._1.name}[1]; //game.foundation[1] or game.tableau[1]
             |int ss = source.count();
             |int ds = dest.count();
             |int ms = movingStack.count();
             |
             |${m.name} move = new ${m.name}(source, ${dealDeck_logic});
             |
             |Assert.assertTrue(move.valid(game));
             |move.doMove(game);
             |Assert.assertEquals(${additional_assertion});
             |
             |}""".stripMargin).methodDeclarations().head

        methods = methods :+ method
      }

      val container = Java(s"""|package org.combinators.solitaire.${pkgName.toLowerCase};
               |import org.combinators.solitaire.${pkgName.toLowerCase}.model.*;
               |import ks.client.gamefactory.GameWindow;
               |import ks.common.model.*;
               |import ks.launcher.Main;
               |import org.junit.Assert;
               |import org.junit.Before;
               |import org.junit.Test;
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
    val semanticType: Type = classes("TestCases")
  }

      //val combi
}
