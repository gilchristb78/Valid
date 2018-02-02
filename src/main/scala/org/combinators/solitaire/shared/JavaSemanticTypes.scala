package org.combinators.solitaire.shared

import org.combinators.cls.types._
import org.combinators.cls.types.syntax._

/**
  * These codify the semantic types necessary to do with Java version of solitaire.
  *
  * Extends SemanticTypes to clarify
  */
trait JavaSemanticTypes extends SemanticTypes {

  /** Placed here for ease of reference. */
  var constraintCodeGenerators = org.combinators.solitaire.shared.compilation.constraintCodeGenerators

  /** Used when synthesizing special class to use for variation. */
  object classes {
     def apply(name:String) : Constructor = 'Classes (Constructor(name))
  }

  /** only one part since synthesizing 'the' game. */
  object kombat {
    def apply (part:Type): Constructor = 'Game (part)

    val winCondition: Type = 'WinCondition
    val autoMoves:Type = 'AutoMoves

    val fields:Type = 'Fields
    val methods:Type = 'Methods
    val imports:Type = 'Imports    // Should be part of context...

    val model:Type = 'Model
    val view:Type= 'View
    val control:Type = 'Control

    val availableMoves:Type = 'AvailableMoves
    val solvable:Type = 'Solvable   // does game provide availableMoves

    val deal:Type = 'Deal
  }

  object score {
    val increment: Type = 'Increment
    val decrement: Type = 'Decrement
  }

  object numberCardsLeft {
    val increment: Type = 'IncrementNumberCardsLeft
    val decrement: Type = 'DecrementNumberCardsLeft
  }

  object drag {
    def apply(variable: Type, ignore: Type) : Constructor = 'Drag (variable, ignore)

    val variable: Type = 'WidgetVariableName
    val ignore: Type = 'IgnoreWidgetVariableName
  }

  // might consider changing to Type, but was Constructor in original code.
  object press {
    val card:Type = 'GuardCardView
    val column:Type = 'GuardColumnView
    val row:Type = 'GuardRowView
  }

  // parts of the structural definition of a move class
  object move {
    def apply(entity: Type, part: Type): Constructor = 'Move (entity, part)

    val helper: Type = 'HelperMethods
    val doStatements: Type = 'DoStatements
    val undoStatements: Type = 'UndoStatements
    val validStatements: Type = 'CheckValidStatements

    val draggingVariableCardName: Type = 'DraggingCardVariableName

    /** When multiple cards are being moved. */
    val multipleCardMove: Type = 'MultipleCardMove

    val generic: Type = 'GenericMove
    val potential: Type = 'PotentialMove
    val potentialMultipleMove: Type = 'PotentialMultipleMove
  }

}
