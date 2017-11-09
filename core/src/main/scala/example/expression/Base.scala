package example.expression

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import expression.DomainModel

trait Base {

  /**
    * To be overridden by sub-typed traits that are part of the dynamic constructions process.
    * @param gamma
    * @param e
    * @tparam G
    * @return
    */
  def init[G <: ExpressionDomain](gamma : ReflectedRepository[G], e:DomainModel) : ReflectedRepository[G] = gamma

  // Find way to maintain clean separation between Language (i.e., Java) and constraints (i.e., NextRank).

}

// This class exists so the 'def init' methods can be included in any trait that wishes
// to add dynamic traits to the repository. Otherwise nothing is included here (for now)
class ExpressionDomain(val domain:DomainModel) {
  // assumed access to expression
}

