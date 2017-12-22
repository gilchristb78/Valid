package org.combinators.solitaire.klondike

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.{Constructor, Type}
import org.combinators.templating.twirl.Java
import org.combinators.generic
import domain._

trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms with SemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Klondike Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // these all have to do with GUI commands being ignored
    updated = updated
      .addCombinator (new IgnoreClickedHandler(buildablePile))
      .addCombinator (new IgnoreClickedHandler(pile))
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler('WastePile))
      .addCombinator (new IgnoreReleasedHandler('WastePile))

    updated = updated
      .addCombinator (new IgnoreReleasedHandler(deck))
      .addCombinator (new IgnoreClickedHandler(deck))

    // these clarify the allowed moves
    updated = updated
      .addCombinator (new deckPress.DealToTableauHandlerLocal())
      .addCombinator (new deckPress.ResetDeckLocal())
      .addCombinator (new SingleCardMoveHandler('WastePile))
      .addCombinator (new buildablePilePress.CP2())

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

  /**
    * Recognize that Klondike has two kinds of press moves (ones that act, and ones that lead to drags).
    * While the automatic one is handled properly, it produces terminals that will be 'dragStart'. We need
    * to chain together to form complete set.
    */
  object buildablePilePress {
    val buildablePile1:Constructor = 'BuildablePile1

    class CP2() {
     def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widget, ignore) =>

        Java(s"""|BuildablePile srcPile = (BuildablePile) src.getModelElement();
                 |
                 |// Only apply if not empty AND if top card is face down
                 |if (srcPile.count() != 0) {
                 |  if (!srcPile.peek().isFaceUp()) {
                 |    Move fm = new FlipCard(srcPile, srcPile);
                 |    if (fm.doMove(theGame)) {
                 |      theGame.pushMove(fm);
                 |      c.repaint();
                 |      return;
                 |    }
                 |  }
                 |}""".stripMargin).statements()
    }

    val semanticType: Type =
      drag(drag.variable, drag.ignore) =>: controller (buildablePile1, controller.pressed)
    }

    class ChainBuildablePileTogether extends ParameterizedStatementCombiner[SimpleName, SimpleName](
      drag(drag.variable, drag.ignore) =>: controller(buildablePile1, controller.pressed),
      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.dragStart),
      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.pressed))
  }

  @combinator object ChainBuildablePileTogether extends buildablePilePress.ChainBuildablePileTogether

  object deckPress {

    val deck1:Constructor = 'Deck1
    val deck2:Constructor = 'Deck2

  /** When dealing card(s) from the stock to all elements in Tableau. */
  // This should be generated from one of the rules.
  class DealToTableauHandlerLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.waste);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck1, controller.pressed)
  }

  /** When deck is empty and must be reset from waste pile. */
  // This should be generated from one of the rules.
  class ResetDeckLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.waste);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck2, controller.pressed)
  }

  // in face, once the earlier two are properly generated, then we don't need to chain
  // together, but we can do all at once.
  class ChainTogether extends ParameterizedStatementCombiner[SimpleName, SimpleName](
    drag(drag.variable, drag.ignore) =>: controller(deck1, controller.pressed),
    drag(drag.variable, drag.ignore) =>: controller(deck2, controller.pressed),
    drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed))
  }

  @combinator object ChainTogether extends deckPress.ChainTogether


}



