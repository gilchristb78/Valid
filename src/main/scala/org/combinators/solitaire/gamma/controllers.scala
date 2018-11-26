package org.combinators.solitaire.gamma

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.domain.WinningLogic

trait controllers extends shared.Controller with shared.Moves with GameTemplate with WinningLogic with generic.JavaCodeIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> Dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    updated = generateExtendedClasses(updated, s)


    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES. 

    //ADD IGNORE HANDLERS HERE

    // winning logic inferred from domain
    updated = createWinLogic(updated, s)

    // Start with these from domain
    updated = updated
      .addCombinator(new DefineRootPackage(s))
      .addCombinator(new DefineNameOfTheGame(s))
      .addCombinator(new ProcessModel(s))
      .addCombinator(new ProcessView(s))
      .addCombinator(new ProcessControl(s))
      .addCombinator(new ProcessFields(s))

    updated
  }

}


