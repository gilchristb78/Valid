package org.combinators.solitaire.narcotic

// name clash
import com.github.javaparser.ast.`type`.{Type => JType}

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import org.combinators.generic
import domain._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Narcotic Controller dynamic combinators.")

    // handle automoves

//
//    // structural
//    val ui = new UserInterface(s)
//
//    val els_it = ui.controllers
//    while (els_it.hasNext) {
//      val el = els_it.next()
//
//      // Each of these controllers are expected in the game.
//      updated = updated.
//          addCombinator (new WidgetController(Symbol(el)))
//
//    }
//
//    // not much to do, if no rules...
//    if (s.getRules == null) {
//      return updated
//    }

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // Must determine how to handle clicks
    updated = updated
      .addCombinator (new IgnoreClickedHandler(pile))

    updated = updated
      .addCombinator (new IgnoreReleasedHandler(deck))
      .addCombinator (new IgnoreClickedHandler(deck))


    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator (new CombinedPileHandlerLocal())
      .addCombinator (new DealToTableauHandlerLocal())
      .addCombinator (new ResetDeckLocal())

    // Potential moves clarify structure (by type not instance). FIX ME
    // FIX ME FIX ME FIX ME
    //    updated = updated
    //      .addCombinator (new PotentialSingleCardMove("Pile", 'PileToPile))

    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
//    updated = updated
//      .addCombinator (new ControllerNaming('Pile))

    // CASE STUDY: Add Automove logic at end of release handlers

    updated
  }

  /**
    * When dealing card(s) from the stock to all elements in Tableau
    * If deck is empty, then reset.
    * NOTE: How to make this more compositional?
    */
  class DealToTableauHandlerLocal() {
    def apply():(SimpleName, SimpleName) => Seq[Statement] = (widget,ignore) =>{
      Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.fieldPiles);
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
      Java(s"""|{Move m = new ResetDeck(theGame.deck, theGame.fieldPiles);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were affected
               |   theGame.refreshWidgets();
               |   return;
               |}}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: 'Deck2
  }


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
                 |Move rm = new RemoveAllCards(theGame.fieldPiles);
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


