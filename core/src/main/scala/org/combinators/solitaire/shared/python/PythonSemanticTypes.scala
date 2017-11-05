package org.combinators.solitaire.shared.python

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.SemanticTypes

/**
  * These codify the semantic types necessary to do with Java version of solitaire.
  *
  * Extends SemanticTypes to clarify 
  */
trait PythonSemanticTypes extends SemanticTypes {

  // every PysolFC game has a unique ID. Must be maintained in special registry.
  val gameID: Type = 'GameId

  /** Even though has same 'game' object definition from Semantic Types, these apparently can co-exist. */
  object pysol {
    def apply (part:Type): Constructor = 'Pysol (part)

    val fileName:Type = 'FileName
    val createGame:Type = 'CreateGame
    val startGame:Type = 'StartGame
    val classes:Type = 'ClassDefinitions
    val structure:Type = 'Structure

    val initFile:Type = 'InitFile
  }

  object pygames {
    val castle:Int = 99000
    val klondike:Int = 99001
    val narcotic:Int = 99002
  }
}
