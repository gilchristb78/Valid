package org.combinators.solitaire.delta

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.solitaire.domain._
import org.combinators.solitaire.domain.WinningLogic
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java



trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](delta: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = super.init(delta, s)
    println(">>> Dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    updated = generateExtendedClasses(updated, s)


    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES. 

    updated = updated
      .addCombinator(new IgnoreClickedHandler(column))
      .addCombinator(new SingleCardMoveHandler(column))

    updated = updated
      .addCombinator(new IgnoreClickedHandler(pile))
      .addCombinator(new IgnorePressedHandler(pile))

    //.addCombinator(new SingleCardMoveHandler(pile))

    updated = updated
        .addCombinator(new IgnoreClickedHandler(deck))
        .addCombinator(new IgnoreReleasedHandler(deck))

    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())

    // winning logic inferred from domain
    updated = createWinLogic(updated, s)

    // Start with these from domain
    updated = updated
      .addCombinator(new DefineRootPackage(s))
      .addCombinator(new DefineNameOfTheGame(s))
      .addCombinator(new ProcessModel(s))
      .addCombinator(new ProcessView(s))
      .addCombinator(new ProcessControl(s))
      .addCombinator(new ProcessFields(s))

    updated
  }

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
  } //this is necessary for the Deck controller to be generated properly.

}


