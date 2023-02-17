package org.combinators.discord.shared

import org.combinators.cls.types._
import org.combinators.cls.types.syntax._

trait SemanticTypes {

  // structural high-level concerns
  val packageName: Type = 'RootPackage

  // meta-concerns. When you have completed the definition of a constructor
  val complete: Type = 'Complete
  val initialized: Type = 'Initialized

  /**
   * Constructing combinators from scratch require unique ids
   */
  object dynamic {
    def apply(uniq: Symbol): Constructor = 'Dynamic(uniq)
  }

  /** only one part since synthesizing 'the' game. */
  object bot {
    def apply(part: Type): Constructor = 'Bot(part)

    val prefix: Type = 'Prefix
    val description: Type = 'Description

    val fields: Type = 'Fields
    val methods: Type = 'Methods
    val imports: Type = 'Imports // Should be part of context...

    val fileName:Type = 'FileName
    val structure:Type = 'Structure

    val initFile:Type = 'InitFile
  }
}
