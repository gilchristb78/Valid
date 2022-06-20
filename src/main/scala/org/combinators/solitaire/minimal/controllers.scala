package org.combinators.solitaire.minimal

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java

// for now a bit ugly. This determines primary movement to be single cards
trait singleCardMovers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms with SemanticTypes {
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = super.init(gamma, s)

    updated = updated.addCombinator(new SingleCardMoveHandler(column))

    updated
  }
}

// this is ambiguous on single/multiple cards
trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms with SemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Minimal Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)
    updated = createWinLogic(updated, s)

    updated = updated
      .addCombinator (new IgnoreClickedHandler(pile))        // can't move from foundation
      .addCombinator (new IgnorePressedHandler(pile))        // can't initiate move from foundation
      .addCombinator (new IgnoreClickedHandler(column))
      .addCombinator (new IgnoreClickedHandler(deck))
      .addCombinator (new IgnoreReleasedHandler(deck))

    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())          // how to deal cards.
      //.addCombinator(new SingleCardMoveHandler(column))      // enable single cards to be moved from columns
      // not needed if Multiple cards are being moved? This is distinctly odd, but at least I can understand it now

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
