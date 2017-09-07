package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, NameExpr, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

import domain._
import domain.constraints._
import domain.moves._
import domain.ui._

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

  // renamed to avoid name clash with Java-domain 'Move' class
  /* From one source to another source. */
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
    * Reassemble a deck from a number of stacks.
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

  // Useful generic move for removing a single card from a stack
  @combinator object RemoveSingleCard {
    def apply(rootPackage: Name): CompilationUnit = {
      shared.moves.java.RemoveSingleCard.render(rootPackage).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'Move ('RemoveSingleCard, 'CompleteMove)
  }

  @combinator object RemovedCard {
    def apply(rootPackage: Name): CompilationUnit = {
      shared.moves.java.RemovedCard.render(rootPackage).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'Move ('RemovedCard, 'CompleteMove)
  }

  /**
    * Deal cards from deck onto a set of stacks.
    */
  @combinator object DealStacks {
    def apply(rootPackage: Name): CompilationUnit = {
      shared.moves.java.DealStacksMove.render(rootPackage).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'Move ('DealStacks, 'CompleteMove)
  }

class PotentialDraggingVariableGenerator(m:Move, constructor:Constructor) {
  def apply(): SimpleName = {
    m match {
      case single: SingleCardMove => Java(s"""movingCard""").simpleName()
      case column: ColumnMove     => Java(s"""movingColumn""").simpleName()
    }
  }
    val semanticType: Type = constructor
}

// Note: while I can have code within the apply() method, the semanticType
// is static, so that must be passed in as is. These clarify that a
// potential moveOneCardFromStack is still a Column Type.
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



  val moveTaxonomy: Taxonomy =
    Taxonomy("GenericMove")
      .addSubtype("PotentialMove")
      .addSubtype("ResetDeck")
      .addSubtype("RemoveCard")
      .addSubtype("DealStacks")
  //.addSubtype("FreeCellColumnToColumn")     // HOW TO EXPOSE THIS TO EXTENDERS! Is this necessary?
}
