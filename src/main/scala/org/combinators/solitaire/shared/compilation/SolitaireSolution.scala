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

  /** The computed result location (target/solitaire so all generated code goes into same directory.) */
//  implicit lazy val resultLocation: ResultLocation = if (routingPrefix.isEmpty) {
//      ResultLocation(Paths.get("target", controllerAddress))
//  } else {
//      ResultLocation(Paths.get("target", routingPrefix.get, controllerAddress))
//  }

  implicit lazy val priorCode: Option[ResultLocation] = if (routingPrefix.isEmpty) {
      Option.empty
  } else {
      //Some(ResultLocation(Paths.get("target", "solitaire", "src", "main", "java", "org", "combinators", "solitaire", controllerAddress)))
        Some(ResultLocation(Paths.get("generated", "src", "main", "java", "org", "combinators", "solitaire", controllerAddress)))
  }

  //implicit lazy val resultLocation: ResultLocation = ResultLocation(Paths.get("target", "solitaire"))
  implicit lazy val resultLocation: ResultLocation = ResultLocation(Paths.get("generated"))

  println("resulting targets:" + results)
  println(results.targets.collect { case (ty, Some(n)) if n == BigInt(0) => s"&Gamma; &vdash; ? : ${ty.toString}" }.mkString("\n"))

  // clean up before generating
  println("cleaning prior code:" + priorCode)
  if (priorCode.nonEmpty) {
    FileUtils.deleteDirectory(priorCode.get.relativeTo.toFile)
  }

  println("storing generated code in " + resultLocation.relativeTo.toString)
  results.storeToDisk(0)
}