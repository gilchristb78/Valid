package org.combinators.discord

import org.combinators.discord.domain.Discord

package object hello {
  val hello:Discord = {
    Discord(name = "HelloWorld",
      description = "My First Bot",
      prefix = "?"
    )
  }
}
