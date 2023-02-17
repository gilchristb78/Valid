package org.combinators.solitaire.freecell

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.types.Constructor
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

trait controllers extends shared.Controller with GameTemplate with shared.Moves with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> FreeCell controllers dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    // these all have to do with GUI commands being ignored. Note we can envision an alternate
    // set of default behaviors to try to generate all possible moves to the Foundation,
    // should one exist.

    // Must determine how to handle clicks
    updated = updated
      .addCombinator(new IgnoreClickedHandler('FreeCellPile))  // CRITICAL that name exactly matches Scala name of case object
      .addCombinator(new IgnoreClickedHandler(column))
      .addCombinator(new IgnoreClickedHandler(pile))

    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))  // FOUNDATION has no PRESS requirements
      .addCombinator(new SingleCardMoveHandler(pile))  // RELEASE on Foundation
      .addCombinator (new SingleCardMoveHandler('FreeCellPile))    // RELEASE on FreeCellPile

    // are multiple card moves automatically generated? WHY

    updated = createWinLogic(updated, s)

    // move these to shared area
    updated = updated
      .addCombinator (new DefineRootPackage(s))
      .addCombinator (new DefineNameOfTheGame(s))
      .addCombinator (new ProcessModel(s))
      .addCombinator (new ProcessView(s))
      .addCombinator (new ProcessControl(s))
      .addCombinator (new ProcessFields(s))

    // CASE STUDY: Add Automove logic at end of release handlers
    // this is done by manipulating the chosen combinator.
    updated
  }
}


