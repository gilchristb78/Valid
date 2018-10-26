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
    s.structure.foreach(ctPair => {
      updated = updated.addCombinator(new IgnoreClickedHandler(Constructor(ctPair._2.head.name)))

      ctPair._1 match {
        case Foundation => updated = updated.addCombinator (new IgnorePressedHandler(Constructor(ctPair._2.head.name)))
        case _ =>
      }
    })


    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
//      .addCombinator (new IgnorePressedHandler('HomePile))
//      .addCombinator (new IgnoreClickedHandler('HomePile))
      .addCombinator (new SingleCardMoveHandler('FreePile))
//      .addCombinator (new IgnoreClickedHandler('FreePile))
//      .addCombinator (new IgnoreClickedHandler('Column))

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

  /**
    * When moving between columns, use the 'validColumn' method to confirm press sequence.
    */
 // @combinator object PC extends ColumnMoveHandler(column, Java("Column").simpleName(), Java(s"""validColumn""").simpleName())
}


