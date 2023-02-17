package org.combinators.discord.shared

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.discord.domain.Discord
import org.combinators.solitaire.domain.Solitaire

// Base trait for any trait using dynamic combinators. Assumes availability
// of solitaire domain object model passed in during init.
trait Base {

  /**
    * To be overridden by sub-typed traits that are part of the dynamic constructions process.
    */
  def init[G <: DiscordDomain](gamma: ReflectedRepository[G], discord: Discord): ReflectedRepository[G] = gamma
}

// This class exists so the 'def init' methods can be included in any trait that wishes
// to add dynamic traits to the repository. Otherwise nothing is included here (for now)
class DiscordDomain(val discord:Discord) {
  // assumed access to solitaire. Or at least, make it available
}


