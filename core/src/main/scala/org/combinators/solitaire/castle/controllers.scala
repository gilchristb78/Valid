package org.combinators.solitaire.castle

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import domain._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

trait controllers extends shared.Controller with shared.Moves with WinningLogic with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Castle Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    updated = updated
      .addCombinator (new IgnoreClickedHandler(row))
      .addCombinator (new IgnoreClickedHandler(pile))
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreReleasedHandler(deck))


    updated = createWinLogic(updated, s)
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


