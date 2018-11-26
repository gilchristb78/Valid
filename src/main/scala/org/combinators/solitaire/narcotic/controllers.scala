package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.domain.WinningLogic

trait controllers extends shared.Controller with GameTemplate with WinningLogic with shared.Moves with generic.JavaCodeIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](narcotic: ReflectedRepository[G], s: Solitaire):
  ReflectedRepository[G] = {
    var updated = super.init(narcotic, s)
    println(">>> Narcotic Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    // Must determine how to handle clicks
    updated = updated
      .addCombinator(new IgnoreClickedHandler(pile))

    updated = updated
      .addCombinator(new IgnoreReleasedHandler(deck))
      .addCombinator(new IgnoreClickedHandler(deck))

    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator(new CombinedPileHandlerLocal())
      .addCombinator(new deckPress.DealToTableauHandlerLocal())
      .addCombinator(new deckPress.ResetDeckLocal())

    updated = createWinLogic(updated, s)

    // move these to shared area
    updated = updated
      .addCombinator (new DefineRootPackage(s))
      .addCombinator (new DefineNameOfTheGame(s))
      .addCombinator (new ProcessModel(s))
      .addCombinator (new ProcessView(s))
      .addCombinator (new ProcessControl(s))
      .addCombinator (new ProcessFields(s))

    updated
  }

  object deckPress {

    val deck1: Type = 'Deck1
    val deck2: Type = 'Deck2

    /**
      * When dealing card(s) from the stock to all elements in Tableau
      * If deck is empty, then reset.
      * NOTE: How to make this more compositional?
      */
    class DealToTableauHandlerLocal() {
      def apply(): (SimpleName, SimpleName) => Seq[Statement] = (widget, ignore) => {
        Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.tableau);
           |if (m.doMove(theGame)) {
           |   theGame.pushMove(m);
           |   // have solitaire game refresh widgets that were affected
           |   theGame.refreshWidgets();
           |   return;
           |}}""".stripMargin
        ).statements()
      }

      val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck1, controller.pressed)
    }


    class ResetDeckLocal() {
      def apply(): (SimpleName, SimpleName) => Seq[Statement] = (widget, ignore) => {
        Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.tableau);
           |if (m.doMove(theGame)) {
           |   theGame.pushMove(m);
           |   // have solitaire game refresh widgets that were affected
           |   theGame.refreshWidgets();
           |   return;
           |}}""".stripMargin
        ).statements()
      }

      val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck2, controller.pressed)
    }


    /** Parameterize with the (widget,ignore) pair. */
    class ChainTogether extends ParameterizedStatementCombiner[SimpleName, SimpleName] (
      drag(drag.variable, drag.ignore) =>: controller(deck1, controller.pressed),
      drag(drag.variable, drag.ignore) =>: controller(deck2, controller.pressed),
      drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed))


  }

  @combinator object ChainTogether extends deckPress.ChainTogether

  /**
    * Non-compositional combination of two logics for Press in Narcotic.
    * Chain these together since both active on the press.
    */
  class CombinedPileHandlerLocal {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
                 |Pile srcPile = (Pile) src.getModelElement();
                 |
                 |// Return in the case that the pile clicked on is empty
                 |if (srcPile.count() == 0) {
                 |  return;
                 |}
                 |// Deal with situation when all are the same.
                 |Move rm = new RemoveAllCards(theGame.tableau);
                 |if (rm.doMove(theGame)) {
                 |   theGame.pushMove(rm);
                 |   c.repaint();
                 |   return;
                 |}
                 |$widgetVariableName = src.getCardViewForTopCard(me);
                 |if ($widgetVariableName == null) {
                 |  return;
                 |}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(pile, controller.pressed)
  }
}



