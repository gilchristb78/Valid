package org.combinators.discord.shared.compilation

import org.apache.commons.io.FileUtils
import org.combinators.cls.git.{ResultLocation, Results}
import org.combinators.cls.types.Constructor
import org.combinators.discord.domain.Discord
import org.combinators.solitaire.domain.Solitaire

import java.nio.file.Paths

/** Enforces everything is proper. */
trait DiscordSolution {

    val discord: Discord

    /** The computed result location (root/sourceDirectory) */
    implicit val resultLocation: ResultLocation

    val targets: Seq[Constructor]

    val results: Results

    val routingPrefix: Option[String]
    lazy val controllerAddress: String = discord.name.toLowerCase
}

// Must always be inherited FIRST
trait DefaultMain extends App { self:DiscordSolution =>

  override lazy val routingPrefix: Option[String] = None

  val results: Results

  // no longer needed? TODO: Check
  implicit lazy val priorCode: Option[ResultLocation] = if (routingPrefix.isEmpty) {
      Option.empty
  } else {
      Some(ResultLocation(Paths.get("target", "discord", "src", controllerAddress)))
  }

  implicit lazy val resultLocation: ResultLocation = ResultLocation(Paths.get("target", "discord"))

  println("resulting targets:" + results)
  println(results.targets.collect { case (ty, Some(n)) if n == BigInt(0) => s"&Gamma; &vdash; ? : ${ty.toString}" }.mkString("\n"))

  // clean up before generating
  println("cleaning prior code")
  if (priorCode.nonEmpty) {
    FileUtils.deleteDirectory(priorCode.get.relativeTo.toFile)
  }

  println("storing generated code in " + resultLocation.relativeTo.toString)
  results.storeToDisk(0)
}
