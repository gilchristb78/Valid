package org.combinators.solitaire.bakersdozen

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.solitaire.domain._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java
trait controllers extends shared.Controller  with GameTemplate with shared.Moves with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Bakersdozen Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)


    updated = updated
      .addCombinator (new IgnoreClickedHandler(column))
      .addCombinator (new SingleCardMoveHandler(column))
    updated = updated
      .addCombinator (new IgnoreClickedHandler('AlexColumn))
      .addCombinator (new SingleCardMoveHandler('AlexColumn))


    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler(pile))



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


