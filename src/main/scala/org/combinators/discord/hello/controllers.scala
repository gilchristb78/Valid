package org.combinators.discord.hello

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.discord.domain._
import org.combinators.discord.shared.DiscordDomain
import org.combinators.discord.shared.SemanticTypes
import pydiscord.shared.DiscordTemplate

trait controllers extends DiscordTemplate  with SemanticTypes  {

    // dynamic combinators added as needed
    override def init[G <: DiscordDomain](gamma : ReflectedRepository[G], discord:Discord) :  ReflectedRepository[G] = {
      var updated = super.init(gamma, discord)
      println(">>> Hello Controller dynamic combinators.")

     // override as you need to..
      updated = updated.addCombinator(new Description(discord.description))
      updated = updated.addCombinator(new Prefix(discord.prefix))
      updated = updated.addCombinator(new OutputFile(discord.name))

      updated
    }
}
