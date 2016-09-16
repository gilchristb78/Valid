package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.body.{BodyDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait PileMoves extends shared.Moves {

//		val semanticType: Type =
//				'RootPackage =>:
//					'Move(semanticMoveNameType, 'ClassName) =>: 
//				  'Move(semanticMoveNameType, 'HelperMethods) =>:
//					'Move(semanticMoveNameType, 'DoStatements) =>:
//					'Move(semanticMoveNameType, 'UndoStatements) =>:
//					'Move(semanticMoveNameType, 'CheckValidStatements) =>:
//					'Move(semanticMoveNameType :&: 'GenericMove, 'CompleteMove)

	@combinator object FreePileToColumnMoveObject extends Move ('FreePileToColumn)
	@combinator object ColumnToFreePileMoveObject extends Move ('ColumnToFreePile)	
	@combinator object ColumnToHomePileMoveObject extends Move ('ColumnToHomePile)
	@combinator object FreePileToHomePileMoveObject extends Move ('FreePileToHomePile)
	@combinator object FreePileToFreePileMoveObject extends Move ('FreePileToFreePile)
	
	@combinator object FreePileToColumn {
		def apply: NameExpr = {
			Java("FreePileToColumn").nameExpression
	}
	  val semanticType: Type = 'Move('FreePileToColumn, 'ClassName)
	}
	@combinator object ColumnToHomePile {
		def apply: NameExpr = {
			Java("ColumnToHomePile").nameExpression
	}
	  val semanticType: Type = 'Move('ColumnToHomePile, 'ClassName)
	}
	@combinator object FreePileToHomePile {
		def apply: NameExpr = {
			Java("FreePileToHomePile").nameExpression
	}
	  val semanticType: Type = 'Move('FreePileToHomePile, 'ClassName)
	}
	@combinator object ColumnToFreePile {
		def apply: NameExpr = {
			Java("ColumnToFreePile").nameExpression
	}
	  val semanticType: Type = 'Move('ColumnToFreePile, 'ClassName)
	}
	@combinator object FreePileToFreePile {
		def apply: NameExpr = {
			Java("FreePileToFreePile").nameExpression
	}
	  val semanticType: Type = 'Move('FreePileToFreePile, 'ClassName)
	}
	
	@combinator object PotentialPileToColumnMoveObject extends PotentialMove ('FreePileToColumn)
	@combinator object PotentialColumnToFreePileMoveObject extends PotentialMoveOneCardFromStack ('ColumnToFreePile)
	@combinator object PotentialColumnToHomePileMoveObject extends PotentialMoveOneCardFromStack ('ColumnToHomePile)
	@combinator object PotentialFreePileToHomePileMoveObject extends PotentialMove ('FreePileToHomePile)
	@combinator object PotentialFreePileToFreePileMoveObject extends PotentialMove ('FreePileToFreePile)

	@combinator object PotentialStackMoveFree {
		def apply(): NameExpr = { Java("Column").nameExpression() }
		val semanticType: Type = 'Move('ColumnToFreePile, 'TypeConstruct)
	}
	@combinator object PotentialStackMoveHome {
		def apply(): NameExpr = { Java("Column").nameExpression() }
		val semanticType: Type = 'Move('ColumnToHomePile, 'TypeConstruct)
	}
	
	@combinator object PotentialFreeCellPileDraggingVariable {
		def apply(): NameExpr = { Java("movingCard").nameExpression() }
		val semanticType: Type = 'Move('FreePileToColumn, 'DraggingCardVariableName)
	}
  @combinator object PotentialColumnToHomePileDraggingVariable {
		def apply(): NameExpr = { Java("movingColumn").nameExpression() }
		val semanticType: Type = 'Move('ColumnToHomePile, 'DraggingCardVariableName)
	}
  @combinator object PotentialFreePileToHomePileDraggingVariable {
		def apply(): NameExpr = { Java("movingCard").nameExpression() }
		val semanticType: Type = 'Move('FreePileToHomePile, 'DraggingCardVariableName)
	}
  @combinator object PotentialColumnToFreePileDraggingVariable {
		def apply(): NameExpr = { Java("movingColumn").nameExpression() }
		val semanticType: Type = 'Move('ColumnToFreePile, 'DraggingCardVariableName)
	}
  @combinator object PotentialFreePileToFreePileDraggingVariable {
		def apply(): NameExpr = { Java("movingCard").nameExpression() }
		val semanticType: Type = 'Move('FreePileToFreePile, 'DraggingCardVariableName)
	}
	
	//	val semanticType: Type =
	//      'RootPackage =>:
	//      'Move(semanticMoveNameType, 'ClassName) =>:
	//      'Move(semanticMoveNameType, 'DraggingCardVariableName) =>:
	//      'Move(semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)

	@combinator object PileToColumnMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.piletocolumn.java.PileToColumnMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('FreePileToColumn, 'ClassName) =>: 'Move('FreePileToColumn, 'HelperMethods)
	}
 @combinator object ColumnToHomePileMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.columntohomepile.java.ColumnToHomePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ColumnToHomePile, 'ClassName) =>: 'Move('ColumnToHomePile, 'HelperMethods)
	}
  @combinator object FreePileToHomePileMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.freecelltohomepile.java.FreePileToHomePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('FreePileToHomePile, 'ClassName) =>: 'Move('FreePileToHomePile, 'HelperMethods)
	}
  @combinator object ColumnToFreePileMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.columntofreepile.java.ColumnToFreePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ColumnToFreePile, 'ClassName) =>: 'Move('ColumnToFreePile, 'HelperMethods)
	}
  @combinator object FreePileToFreePileMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.freepiletofreepile.java.FreePileToFreePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('FreePileToFreePile, 'ClassName) =>: 'Move('FreePileToFreePile, 'HelperMethods)
	}

	@combinator object PileToColumnMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.add(movingCard);").statements()
		}
		val semanticType: Type = 'Move('FreePileToColumn, 'DoStatements)
	}
	@combinator object ColumnToHomePileMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.push(movingColumn);").statements()
		}
		val semanticType: Type = 'Move('ColumnToHomePile, 'DoStatements)
	}
  @combinator object FreePileToHomePileMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.add(movingCard);").statements()
		}
		val semanticType: Type = 'Move('FreePileToHomePile, 'DoStatements)
	}
  @combinator object ColumnToFreePileMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.push(movingColumn);").statements()
		}
		val semanticType: Type = 'Move('ColumnToFreePile, 'DoStatements)
	}
  @combinator object FreePileToFreePileMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.add(movingCard);").statements()
		}
		val semanticType: Type = 'Move('FreePileToFreePile, 'DoStatements)
	}

	@combinator object PileToColumnMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""source.add(destination.get());""").statements()
		}
		val semanticType: Type = 'Move('FreePileToColumn, 'UndoStatements)
	}
	@combinator object ColumnToHomePileMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""source.add(destination.get());""").statements()
		}
		val semanticType: Type = 'Move('ColumnToHomePile, 'UndoStatements)
	}
	@combinator object FreePileToHomePileMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""source.add(destination.get());""").statements()
		}
		val semanticType: Type = 'Move('FreePileToHomePile, 'UndoStatements)
	}
	@combinator object ColumnToFreePileMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""source.add(destination.get());""").statements()
		}
		val semanticType: Type = 'Move('ColumnToFreePile, 'UndoStatements)
	}
	@combinator object FreePileToFreePileMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""source.add(destination.get());""").statements()
		}
		val semanticType: Type = 'Move('FreePileToFreePile, 'UndoStatements)
	}
	
	@combinator object PileToColumnValid {
		def apply(): Seq[Statement] = {
				moves.piletocolumn.java.PileToColumnValid.render().statements()
		}
		val semanticType: Type = 'Move('FreePileToColumn, 'CheckValidStatements)
	}
	@combinator object ColumnToHomePileValid {
		def apply(): Seq[Statement] = {
				moves.columntohomepile.java.ColumnToHomePileValid.render().statements()
		}
		val semanticType: Type = 'Move('ColumnToHomePile, 'CheckValidStatements)
	}
	@combinator object FreePileToHomePileValid {
		def apply(): Seq[Statement] = {
				moves.freecelltohomepile.java.FreePileToHomePileValid.render().statements()
		}
		val semanticType: Type = 'Move('FreePileToHomePile, 'CheckValidStatements)
	}
  @combinator object ColumnToFreePileValid {
		def apply(): Seq[Statement] = {
				moves.columntofreepile.java.ColumnToFreePileValid.render().statements()
		}
		val semanticType: Type = 'Move('ColumnToFreePile, 'CheckValidStatements)
	}
  @combinator object FreePileToFreePileValid {
		def apply(): Seq[Statement] = {
				moves.freepiletofreepile.java.FreePileToFreePileValid.render().statements()
		}
		val semanticType: Type = 'Move('FreePileToFreePile, 'CheckValidStatements)
	}
}