package org.combinators.solitaire.archway

import _root_.java.util.UUID
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import scala.collection.mutable.ListBuffer

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
trait Controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Archway Controller dynamic combinators.")

    // structural
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext()) {
      val el = els_it.next()

      updated = updated.
        addCombinator (new WidgetController(Symbol(el)))
    }

    // Skip controllers if there are no rules defined.
    if (s.getRules == null) {
      return updated
    }

    // DOC: Not sure.
    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)

    // Create real class names for the Controllers. Becomes 'Symbol + "Controller"
    // i.e. 'AcesUpPile -> "AcesUpPileController"
    updated = updated
      .addCombinator (new ControllerNaming('AcesUpPile, 'AcesUpPile, "AcesUpPile"))
      .addCombinator (new ControllerNaming('KingsDownPile, 'KingsDownPile, "KingsDownPile"))
      .addCombinator (new ControllerNaming('Column, 'Column, "Column"))
      .addCombinator (new ControllerNaming('Pile, 'Pile, "Pile"))

    updated = updated

      // Cards are not moved from the Aces or Kings Foundation.
      .addCombinator (new IgnorePressedHandler('AcesUpPile, 'AcesUpPile))
      .addCombinator (new IgnoreClickedHandler('AcesUpPile, 'AcesUpPile))

      .addCombinator (new IgnorePressedHandler('KingsDownPile, 'KingsDownPile))
      .addCombinator (new IgnoreClickedHandler('KingsDownPile, 'KingsDownPile))


      // IgnoreReleasedHandler is necessary, because cards are only moved away
      // from the reserve, and never to it.
      .addCombinator (new IgnoreClickedHandler('Pile, 'Pile))
      .addCombinator (new IgnoreReleasedHandler('Pile, 'Pile))
      .addCombinator (new SingleCardMoveHandler("Pile", 'Pile, 'Pile))

      // Cards can be dragged to and from the Tableau.
      .addCombinator (new IgnoreClickedHandler('Column, 'Column))
      .addCombinator (new SingleCardMoveHandler("Column", 'Column, 'Column))

    updated = updated
      .addCombinator (new PotentialTypeConstructGen("Column", 'ColumnToAcesUpPile))
      .addCombinator (new PotentialTypeConstructGen("Column", 'ColumnToKingsDownPile))
      .addCombinator (new PotentialTypeConstructGen("Pile", 'PileToAcesUpPile))
      .addCombinator (new PotentialTypeConstructGen("Pile", 'PileToKingsDownPile))
      .addCombinator (new PotentialTypeConstructGen("Pile", 'PileToColumn))



    updated
  }
}


