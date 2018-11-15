import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.generic
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared.{GameTemplate, SolitaireDomain}

/** Defines TEMPLATE's controllers and their behaviors.
  * Every controller requires definitions for three actions:
  *   - Click (no dragging, like clicking on a deck to deal more cards)
  *   - Press (click, drag, release)
  *   - Release (release after press)
  *
  * Either a rule must be associated with an action, or the action must be
  * explicity ignored. See FanRules in game.scala.
  */
trait controllers extends shared.Controller  with GameTemplate with shared.Moves with generic.JavaCodeIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire):
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> TEMPLATE Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)


    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())

    updated = createWinLogic(updated, s)

    // move these to shared area
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