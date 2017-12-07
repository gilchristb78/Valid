package org.combinators.solitaire.shared

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import domain._

// Base trait for any trait using dynamic combinators. Assumes availability
// of solitaire domain object model passed in during init.
trait Base {

  /**
    * To be overridden by sub-typed traits that are part of the dynamic constructions process.
    */
  def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = gamma

  // Find way to maintain clean separation between Language (i.e., Java) and constraints (i.e., NextRank).

}

// This class exists so the 'def init' methods can be included in any trait that wishes
// to add dynamic traits to the repository. Otherwise nothing is included here (for now)
class SolitaireDomain(val solitaire:Solitaire) {
  // assumed access to solitaire. Or at least, make it available
}

