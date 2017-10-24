package org.combinators.solitaire.shared

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._


/**
  * These codify the semantic types used by the Solitaire combinators.
  *
  * For any of these that are ever going to be translated directly into Java Type Names, you must
  * make them Constructor.
  */
trait SemanticTypes {

  // structural high-level concerns
  val packageName: Type = 'RootPackage
  val variationName: Type = 'NameOfTheGame
  val className:Type = 'ClassName

  // meta-concerns. When you have completed the definition of a constructor
  val complete: Type = 'Complete
  val initialized:Type = 'Initialized

  /**
    * Constructing combinators from scratch require unique ids
    */
  object dynamic {
    def apply (uniq:Symbol) : Constructor = 'Dynamic(uniq)
  }

  // common structures
  val deck: Constructor          = 'Deck
  val column: Constructor        = 'Column
  val buildablePile: Constructor = 'BuildablePile
  val pile: Constructor          = 'Pile

  /** only one part since synthesizing 'the' game. */
  object game {
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
  }

  // parts of the widgets during move : Dynamic Behavior
  object widget {
    def apply (entity:Type, part:Type): Constructor = 'MoveElement (entity, part)

    val movable:Type = 'MovableElementName
    val source:Type = 'SourceWidgetName
    val target:Type = 'TargetWidgetName
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

  /**
    * Manages the press/click/release on widgets.
    */
  object controller {
    def apply(context: Type, part: Type): Constructor = 'Controller (context, part)

    val pressed:Type = 'Pressed
    val clicked:Type = 'Clicked
    val released:Type = 'Released

  }

  /**
    * Used to clarify the helper code to be synthesized.
    */
  object constraints {
    def apply(part: Type): Constructor = 'Constraints (part)

    val methods:Type = 'Methods
    val generator:Type = 'ConstraintGen
  }

}
