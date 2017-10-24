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
  * The constituent parts of a move are:
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
trait Moves extends Base with SemanticTypes {

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
      packageName =>:
      move (semanticMoveNameType, className) =>:
      move (semanticMoveNameType, move.helper) =>:
      move (semanticMoveNameType, move.doStatements) =>:
      move (semanticMoveNameType, move.undoStatements) =>:
      move (semanticMoveNameType, move.validStatements) =>:
      move (semanticMoveNameType :&: move.generic, complete)
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
      packageName =>:
        move (semanticMoveNameType, className) =>:
        move (semanticMoveNameType, move.helper) =>:
        move (semanticMoveNameType, move.doStatements) =>:
        move (semanticMoveNameType, move.undoStatements) =>:
        move (semanticMoveNameType, move.validStatements) =>:
        move (semanticMoveNameType :&: move.generic, complete)
  }

  
  /**
    * Given an existing move, this combinator uses the PotentialMove
    * template to synthesize a new "wrapper class" that reflects a
    * potential move, that is, one that could be made in the future.
    */
  class PotentialMoveSingleCard(semanticMoveNameType: Type) {
    def apply(rootPackage: Name, moveName: SimpleName, draggingCardVariableName: SimpleName): CompilationUnit = {
      shared.moves.java.PotentialMove.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }

    val semanticType: Type =
      packageName =>:
        move(semanticMoveNameType, className) =>:
        move(semanticMoveNameType, move.draggingVariableCardName) =>:
        move(semanticMoveNameType :&: move.potential, complete)
  }

  /**
    * Given an existing move that requires moving a column of cards,
    * this combinator synthesizes a new "wrapper class" that creates
    * a column (or whatever typeConstruct specifies) by grabbing the top
    * card from the underlying stack.
    *
    * Choose this one (over PotentialMoveSingleCard) when moves involve columns.
    */
  class PotentialMoveMultipleCards(semanticMoveNameType: Type) {
    def apply(rootPackage: Name, moveName: SimpleName, draggingCardVariableName: SimpleName,
              typeConstruct: JType): CompilationUnit = {
      shared.moves.java.PotentialMoveOneCardFromStack.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Type = typeConstruct,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }

    val semanticType: Type =
      packageName =>:
        move(semanticMoveNameType, className) =>:
        move(semanticMoveNameType, move.draggingVariableCardName) =>:
        move(semanticMoveNameType, move.multipleCardMove) =>:
        move(semanticMoveNameType :&: move.potentialMultipleMove, complete)
  }

  /**
    * Increment score
    */
  @combinator object IncrementScore {
    def apply(): Seq[Statement] = Java("game.updateScore(1);").statements()
    val semanticType: Type = score.increment
  }

  /**
    * Decrement score
    */
  @combinator object DecrementScore {
    def apply(): Seq[Statement] = Java("game.updateScore(-1);").statements()
    val semanticType: Type = score.decrement
  }

  /**
    * Increment NumberCardsLeft
    */
  @combinator object IncrementNumberCardsLeft {
    def apply(): Seq[Statement] = Java("game.updateNumberCardsLeft(1);").statements()
    val semanticType: Type = numberCardsLeft.increment
  }

  /**
    * Decrement NumberCardsLeft
    */
  @combinator object DecrementNumberCardsLeft {
    def apply(): Seq[Statement] = Java("game.updateNumberCardsLeft(-1);").statements()
    val semanticType: Type = numberCardsLeft.decrement
  }


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
    * Identify that a potential move can involve multiple cards, and uses the given Java type.
    */
  class PotentialMultipleCardMove(typ:String, constructor:Constructor) {
    def apply(): JType = Java(typ).tpe()
    val semanticType: Type = move(constructor, move.multipleCardMove)
  }

  /**
    * When a single card is being removed from the top card of a widget,
    * either a Column or a Pile
    */
  class SingleCardMoveHandler(tpe:Constructor) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        val realType = tpe.toString
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
      drag(drag.variable, drag.ignore) =>:
    controller(tpe, controller.pressed)
  }

  /**
    * When a column of cards is being removed from the top card of a widget,
    * either a Column or perhaps a buildablePile
    *
    * Provides ability to add 'filtering' statements that can determine whether to deny
    * the press request (typically because of requirement that cards form alternating colors or
    * descending suits, for example.
    *
    * TODO: Work to bring move precondition in here, rather than relegating to an extra
    * method
    */
  class ColumnMoveHandler(tpe:Constructor, realType:SimpleName, name:SimpleName = null) {
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
      drag(drag.variable, drag.ignore) =>: controller(tpe, controller.pressed)
  }
}
