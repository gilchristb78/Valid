package org.combinators.solitaire.freecell

// name clash
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._


trait controllers extends shared.Controller with shared.Moves with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> PileController dynamic combinators.")


    // not much to do, if no rules...
    if (s.getRules == null) {
      return updated
    }

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator (new IgnorePressedHandler('HomePile))
      .addCombinator (new IgnoreClickedHandler('HomePile))
      .addCombinator (new SingleCardMoveHandler('FreePile))
      .addCombinator (new IgnoreClickedHandler('FreePile))
      .addCombinator (new IgnoreClickedHandler('Column))

    // CASE STUDY: Add Automove logic at end of release handlers
    // this is done by manipulating the chosen combinator.
    updated
  }

  /**
    * When moving between columns, use the 'validColumn' method to confirm press sequence.
    */
 // @combinator object PC extends ColumnMoveHandler(column, Java("Column").simpleName(), Java(s"""validColumn""").simpleName())
}


