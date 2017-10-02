package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, Expression, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

import domain._
import domain.moves._

/**
  * This trait contains combinators related to moves in a solitaire variation.
  *
  * Each move in solitaire defines a subclass of the Move base class.
  * The consituent parts of a move are:
  *
  *   1. Package and class name
  *   2. Helper declarations (which include constructors, fields, methods)
  *   3. Statements that contain the logic of the move (DO)
  *   4. Statements that contain the logic of undoing a move (UNDO)
  *   5. Statements that contain the logic to determine whether move is valid
  *
  * One can envision a future expansion that automatically synthesizes the
  * Undo logic given just the Do logic.
  */
trait Moves extends Base {

  /* 
   * From one source to many destinations. 
   * Or from many destinations to one source. 
   */
  class MultiMove(semanticMoveNameType: Type) {
    def apply(rootPackage: Name,
              moveName: SimpleName,
              helper: Seq[BodyDeclaration[_]],
              doStmts: Seq[Statement],
              undoStmts: Seq[Statement],
              checkValid: Seq[Statement]): CompilationUnit = {
      shared.moves.java.MultiMove.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Helper = helper,
        Do = doStmts,
        Undo = undoStmts,
        CheckValid = checkValid
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Move (semanticMoveNameType, 'ClassName) =>:
        'Move (semanticMoveNameType, 'HelperMethods) =>:
        'Move (semanticMoveNameType, 'DoStatements) =>:
        'Move (semanticMoveNameType, 'UndoStatements) =>:
        'Move (semanticMoveNameType, 'CheckValidStatements) =>:
        'Move (semanticMoveNameType :&: 'GenericMove, 'CompleteMove)
  }

  /**
    * From one source to another source.
    * renamed to avoid name clash with Java-domain 'Move' class
    */
  class SolitaireMove(semanticMoveNameType: Type) {
    def apply(rootPackage: Name,
              moveName: SimpleName,
              helper: Seq[BodyDeclaration[_]],
              doStmts: Seq[Statement],
              undoStmts: Seq[Statement],
              checkValid: Seq[Statement]): CompilationUnit = {
      shared.moves.java.Move.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Helper = helper,
        Do = doStmts,
        Undo = undoStmts,
        CheckValid = checkValid
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Move (semanticMoveNameType, 'ClassName) =>:
        'Move (semanticMoveNameType, 'HelperMethods) =>:
        'Move (semanticMoveNameType, 'DoStatements) =>:
        'Move (semanticMoveNameType, 'UndoStatements) =>:
        'Move (semanticMoveNameType, 'CheckValidStatements) =>:
        'Move (semanticMoveNameType :&: 'GenericMove, 'CompleteMove)
  }

  /**
    * Given an existing move, this combinator uses the PotentialMove
    * template to synthesize a new "wrapper class" that reflects a
    * potential move, that is, one that could be made in the future.
    */
  class PotentialMove(semanticMoveNameType: Type) {
    def apply(rootPackage: Name, moveName: SimpleName, draggingCardVariableName: SimpleName): CompilationUnit = {
      shared.moves.java.PotentialMove.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Move (semanticMoveNameType, 'ClassName) =>:
        'Move (semanticMoveNameType, 'DraggingCardVariableName) =>:
        'Move (semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)
  }

  /**
    * Given an existing move that requires moving a column of cards,
    * this combinator synthesizes a new "wrapper class" that creates
    * a column by grabbing the top card from the underlying stack.
    *
    * Choose this one (over PotentialMove) when moves involve columns.
    */
  class PotentialMoveOneCardFromStack(semanticMoveNameType: Type) {
    def apply(rootPackage: Name,
              moveName: SimpleName,
              draggingCardVariableName: SimpleName,
              typeConstruct: JType): CompilationUnit = {
      shared.moves.java.PotentialMoveOneCardFromStack.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Type = typeConstruct,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Move (semanticMoveNameType, 'ClassName) =>:
        'Move (semanticMoveNameType, 'DraggingCardVariableName) =>:
        'Move (semanticMoveNameType, 'TypeConstruct) =>:
        'Move (semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)
  }

  /**
    * Create a Move class that resets a Deck of cards from a collection
    * of stacks.
    */
  @combinator object ResetDeck {
    def apply(rootPackage: Name): CompilationUnit = {
      shared.moves.java.ResetDeck.render(rootPackage).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'Move ('ResetDeck, 'CompleteMove)
  }

  /**
    * Increment score
    */
  @combinator object IncrementScore {
    def apply(): Seq[Statement] = Java("game.updateScore(1);").statements()
    val semanticType: Type = 'IncrementScore
  }

  /**
    * Decrement score
    */
  @combinator object DecrementScore {
    def apply(): Seq[Statement] = Java("game.updateScore(-1);").statements()
    val semanticType: Type = 'DecrementScore
  }

  /**
    * Increment NumberCardsLeft
    */
  @combinator object IncrementNumberCardsLeft {
    def apply(): Seq[Statement] = Java("game.updateNumberCardsLeft(1);").statements()
    val semanticType: Type = 'IncrementNumberCardsLeft
  }

  /**
    * Decrement NumberCardsLeft
    */
  @combinator object DecrementNumberCardsLeft {
    def apply(): Seq[Statement] = Java("game.updateNumberCardsLeft(-1);").statements()
    val semanticType: Type = 'DecrementNumberCardsLeft
  }

//  /**
//    * Useful generic move for removing a single card from a stack
//    */
//  @combinator object RemoveSingleCard {
//    def apply(rootPackage: Name): CompilationUnit = {
//      shared.moves.java.RemoveSingleCard.render(rootPackage).compilationUnit()
//    }
//    val semanticType: Type = 'RootPackage =>: 'Move ('RemoveSingleCard, 'CompleteMove)
//  }

  /**
    * Creates stand-alone class to represent a card that has been removed
    * from a specific source element (identified by name)>
    */
//  @combinator object RemovedCard {
//    def apply(rootPackage: Name): CompilationUnit = {
//      shared.moves.java.RemovedCard.render(rootPackage).compilationUnit()
//    }
//    val semanticType: Type = 'RootPackage =>: 'Move ('RemovedCard, 'CompleteMove)
//  }

  /**
    * Deal cards from deck onto a set of stacks.
    */
//  @combinator object DealStacks {
//    def apply(rootPackage: Name): CompilationUnit = {
//      shared.moves.java.DealStacksMove.render(rootPackage).compilationUnit()
//    }
//    val semanticType: Type = 'RootPackage =>: 'Move ('DealStacks, 'CompleteMove)
//  }

  /**
    * Scala class to generate combinators which record the name of the
    * variable used to represent the widget being dragged.
    */
  class PotentialDraggingVariableGenerator(m:Move, constructor:Constructor) {
    def apply(): SimpleName = {
      m match {
        case _ : SingleCardMove => Java(s"""movingCard""").simpleName()
        case _ : ColumnMove     => Java(s"""movingColumn""").simpleName()
      }
    }
    val semanticType: Type = constructor
  }

  /**
    * Identify the TypeConstruct logical symbol to associate with the
    * potential Move.
    */
  class PotentialTypeConstructGen(typ:String, constructor:Constructor) {
    def apply(): JType = Java(typ).tpe()
    val semanticType: Type = 'Move (constructor, 'TypeConstruct)
  }

  /**
    * When a single card is being removed from the top card of a widget,
    * either a Column or a Pile
    */
  class SingleCardMoveHandler(realType:String, typ:Symbol, source:Symbol) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
                 |$realType srcElement = ($realType) src.getModelElement();
                 |
                 |// Return in the case that the widget clicked on is empty
                 |if (srcElement.count() == 0) {
                 |  return;
                 |}
                 |$widgetVariableName = src.getCardViewForTopCard(me);
                 |if ($widgetVariableName == null) {
                 |  return;
                 |}""".stripMargin).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        typ (source, 'Pressed) :&: 'NonEmptySeq
  }

  /**
    * When a column of cards is being removed from the top card of a widget,
    * either a Column or perhaps a buildablePile
    *
    * Provides ability to add 'filtering' statements that can determine whether to deny
    * the press request (typically because of requirement that cards form alternating colors or
    * descending suits, for example.
    */
  class ColumnMoveHandler(realType:String, typ:Symbol, source:Symbol, name:SimpleName = null) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        var filter:Seq[Statement] = Seq.empty
        if (name != null) {
          filter = Java(s"""
               |if (!theGame.$name((Column) (${widgetVariableName}.getModelElement()))) {
               |  src.returnWidget($widgetVariableName);
               |	$ignoreWidgetVariableName = true;
               |	c.releaseDraggingObject();
               |	return;
               |}""".stripMargin).statements()
        }

        Java(s"""|$ignoreWidgetVariableName = false;
                 |$realType srcElement = ($realType) src.getModelElement();
                 |
                 |// Return in the case that the widget clicked on is empty
                 |if (srcElement.count() == 0) {
                 |  return;
                 |}
                 |$widgetVariableName = src.getColumnView(me);
                 |if ($widgetVariableName == null) {
                 |  return;
                 |}
                 |
                 |${filter.mkString("\n")}
                 |""".stripMargin).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        typ (source, 'Pressed) :&: 'NonEmptySeq
  }

}
