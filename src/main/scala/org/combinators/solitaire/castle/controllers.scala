package org.combinators.solitaire.castle

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.generic
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Castle Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    updated = updated
      .addCombinator (new IgnoreClickedHandler(row))
      .addCombinator (new IgnoreClickedHandler(pile))
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreReleasedHandler(deck))


    updated = createWinLogic(updated, s)

    // move these to shared area
    updated = updated
      .addCombinator (new DefineRootPackage(s))
      .addCombinator (new DefineNameOfTheGame(s))
      .addCombinator (new ProcessView(s))
      .addCombinator (new ProcessControl(s))
      .addCombinator (new ProcessFields(s))

    updated
  }
}


