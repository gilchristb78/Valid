package org.combinators.solitaire.bigforty
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.{Constructor, Type}
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import domain._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms {
  override def init[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> BigForty Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // these all have to do with GUI commands being ignored
    updated = updated
      .addCombinator(new IgnoreClickedHandler(buildablePile))
      .addCombinator(new IgnoreClickedHandler(pile))
      .addCombinator(new IgnoreClickedHandler('WastePile))
      .addCombinator(new IgnoreReleasedHandler('WastePile))

    updated = updated
      .addCombinator(new IgnoreReleasedHandler(deck))
      .addCombinator(new IgnoreClickedHandler(deck))


    updated

  }
}
