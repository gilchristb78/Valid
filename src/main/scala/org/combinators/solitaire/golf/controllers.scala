package org.combinators.solitaire.golf

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.domain.{WinningLogic, _}
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java

trait controllers extends shared.Controller with GameTemplate with WinningLogic with shared.Moves with generic.JavaCodeIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire):
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> Golf Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    // Must determine how to handle clicks
    updated = updated
      .addCombinator(new IgnoreClickedHandler(column))
      .addCombinator(new SingleCardMoveHandler(column))

    updated = updated
      .addCombinator(new IgnoreClickedHandler('WastePile))
      .addCombinator(new IgnorePressedHandler('WastePile))

    updated = updated
      .addCombinator(new IgnoreReleasedHandler(deck))
      .addCombinator(new IgnoreClickedHandler(deck))

    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      //.addCombinator(new CombinedPileHandlerLocal())
      .addCombinator(new DealToWasteHandlerLocal())
      //.addCombinator(new deckPress.ResetDeckLocal())

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

  class DealToWasteHandlerLocal() {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = (widget, ignore) => {
      Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.waste);
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



