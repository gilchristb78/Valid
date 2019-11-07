package org.combinators.solitaire.simplesimon

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

/** Defines SimpleSimons's controllers and their behaviors.
  *
  * Every controller requires definitions for three actions:
  *   - Click (no dragging, like clicking on a deck to deal more cards)
  *   - Press (click, drag, release)
  *   - Release (release after press)
  *
  * Either a rule must be associated with an action, or the action must be
  * explicitly ignored.
  */

trait controllers extends shared.Controller with GameTemplate with WinningLogic  with shared.Moves with generic.JavaCodeIdioms {
  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Simplesimon Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler(pile))

    updated = updated
      .addCombinator (new IgnoreClickedHandler(column))
    //.addCombinator (new IgnoreClickedHandler(buildablePile))


    //determine if our variation needs the buildablePilePress controller
    //if(s.isInstanceOf[closedVariationPoints]){
//    var flip = 0
//    for( m <- s.moves) {
//      //TODO try utilizing movetype here?
//      if (m.name contains "FlipCard") {
//        flip = 1
//      }
//    }
//
//    if(flip == 1){
//      updated = updated
//        .addCombinator (new buildablePilePress.CP2())
//    }

    updated = updated
      .addCombinator (new IgnoreClickedHandler(deck))
      .addCombinator (new IgnoreReleasedHandler(deck))

    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())

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

//  /**
//    * Same as Klondike, Spider has two kinds of press moves (ones that act, and ones that lead to drags).
//    * While the automatic one is handled properly, it produces terminals that will be 'dragStart'. We need
//    * to chain together to form complete set.
//    */
//  object buildablePilePress {
//    val buildablePile1:Type = 'BuildablePile1 //Changed from :Constructor to :Type
//
//    class CP2() {
//      def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
//        (widget, ignore) =>
//
//          Java(s"""|BuildablePile srcPile = (BuildablePile) src.getModelElement();
//                   |
//                   |// Only apply if not empty AND if top card is face down
//                   |if (srcPile.count() != 0) {
//                   |  if (!srcPile.peek().isFaceUp()) {
//                   |    Move fm = new FlipCard(srcPile, srcPile);
//                   |    if (fm.doMove(theGame)) {
//                   |      theGame.pushMove(fm);
//                   |      c.repaint();
//                   |      return;
//                   |    }
//                   |  }
//                   |}""".stripMargin).statements()
//      }
//
//      val semanticType: Type =
//        drag(drag.variable, drag.ignore) =>: controller (buildablePile1, controller.pressed)
//    }
//
//    class ChainBuildablePileTogether extends ParameterizedStatementCombiner[SimpleName, SimpleName](
//      drag(drag.variable, drag.ignore) =>: controller(buildablePile1, controller.pressed),
//      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.dragStart),
//      drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.pressed))
//  }

//  @combinator object ChainBuildablePileTogether extends buildablePilePress.ChainBuildablePileTogether
  //TODO double check above when flip-press added
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

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)
  }

}


