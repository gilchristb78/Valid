package org.combinators.solitaire.freecell

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import domain._
import org.combinators.cls.types.Constructor
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._

import scala.collection.JavaConverters._

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
    for (cont:Container <- s.containers.asScala) {
      for (elt <- cont.types.asScala) {
        updated = updated.addCombinator(new IgnoreClickedHandler(Constructor(elt)))
      }
    }

    // can't initiate from the Foundation. This could be generic code to add for many variations
    var found:Container = s.getByType(SolitaireContainerTypes.Foundation)
    for (elt <- found.types.asScala) {
      updated = updated.addCombinator (new IgnorePressedHandler(Constructor(elt)))
    }

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


