package org.combinators.solitaire.napoleon

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

/**
  * Define Napoleon transformation/generation
  * Either a rule must be associated with an action, or the action must be
  * explicity ignored. See ArchwayRules in game.scala.
  */
trait controllers extends shared.Controller with shared.Moves with GameTemplate with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Napoleon Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler(pile))

    updated = updated
      .addCombinator (new IgnoreClickedHandler(wastePile))
      .addCombinator (new IgnoreReleasedHandler(wastePile))
      .addCombinator (new SingleCardMoveHandler(wastePile))

    updated = updated
      .addCombinator (new IgnoreClickedHandler(column))
      .addCombinator (new IgnorePressedHandler(column))   // scaffolding
      .addCombinator (new IgnoreReleasedHandler(column))  // scaffolding

    updated = updated
      .addCombinator (new IgnoreClickedHandler(deck))
      .addCombinator (new IgnoreReleasedHandler(deck))

    updated = updated
      .addCombinator(new DealToWasteHandlerLocal())

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
      * How to deal from the deck to the waste pile
      */
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


