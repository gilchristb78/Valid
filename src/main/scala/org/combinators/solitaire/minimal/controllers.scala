package org.combinators.solitaire.minimal

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.generic
import org.combinators.solitaire.domain.{Foundation, Solitaire}
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms with SemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Minimal Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)

    // these all have to do with GUI commands being ignored. Note we can envision an alternate
    // set of default behaviors to try to generate all possible moves to the Foundation,
    // should one exist.
    s.structure.foreach(ctPair => {
      updated = updated.addCombinator(new IgnoreClickedHandler(Constructor(ctPair._2.head.name)))

      ctPair._1 match {
        case Foundation => updated = updated.addCombinator (new IgnorePressedHandler(Constructor(ctPair._2.head.name)))
        case _ =>
      }
    })

    updated = createWinLogic(updated, s)

    updated = updated
      .addCombinator (new SingleCardMoveHandler(pile))
      .addCombinator (new IgnoreReleasedHandler(pile))

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
