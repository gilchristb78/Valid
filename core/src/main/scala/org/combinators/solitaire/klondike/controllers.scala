package org.combinators.solitaire.klondike

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
import domain.ui._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms with SemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Klondike Controller dynamic combinators.")

//    // structural
//    val ui = new UserInterface(s)
//
//    val els_it = ui.controllers
//    while (els_it.hasNext) {
//      val el = els_it.next()
//
//      // Each of these controllers are expected in the game.
//      updated = updated
//          .addCombinator (new WidgetController(Symbol(el)))
//          .addCombinator (new ControllerNaming(Symbol(el)))
//    }
//
//    // not much to do, if no rules...
//    if (s.getRules == null) {
//      return updated
//    }

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // these all have to do with GUI commands being ignored
    updated = updated
      .addCombinator (new IgnoreClickedHandler('BuildablePile))
      .addCombinator (new IgnoreClickedHandler('Pile))
      .addCombinator (new IgnoreClickedHandler('WastePile))
      .addCombinator (new IgnoreReleasedHandler('WastePile))

    updated = updated
      .addCombinator (new IgnoreReleasedHandler(deck))
      .addCombinator (new IgnoreClickedHandler(deck))

    // these clarify the allowed moves
    updated = updated
      .addCombinator (new DealToTableauHandlerLocal())
      .addCombinator (new ResetDeckLocal())
      .addCombinator (new SingleCardMoveHandler('Pile))
      .addCombinator (new SingleCardMoveHandler('WastePile))


    updated
  }

  /**
   * Specify filtering method 'validColumn' in the base class to use to pre-filter mouse press.
   * Note: This introduces 'Stage1 as a means to combine the two press events in sequence.
   */
  val name = Java(s"""validColumn""").simpleName()
  @combinator object PC extends ColumnMoveHandler('Stage1, name)
  // controller('Stage1, controller.pressed)

  // MAKE NOTE: TO FIX. HACK. PERHAPS CAN CALL DIRECTLY..

  /** This is one way to combine things... */
  @combinator object Combine {
    def apply(head: (SimpleName, SimpleName) => Seq[Statement]): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>

        val seq:Seq[Statement] = head.apply(widgetVariableName, ignoreWidgetVariableName)
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
                 |}
                 |${seq.mkString("\n")}""".stripMargin).statements()
    }

    val semanticType: Type =
      (drag(drag.variable, drag.ignore) =>: controller ('Stage1, controller.pressed)) =>:
        (drag(drag.variable, drag.ignore) =>: controller(buildablePile, controller.pressed))
  }

  /**
    * When dealing card(s) from the stock to all elements in Tableau
    * If deck is empty, then reset.
    * NOTE: How to make this more compositional?
    */
  class DealToTableauHandlerLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.fieldWastePiles);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: 'Deck1
  }

  class ResetDeckLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.fieldWastePiles);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: 'Deck2
  }

//  /**
//    * Statically knit together these two combinators to bring in the desired behavior
//    */
//  @combinator object ChainTogether extends StatementCombiner('Deck1, 'Deck2,
//    controller(deck, controller.pressed))
}



