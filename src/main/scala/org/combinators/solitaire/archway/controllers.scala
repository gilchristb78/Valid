package org.combinators.solitaire.archway

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

/** Defines Archway's controllers and their behaviors.
  *
  * Every controller requires definitions for three actions:
  *   - Click (no dragging, like clicking on a deck to deal more cards)
  *   - Press (click, drag, release)
  *   - Release (release after press)
  *
  * Either a rule must be associated with an action, or the action must be
  * explicity ignored. See ArchwayRules in game.scala.
  */
trait controllers extends shared.Controller with shared.Moves with GameTemplate with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Archway Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    updated = updated

      // Cards are not moved from the Aces or Kings Foundation.
      .addCombinator (new IgnorePressedHandler('AcesUpPile))
      .addCombinator (new IgnoreClickedHandler('AcesUpPile))

      .addCombinator (new IgnorePressedHandler('KingsDownPile))
      .addCombinator (new IgnoreClickedHandler('KingsDownPile))


      // IgnoreReleasedHandler is necessary, because cards are only moved away
      // from the reserve, and never to it.
      .addCombinator (new IgnoreClickedHandler(pile))
      .addCombinator (new IgnoreReleasedHandler(pile))
      .addCombinator (new SingleCardMoveHandler(pile))

      // Cards can be dragged to and from the Tableau.
      .addCombinator (new IgnoreClickedHandler(column))
      .addCombinator (new SingleCardMoveHandler(column))


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
}


