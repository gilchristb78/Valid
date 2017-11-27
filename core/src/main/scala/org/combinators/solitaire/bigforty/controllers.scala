package org.combinators.solitaire.bigforty
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import domain._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms {
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> BigForty Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // these all have to do with GUI commands being ignored
    updated = updated
      .addCombinator(new IgnoreClickedHandler(column))

      .addCombinator(new IgnoreClickedHandler(pile))
      .addCombinator(new IgnorePressedHandler(pile))

      .addCombinator(new IgnoreClickedHandler('WastePile))
      .addCombinator(new IgnoreReleasedHandler('WastePile))
      .addCombinator(new SingleCardMoveHandler('WastePile))   // ### DOCUMENT

    updated = updated
      .addCombinator(new IgnoreReleasedHandler(deck))
      .addCombinator(new IgnoreClickedHandler(deck))
      .addCombinator(new deckPress.DealToTableauHandlerLocal())   // ## TEMPLATE
      .addCombinator(new deckPress.ResetDeckLocal())


    updated

  }

  // deck can be flipped to wastepiile. Once empty, reset from wastepile

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
        Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.fieldWastePiles);
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
        Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.fieldWastePiles);
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

  // actually instantiate the deck pressed which was fused together from both deck1/deck2 pressed
  @combinator object ChainTogether extends deckPress.ChainTogether



}
