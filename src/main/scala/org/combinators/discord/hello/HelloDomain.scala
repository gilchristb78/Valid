package org.combinators.discord.hello

import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.discord.domain._
import org.combinators.discord.shared.DiscordDomain
import org.combinators.discord.shared.SemanticTypes
import org.combinators.templating.twirl.Python
import pydiscord.shared.DiscordTemplate

/**
 * @param solitaire    Application domain object with details about solitaire variation.
 */
class HelloDomain(val d:Discord) extends DiscordDomain(d) with DiscordTemplate with SemanticTypes {


}
