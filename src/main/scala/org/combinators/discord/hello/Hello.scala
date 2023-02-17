package org.combinators.discord.hello

import org.combinators.templating.persistable.PythonWithPath
import org.combinators.templating.persistable.PythonWithPathPersistable._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.discord.shared.compilation.{DefaultMain, DiscordSolution}

trait HelloT extends DiscordSolution {

  lazy val repository = new HelloDomain(discord) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), discord)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Seq(bot(complete))
  lazy val jobs =
    Gamma.InhabitationBatchJob[PythonWithPath](targets.head)    // Why just singular target here?

  lazy val results:Results = EmptyResults().addAll(jobs.run())
}

// Match the Trait with multi card moves with the model that defines multi card moves
object HelloDiscordMain extends DefaultMain with HelloT {
  override lazy val discord = org.combinators.discord.hello.hello
}
