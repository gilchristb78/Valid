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

trait ColumnToPileMoves extends shared.Moves {
  
  
  @combinator object ColumnToFoundationPileMove extends Move('ColumnToFoundationPile)
  
  @combinator object ColumnToFoundationName {
		def apply: NameExpr = {
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
		val semanticType: Type = 'Move('ColumnToFoundationPile, 'DoStatements)
	}

	@combinator object ColumnToFoundationMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						destination.select(1);
						source.push(destination.getSelected());
						""").statements()
		}
		val semanticType: Type = 'Move('ColumnToFoundationPile, 'UndoStatements)
	}

}