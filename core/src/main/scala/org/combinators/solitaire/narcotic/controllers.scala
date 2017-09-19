package org.combinators.solitaire.narcotic
import com.github.javaparser.ast.CompilationUnit

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
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.generic
import _root_.java.util.UUID
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {


  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Narcotic Controller dynamic combinators.")

    // structural
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext()) {
      val el = els_it.next()

      // Each of these controllers are expected in the game.
      if (el == "Deck") {
        updated = updated.    // HACK. Why special for Deck???
          addCombinator (new DeckController(Symbol(el)))
      } else if (el == "Pile") {
        updated = updated.
          addCombinator (new WidgetController(Symbol(el)))
      }
    }

    // not much to do, if no rules...
    if (s.getRules == null) {
      return updated
    }

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // Must determine how to handle clicks
    updated = updated
      .addCombinator (new IgnoreClickedHandler('Pile, 'Pile))


    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      //       .addCombinator (new SingleCardMoveHandlerLocal())
      .addCombinator (new CombinedPileHandlerLocal())
      //       .addCombinator (new RemoveAllCardsLocal('Pile, 'Pile))
      .addCombinator (new DealToTableauHandlerLocal())
      .addCombinator (new ResetDeckLocal())
    //       .addCombinator (new TryRemoveCardHandlerLocal('Pile, 'Pile))

    // Potential moves clarify structure (by type not instance). FIX ME
    // FIX ME FIX ME FIX ME
    updated = updated
      .addCombinator (new PotentialTypeConstructGen("Pile", 'PileToPile))

    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
    updated = updated
      .addCombinator (new ControllerNaming('Pile, 'Pile, "Narcotic"))

    // CASE STUDY: Add Automove logic at end of release handlers

    updated
  }


  /**
    * When dealing card(s) from the stock to all elements in Tableau
    * If deck is empty, then reset.
    * NOTE: How to make this more compositional?
    */
  class DealToTableauHandlerLocal() {
    def apply():Seq[Statement] = {
      Java(s"""|m = new DealDeck(theGame.deck, theGame.fieldPiles);
               |if (m.doMove(theGame)) {
               		 |   theGame.pushMove(m);
               		 |   // have solitaire game refresh widgets that were
               		 |   // affected
               		 |   theGame.refreshWidgets();
               		 |   return;
               |}""".stripMargin).statements()
    }

    val semanticType: Type = 'Deck1
  }

  class ResetDeckLocal() {
    def apply():Seq[Statement] = {
      Java(s"""|m = new ResetDeck(theGame.deck, theGame.fieldPiles);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |   // have solitaire game refresh widgets that were
               |   // affected
               |   theGame.refreshWidgets();
               |   return;
               |}""".stripMargin).statements()
    }

    val semanticType: Type = 'Deck2
  }

  @combinator object ChainTogether extends StatementCombiner('Deck1, 'Deck2,
    'Deck ('Pressed) :&: 'NonEmptySeq)


  class TryRemoveCardHandlerLocal(widgetType:Symbol, source:Symbol) {
    def apply():Seq[Statement] = {
      Java(s"""|Pile srcPile = (Pile) src.getModelElement();
               	       |Move m = new RemoveSingleCard(srcPile);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}
               	       |}""".stripMargin).statements()
    }

    val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
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

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile ('Pile, 'Pressed) :&: 'NonEmptySeq
  }

  /**
    * When a single card is being removed from the top card of a pile.
    * Chain these together since both active on the press.
    */
  class SingleCardMoveHandlerLocal {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
                 |Pile srcPile = (Pile) src.getModelElement();
                 |
                 |// Return in the case that the pile clicked on is empty
                 |if (srcPile.count() == 0) {
                 |  return;
                 |}
                 |$widgetVariableName = src.getCardViewForTopCard(me);
                 |if ($widgetVariableName == null) {
                 |  return;
                 |}""".stripMargin).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'ChainHead
  }

  // pull these two statement blocks together. I can't get this to
  // work. The idea is to somehow chain together the two Seq[Statement]
  // generated by SingleCardMoveHandlerLocal and RemoveAllCardsLocal.
  // but I can't seem to get the types right.
  @combinator object CombineChain {
    def apply(head: (SimpleName, SimpleName) => Seq[Statement],
              tail: (SimpleName, SimpleName) => Seq[Statement]) :
    (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) => Seq.empty
      // Do somethng with head and tail (like head ++ tail, but that doesn't work
    }

    val semanticType: Type =
      ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'ChainHead) =>:
        ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'ChainTail) =>:
        ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Pile ('Pile, 'Pressed) :&: 'NonEmptySeq)
  }

  @combinator object RemoveAllCardsLocal {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|Move m = new RemoveFourCards();
                 |if (m.doMove(theGame)) {
                 |   theGame.pushMove(m);
                 	       |   $ignoreWidgetVariableName = true;
                 |}
                 |}""".stripMargin).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'ChainTail
  }

}


