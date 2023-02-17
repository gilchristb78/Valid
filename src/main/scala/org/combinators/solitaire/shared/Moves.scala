package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.syntax._
import org.combinators.cls.types.Type
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.{Constraint, Move, MultipleCards, SingleCard}
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry

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
trait Moves extends Base with JavaSemanticTypes {

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
  class PotentialMoveMultipleCards(semanticMoveNameType: Type, tpe:SimpleName) {
    def apply(rootPackage: Name, moveName: SimpleName, draggingCardVariableName: SimpleName,
              /*typeConstruct: JType*/): CompilationUnit = {
      shared.moves.java.PotentialMoveOneCardFromStack.render(
        RootPackage = rootPackage,
        MoveName = moveName,
        Type = tpe,   // typeConstruct,
        DraggingCardVariableName = draggingCardVariableName
      ).compilationUnit()
    }

    val semanticType: Type =
      packageName =>:
        move(semanticMoveNameType, className) =>:
        move(semanticMoveNameType, move.draggingVariableCardName) =>:
       // move(semanticMoveNameType, move.multipleCardMove) =>:        // UNNECESSARY SINCE JUST ADD PROPER DYNAMIC ONE. DELETE
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
      m.moveType match {
        case MultipleCards  => Java(s"""movingColumn""").simpleName()
        case SingleCard => Java(s"""movingCard""").simpleName()
        case _ => throw new RuntimeException("Invalid drag:" + m.moveType)
      }
    }
    val semanticType: Type = constructor
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
    * method.
    *
    * The constraint will likely be an Or-Constraint.
    *
    * If there is BOTH a PRESS and a DRAG move with the same source, then we have to properly handle
    * these two occurences, giving the Press a time to operate while also allowing the Drag a chance.
    *
    */
  class ColumnMoveHandler(tpe:Constructor, realType:SimpleName, c:Constraint, terminal:Type, draggingType:SimpleName) {
    def apply(generators: CodeGeneratorRegistry[Expression]): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        var filter:Seq[Statement] = Seq.empty

        // Since source-constraint and target-constraint are concatenated together, we need to convert the
        // MovingColumn component into .
        // HACK to make as ((Column) me_widget.getModelElement())
        val moveColumnRegExp = "movingColumn".r  // MoveComponents.MovingColumn.getName.r
        val moveRowRegExp = "movingRow".r        // MoveComponents.MovingRow.getName.r
        val cc3: Option[Expression] = generators(c)

        val strExp = if (cc3.isEmpty) {
          null
        } else {
          val s1 = moveColumnRegExp.replaceAllIn(cc3.get.toString, "((Column) " + widgetVariableName + ".getModelElement())")
          moveRowRegExp.replaceAllIn(s1, "((Column) " + widgetVariableName + ".getModelElement())")
        }

        if (strExp != null) {
          filter = Java(s"""
               |if ($strExp) {
               |  // This mouse press is acceptable
               |} else {
               |  // This mouse press doesn't lead to a valid move. Reject and return.
               |  src.returnWidget($widgetVariableName);
               |	$ignoreWidgetVariableName = true;
               |	c.releaseDraggingObject();
               |	return;
               |}""".stripMargin).statements()
        }

        Java(s"""|$ignoreWidgetVariableName = false;
                 |// name local variable source so it directly corresponds to predefined MoveComponentTypes
                 |$realType source = ($realType) src.getModelElement();
                 |
                 |// Return in the case that the widget clicked on is empty
                 |if (source.count() == 0) {
                 |  return;
                 |}
                 |$widgetVariableName = src.get$draggingType(me);
                 |if ($widgetVariableName == null) {
                 |  return;
                 |}
                 |
                 |${filter.mkString("\n")}
                 |""".stripMargin).statements()
    }

    val semanticType: Type = constraints(constraints.generator) =>:
      drag(drag.variable, drag.ignore) =>: terminal
  }
}
