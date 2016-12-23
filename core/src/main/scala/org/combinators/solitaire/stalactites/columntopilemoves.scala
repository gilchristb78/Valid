package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.body.{BodyDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.generic

trait ColumnToPileMoves extends shared.Moves with generic.JavaIdioms {
  
  
  @combinator object ColumnToFoundationPileMove extends Move('ColumnToFoundationPile)
  
  @combinator object ColumnToFoundationName {
		def apply(): NameExpr = {
			Java("ColumnToFoundation").nameExpression
	}
	  val semanticType: Type = 'Move('ColumnToFoundationPile, 'ClassName)
	}
  
  @combinator object PotentialColumnToFoundationPileMove extends PotentialMoveOneCardFromStack ('ColumnToFoundationPile)
  
  // the construct needed to hold the card being dragged.
  @combinator object PotentialColumnToFoundationPileType {
		def apply(): NameExpr = { Java("Column").nameExpression() }
		val semanticType: Type = 'Move('ColumnToFoundationPile, 'TypeConstruct)
	}
	
  @combinator object PotentialColumnToFoundationDraggingVariable {
		def apply(): NameExpr = {
				Java("movingColumn").nameExpression()
		}
		val semanticType: Type = 'Move('ColumnToFoundationPile, 'DraggingCardVariableName)
	}

  @combinator object ColumnToFoundationPileValid {
		def apply(root:NameExpr, name:NameExpr): Seq[Statement] = {
				moves.columntofoundationpile.java.ColumnToFoundationPileValid.render(root, name).statements()
		}
		val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move('ColumnToFoundationPile, 'CheckValidStatements)
	}
  
	@combinator object ColumnToFoundationHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.columntofoundationpile.java.ColumnToFoundationPileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ColumnToFoundationPile, 'ClassName) =>: 'Move('ColumnToFoundationPile, 'HelperMethods)
	}
	
	@combinator object ColumnToFoundationMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.push(movingColumn);").statements()
		}
		val semanticType: Type = 'MoveColumn
	}

	@combinator object ColumnToFoundationMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						destination.select(1);
						source.push(destination.getSelected());
						""").statements()
		}
		val semanticType: Type = 'UndoColumn
	}

	/** Compose these behaviors. */
	@combinator object DoCFP extends StatementCombiner('MoveColumn, 'IncrementScore, 'IntermediateDoCFPScore)
	@combinator object DoCFP1 extends StatementCombiner('IntermediateDoCFPScore, 'DecrementNumberCardsLeft, 'IntermediateDoCFPScore2)
	@combinator object DoCFP2 extends StatementCombiner('IntermediateDoCFPScore2, 'RecordOrientation, 'Move('ColumnToFoundationPile, 'DoStatements))
	
	@combinator object UndoCFP extends StatementCombiner('UndoColumn, 'DecrementScore, 'IntermediateUndoCFPScore)
	@combinator object UndoCFP1 extends StatementCombiner('IntermediateUndoCFPScore, 'IncrementNumberCardsLeft, 'IntermediateUndoCFPScore2)
	@combinator object UndoCFP2 extends StatementCombiner('IntermediateUndoCFPScore2, 'UndoOrientation, 'Move('ColumnToFoundationPile, 'UndoStatements))
	
	// and injecting them into the compilation unit. Hate to have to sequence them like this, but it works.
	@combinator object OrientationCFPOCombinator extends GetterSetterMethods(Java("orientation").nameExpression(), "int", 'Move('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove), 'orientation)
  @combinator object OrientationPCFPOCombinator extends GetterSetterMethods(Java("orientation").nameExpression(), "int", 'Move('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove), 'orientation)
  
	@combinator object OrientationCFPCombinator extends GetterSetterMethods(Java("lastOrientation").nameExpression(), "int", 'orientation('Move('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove)), 'lastOrientation)
  @combinator object OrientationPCFPCombinator extends GetterSetterMethods(Java("lastOrientation").nameExpression(), "int", 'orientation('Move('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove)), 'lastOrientation)
  
	
	/// ----------------------- THESE ARE TO THE RESERVE ------------------
	
	@combinator object ColumnToReservePileMove extends Move('ColumnToReservePile)
  
	// THIS WAS MISSING apply() but rather had apply without () and no error/warning.
  @combinator object ColumnToReserveName {
		def apply(): NameExpr = {
			Java("ColumnToReserve").nameExpression
	}
	  val semanticType: Type = 'Move('ColumnToReservePile, 'ClassName)
	}
  
  @combinator object PotentialColumnToReservePileMove extends PotentialMoveOneCardFromStack ('ColumnToReservePile)
  
  // the construct needed to hold the card being dragged.
  @combinator object PotentialColumnToReservePileType {
		def apply(): NameExpr = { Java("Column").nameExpression() }
		val semanticType: Type = 'Move('ColumnToReservePile, 'TypeConstruct)
	}
	
  @combinator object PotentialColumnToReserveDraggingVariable {
		def apply(): NameExpr = {
				Java("movingColumn").nameExpression()
		}
		val semanticType: Type = 'Move('ColumnToReservePile, 'DraggingCardVariableName)
	}

  @combinator object ColumnToReservePileValid {
		def apply(): Seq[Statement] = {
				moves.columntoreservepile.java.ColumnToReservePileValid.render().statements()
		}
		val semanticType: Type = 'Move('ColumnToReservePile, 'CheckValidStatements)
	}
  
	@combinator object ColumnToReserveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.columntoreservepile.java.ColumnToReservePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ColumnToReservePile, 'ClassName) =>: 'Move('ColumnToReservePile, 'HelperMethods)
	}
	
	@combinator object ColumnToReserveMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.push(movingColumn);").statements()
		}
		val semanticType: Type = 'MoveColumnRP
	}

	@combinator object ColumnToReserveMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						destination.select(1);
						source.push(destination.getSelected());
						""").statements()
		}
		val semanticType: Type = 'UndoColumnRP
	}
	
	// updates number of cards left when moving from column.
  @combinator object DoCRP extends StatementCombiner('MoveColumnRP, 'DecrementNumberCardsLeft, 'Move('ColumnToReservePile, 'DoStatements))
	@combinator object UndoCRP extends StatementCombiner('UndoColumnRP,  'IncrementNumberCardsLeft, 'Move('ColumnToReservePile, 'UndoStatements))
	

}