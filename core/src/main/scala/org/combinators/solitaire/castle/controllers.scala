package org.combinators.solitaire.castle

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain._
import domain.ui._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Castle Controller dynamic combinators.")

    // structural
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext) {
      val el = els_it.next()

      // Each of these controllers are expected in the game.
      if (el == "Pile") {
        updated = updated.
          addCombinator (new WidgetController(Symbol(el)))
      } else if (el == "Row") {
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

    updated = updated
      .addCombinator (new SingleCardMoveHandler(row))
      .addCombinator (new IgnoreClickedHandler(row))
      .addCombinator (new IgnoreClickedHandler(pile))
      .addCombinator (new IgnorePressedHandler('Pile))

//
//
//    // Potential moves clarify structure (by type not instance). FIX ME
//    // FIX ME FIX ME FIX ME
//    updated = updated
//      .addCombinator (new PotentialTypeConstructGen("Row", 'RowToRow))
//
//    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
//    updated = updated
//      .addCombinator (new ControllerNaming('Row, 'Row, "Castle"))
//      .addCombinator (new ControllerNaming('Pile, 'Pile, "Castle"))


    updated
  }


}


