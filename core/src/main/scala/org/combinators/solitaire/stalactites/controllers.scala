package org.combinators.solitaire.stalactites

// name clash
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import domain._
import domain.ui._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._


trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Dynamic combinators for stalactites.")


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
//    updated = updated
//      .addCombinator (new IgnorePressedHandler('HomePile, 'HomePile))
//      .addCombinator (new IgnoreClickedHandler('HomePile, 'HomePile))
//      .addCombinator (new SingleCardMoveHandler("FreePile", 'FreePile, 'FreePile))
//      .addCombinator (new IgnoreClickedHandler('FreePile, 'FreePile))
//      .addCombinator (new IgnoreClickedHandler('Column, 'Column))
//
//    // Potential moves clarify structure (by type not instance). FIX ME
//    // FIX ME FIX ME FIX ME
//    updated = updated
//      .addCombinator (new PotentialTypeConstructGen("Column", 'PlaceColumn))
//      .addCombinator (new PotentialTypeConstructGen("Column", 'MoveColumn))
//      .addCombinator (new PotentialTypeConstructGen("Column", 'BuildColumn))
//
    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
    updated = updated
      .addCombinator (new ControllerNaming('ReservePile, 'ReservePile, "ReservePile"))
      .addCombinator (new ControllerNaming('Pile, 'Pile, "Pile"))
      .addCombinator (new ControllerNaming('Column, 'Column, "FreeCell"))

    // Go through and assign GUI interactions for each of the known moves. Clean these up...
    val ui = new UserInterface(s)
    val els_it = ui.controllers
    while (els_it.hasNext) {
      val el = els_it.next()

      // generic widget controller with auto moves available since we
      // have that provided by our variation (see extra methods)
      print ("   ** " + el + ":WidgetController")
      updated.addCombinator(new WidgetController(Symbol(el)))

    }

    // CASE STUDY: Add Automove logic at end of release handlers
    // this is done by manipulating the chosen combinator.
    updated
  }
}


