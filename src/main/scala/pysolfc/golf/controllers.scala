package pysolfc.golf

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.python.PythonSemanticTypes
import pysolfc.shared.GameTemplate

trait controllers extends GameTemplate  with PythonSemanticTypes  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println(">>> Golf Controller dynamic combinators.")

    updated = updated
      .addCombinator (new ProcessView(s))

    updated
  }


}
