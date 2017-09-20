package org.combinators.solitaire.klondike

import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import org.combinators.generic
import domain._
import domain.ui._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Klondike Controller dynamic combinators.")

    // structural
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext) {
      val el = els_it.next()

      // Each of these controllers are expected in the game.
      if (el == "Deck") {
        updated = updated.    // HACK. Why special for Deck???
          addCombinator (new DeckController(Symbol(el)))
      } else if (el == "Column") {
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


    updated
  }


}


