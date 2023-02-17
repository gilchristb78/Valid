package pydiscord.shared

import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.discord.domain._
import org.combinators.discord.shared.{DiscordDomain, SemanticTypes}
import org.combinators.templating.twirl.Python

import java.nio.file.Paths
import org.combinators.discord.shared._
import org.combinators.templating.persistable.PythonWithPath

trait DiscordTemplate extends Base with SemanticTypes {

  /**
    * Opportunity to customize based on solitaire domain object.
    */
  override def init[G <: DiscordDomain](gamma : ReflectedRepository[G], discord:Discord) : ReflectedRepository[G] = {
    var updated = gamma

    updated = updated
      .addCombinator (new MakeMain(discord))
    updated
  }

  // instantiate THIS
  class Description(str:String) {
    def apply() : String = {
      str
    }

    val semanticType:Type = bot(bot.description)
  }

  // instantiate THIS
  class Prefix(str:String) {
    def apply() : String = {
      str
    }

    val semanticType:Type = bot(bot.prefix)
  }

  class OutputFile(str:String) {
    def apply: String = str
    val semanticType:Type = bot(bot.fileName)
  }

  class MakeMain(disc:Discord) {
    def apply(fileName:String, description:String, prefix:String): PythonWithPath = {

      val code =
        Python(s"""|import discord
                   |from discord.ext import commands
                   |
                   |# depending on certain events, these are auto-generated
                   |intents = discord.Intents.default()
                   |intents.message_content = True
                   |intents.members = True
                   |
                   |description = '$description'
                   |
                   |# ************************************************************************
                   |# * $description
                   |# ************************************************************************
                   |
                   |# something would generate this
                   |bot = commands.Bot(command_prefix='$prefix', description=description, intents=intents)
                   |
                   |@bot.event
                   |async def on_ready():
                   |    print(f'Logged in as {bot.user} (ID: {bot.user.id})')
                   |    print('------')
                   |
                   |@bot.command()
                   |async def echo(ctx, *, message: str):
                   |    await ctx.send(message)
                   |
                   |@bot.command()
                   |async def hello(ctx):
                   |    await ctx.send('Hello!')
                   |
                   |# Now run...
                   |bot.run()
                   |
                   |""".stripMargin)
      PythonWithPath(code, Paths.get(fileName + ".py"))
    }
    val semanticType:Type = bot(bot.fileName) =>: bot(bot.description) =>: bot(bot.prefix) =>: bot(complete)
  }
}