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
import org.combinators.solitaire.domain._
import org.combinators.solitaire.domain.WinningLogic

trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms with SemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Klondike Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    // these all have to do with GUI commands being ignored. Note we can envision an alternate
    // set of default behaviors to try to generate all possible moves to the Foundation,
    // should one exist.
    s.structure.foreach(ctPair => {
      updated = updated.addCombinator(new IgnoreClickedHandler(Constructor(ctPair._2.head.name)))

      // In Klondike, it is never possible to release on the Deck or the wastepiles.
      // can't release on the Deck.
      // can't initiate from the Foundation. This could be generic code to add for many variations
      ctPair._1 match {
        case Waste => updated = updated.addCombinator(new IgnoreClickedHandler(Constructor(ctPair._2.head.name)))
        case StockContainer => updated = updated.addCombinator(new IgnoreReleasedHandler(Constructor(ctPair._2.head.name)))
        case Foundation => updated = updated.addCombinator(new IgnorePressedHandler(Constructor(ctPair._2.head.name)))
        case _ =>
      }
    })

    // these clarify the allowed moves
    updated = updated
      .addCombinator (new deckPress.DealToTableauHandlerLocal())
      .addCombinator (new SingleCardMoveHandler(wastePile))
      .addCombinator (new SingleCardMoveHandler(fanPile))    // Variation
      .addCombinator (new buildablePilePress.CP2())

    // TODO: FIX WITH PROPER MODELING
    // Some variations allow you to reset deck, others don't; note if numRedeals is a positive number, then
    // we can deal with that dynamically via state.
//    if (s.asInstanceOf[klondike.KlondikeDomain].numRedeals() == klondike.VariationPoints.NEVER_REDEAL) {
//      updated = updated.addCombinator (new deckPress.SkipResetDeckLocal())
//    } else {
      updated = updated.addCombinator (new deckPress.ResetDeckLocal())
//    }

    updated = createWinLogic(updated, s)

    // needed for DealByThree variation. Would love to be able to separate these out better.

    //    @combinator object MakeFanPile extends ExtendModel("Column", "FanPile", 'FanPileClass)
    //    @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
    //    @combinator object MakeWastePileView extends ExtendView("View", "WastePileView", "WastePile", 'WastePileViewClass)

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

    /** When deck is empty but variation has decided not able to reset deck. */
    // This should be generated from one of the rules.
    class SkipResetDeckLocal() {
      def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
        Java(s"""|{/* No reset deck available */}""".stripMargin).statements()
      }

      val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck2, controller.pressed)
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

//  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
//  @combinator object MakeWastePileView extends ExtendView("View", "WastePileView", "WastePile", 'WastePileViewClass)

}



