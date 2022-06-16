package org.combinators.solitaire.shared.compilation

import org.apache.commons.io.FileUtils
import org.combinators.cls.git.{ResultLocation, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire

import java.nio.file.Paths

/** Enforces everything is proper. */
trait SolitaireSolution {

    val solitaire: Solitaire

    /** The computed result location (root/sourceDirectory) */
    implicit val resultLocation: ResultLocation

    val targets: Seq[Constructor]

    val results: Results

    val routingPrefix: Option[String]
    lazy val controllerAddress: String = solitaire.name.toLowerCase
}

// Must always be inherited FIRST
trait DefaultMain extends App { self:SolitaireSolution =>

  override lazy val routingPrefix: Option[String] = None

  val results: Results

  /** The computed result location (root/sourceDirectory) */
  implicit lazy val resultLocation: ResultLocation = if (routingPrefix.isEmpty) {
      ResultLocation(Paths.get("target", controllerAddress))
  } else {
      ResultLocation(Paths.get("target", routingPrefix.get, controllerAddress))
  }

  println("resulting targets")
  println(results.targets.collect { case (ty, Some(n)) if n == BigInt(0) => s"&Gamma; &vdash; ? : ${ty.toString}" }.mkString("\n"))

  // clean up before generating
  println("cleaning target output")
  FileUtils.deleteDirectory(resultLocation.relativeTo.toFile)

  println("storing generated code in " + resultLocation.relativeTo.toString)
  results.storeToDisk(0)
}