package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._


import scala.io.StdIn

object Narcotic extends App {
  def askForNext(): Boolean = {
    println("Compute next result? [Y/n]")
    val input = StdIn.readLine().map(_.toLower)
    if (input == "" || input == "y") true
    else if (input == "n") false
    else askForNext()
  }

  val repository = new Game with Moves {}
  val Gamma = ReflectedRepository(repository)
  val results = Gamma.inhabit[CompilationUnit]('SolitaireVariation)

  println("Gamma = {")
  Gamma.combinators.foreach { case (k, v) => println(s"\t $k : $v") }
  println("}")

  println(s"Question: Gamma |- ? : ${results.target}")
  println()
  println(s"Solution grammar:")
  results.grammar.foreach{ case (k, v) => println(s"$k -> $v") }
  println()
  println("Solutions:")
  val rawInhabitants = results.terms.values.flatMap(_._2).iterator
  val resultIterator = results.interpretedTerms.values.flatMap(_._2).iterator
  var next = rawInhabitants.hasNext
  while (next) {
    println(s"Raw inhabitant: ${rawInhabitants.next()}")
    println(resultIterator.next())
    next = rawInhabitants.hasNext && askForNext()
  }
}