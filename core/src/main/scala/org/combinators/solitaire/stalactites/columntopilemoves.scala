package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared

trait ColumnToPileMoves extends shared.Moves with generic.JavaIdioms {


  @combinator object ColumnToFoundationPileMove extends SolitaireMove('ColumnToFoundationPile)

  @combinator object ColumnToFoundationName {
    def apply(): SimpleName = Java("ColumnToFoundation").simpleName()
    val semanticType: Type = 'Move ('ColumnToFoundationPile, 'ClassName)
  }

  @combinator object PotentialColumnToFoundationPileMove extends PotentialMoveMultipleCards('ColumnToFoundationPile)

  // the construct needed to hold the card being dragged.
  @combinator object PotentialColumnToFoundationPileType {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToFoundationPile, 'SingleCardMove)
  }

  @combinator object PotentialColumnToFoundationDraggingVariable {
    def apply(): SimpleName = Java("movingColumn").simpleName()
    val semanticType: Type = 'Move ('ColumnToFoundationPile, 'DraggingCardVariableName)
  }

  @combinator object ColumnToFoundationPileValid {
    def apply(root: Name, name: SimpleName): Seq[Statement] = {
      moves.columntofoundationpile.java.ColumnToFoundationPileValid.render(root, name).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move ('ColumnToFoundationPile, 'CheckValidStatements)
  }

  @combinator object ColumnToFoundationHelper {
    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
      moves.columntofoundationpile.java.ColumnToFoundationPileMoveHelper.render(name).classBodyDeclarations()
    }
    val semanticType: Type = 'Move ('ColumnToFoundationPile, 'ClassName) =>: 'Move ('ColumnToFoundationPile, 'HelperMethods)
  }

  @combinator object ColumnToFoundationMoveDo {
    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
    val semanticType: Type = 'MoveColumn
  }

  @combinator object ColumnToFoundationMoveUndo {
    def apply(): Seq[Statement] = {
      Java(
        s"""
           |destination.select(1);
           |source.push(destination.getSelected());
           """.stripMargin).statements()
    }
    val semanticType: Type = 'UndoColumn
  }

  /** Compose these behaviors. */
  @combinator object DoCFP extends StatementCombiner('MoveColumn, 'IncrementScore, 'IntermediateDoCFPScore)
  @combinator object DoCFP1 extends StatementCombiner('IntermediateDoCFPScore, 'DecrementNumberCardsLeft, 'IntermediateDoCFPScore2)
  @combinator object DoCFP2 extends StatementCombiner('IntermediateDoCFPScore2, 'RecordOrientation, 'Move ('ColumnToFoundationPile, 'DoStatements))

  @combinator object UndoCFP extends StatementCombiner('UndoColumn, 'DecrementScore, 'IntermediateUndoCFPScore)
  @combinator object UndoCFP1 extends StatementCombiner('IntermediateUndoCFPScore, 'IncrementNumberCardsLeft, 'IntermediateUndoCFPScore2)
  @combinator object UndoCFP2 extends StatementCombiner('IntermediateUndoCFPScore2, 'UndoOrientation, 'Move ('ColumnToFoundationPile, 'UndoStatements))

  // and injecting them into the compilation unit. Hate to have to sequence them like this, but it works.
  @combinator object OrientationCFPOCombinator
    extends GetterSetterMethods(
      Java("orientation").simpleName(),
      Java("int").tpe(),
      'Move ('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove),
      'orientation)
  @combinator object OrientationPCFPOCombinator
    extends GetterSetterMethods(
      Java("orientation").simpleName(),
      Java("int").tpe(),
      'Move ('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove),
      'orientation)
  @combinator object OrientationCFPCombinator
    extends GetterSetterMethods(
      Java("lastOrientation").simpleName(),
      Java("int").tpe(),
      'orientation ('Move ('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove)),
      'lastOrientation)
  @combinator object OrientationPCFPCombinator
    extends GetterSetterMethods(
      Java("lastOrientation").simpleName(),
      Java("int").tpe(),
      'orientation ('Move ('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove)),
      'lastOrientation)


  /// ----------------------- THESE ARE TO THE RESERVE ------------------

  @combinator object ColumnToReservePileMove extends SolitaireMove('ColumnToReservePile)

  @combinator object ColumnToReserveName {
    def apply(): SimpleName = Java("ColumnToReserve").simpleName()
    val semanticType: Type = 'Move ('ColumnToReservePile, 'ClassName)
  }

  @combinator object PotentialColumnToReservePileMove extends PotentialMoveMultipleCards('ColumnToReservePile)

  // the construct needed to hold the card being dragged.
  @combinator object PotentialColumnToReservePileType {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToReservePile, 'SingleCardMove)
  }

  @combinator object PotentialColumnToReserveDraggingVariable {
    def apply(): SimpleName = Java("movingColumn").simpleName()
    val semanticType: Type = 'Move ('ColumnToReservePile, 'DraggingCardVariableName)
  }

  @combinator object ColumnToReservePileValid {
    def apply(): Seq[Statement] = {
      moves.columntoreservepile.java.ColumnToReservePileValid.render().statements()
    }
    val semanticType: Type = 'Move ('ColumnToReservePile, 'CheckValidStatements)
  }

  @combinator object ColumnToReserveHelper {
    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
      moves.columntoreservepile.java.ColumnToReservePileMoveHelper.render(name).classBodyDeclarations()
    }
    val semanticType: Type = 'Move ('ColumnToReservePile, 'ClassName) =>: 'Move ('ColumnToReservePile, 'HelperMethods)
  }

  @combinator object ColumnToReserveMoveDo {
    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
    val semanticType: Type = 'MoveColumnRP
  }

  @combinator object ColumnToReserveMoveUndo {
    def apply(): Seq[Statement] = {
      Java(
        s"""
           |destination.select(1);
           |source.push(destination.getSelected());
           """.stripMargin).statements()
    }
    val semanticType: Type = 'UndoColumnRP
  }

  // updates number of cards left when moving from column.
  @combinator object DoCRP extends StatementCombiner('MoveColumnRP, 'DecrementNumberCardsLeft, 'Move ('ColumnToReservePile, 'DoStatements))
  @combinator object UndoCRP extends StatementCombiner('UndoColumnRP, 'IncrementNumberCardsLeft, 'Move ('ColumnToReservePile, 'UndoStatements))


}
