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

trait ColumnMoves extends shared.Moves {

//		val semanticType: Type =
//				'RootPackage =>:
//					'Move(semanticMoveNameType, 'ClassName) =>: 
//				  'Move(semanticMoveNameType, 'HelperMethods) =>:
//					'Move(semanticMoveNameType, 'DoStatements) =>:
//					'Move(semanticMoveNameType, 'UndoStatements) =>:
//					'Move(semanticMoveNameType, 'CheckValidStatements) =>:
//					'Move(semanticMoveNameType :&: 'GenericMove, 'CompleteMove)

	@combinator object FreeCellColumnToColumnMoveObject extends Move ('ColumnToColumn)

	@combinator object FreeCellColumnToColumn {
		def apply: NameExpr = {
			Java("FreeCellColumnToColumn").nameExpression
	}
	  val semanticType: Type = 'Move('ColumnToColumn, 'ClassName)
	}
	
	@combinator object PotentialColumnToColumnMoveObject extends PotentialMoveOneCardFromStack ('ColumnToColumn)

	@combinator object PotentialStackMoveColumn {
		def apply(): NameExpr = { Java("Column").nameExpression() }
		val semanticType: Type = 'Move('ColumnToColumn, 'TypeConstruct)
	}
	
	@combinator object PotentialColumnDraggingVariable {
		def apply(): NameExpr = {
				Java("movingColumn").nameExpression()
		}
		val semanticType: Type = 'Move('ColumnToColumn, 'DraggingCardVariableName)
	}

	//	val semanticType: Type =
	//      'RootPackage =>:
	//      'Move(semanticMoveNameType, 'ClassName) =>:
	//      'Move(semanticMoveNameType, 'DraggingCardVariableName) =>:
	//      'Move(semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)

	@combinator object ColumnToColumnMoveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.columntocolumn.java.ColumnToColumnMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ColumnToColumn, 'ClassName) =>: 'Move('ColumnToColumn, 'HelperMethods)
	}

	@combinator object ColumnToColumnMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.push(movingColumn);").statements()
		}
		val semanticType: Type = 'Move('ColumnToColumn, 'DoStatements)
	}

	@combinator object ColumnToColumnMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						destination.select(numInColumn);
						source.push(destination.getSelected());
						""").statements()
		}
		val semanticType: Type = 'Move('ColumnToColumn, 'UndoStatements)
	}

	@combinator object ColumnToColumnValid {
		def apply(pkg:NameExpr, name:NameExpr): Seq[Statement] = {
				moves.columntocolumn.java.ColumnToColumnValid.render(pkg, name).statements()
		}
		val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move('ColumnToColumn, 'CheckValidStatements)
	}

@combinator object ShortCut {
		def apply(n0: Seq[Statement], n1:NameExpr, n5: Seq[Statement]): CompilationUnit = {
			Java("public class A{}").compilationUnit()
	}
	  val semanticType: Type = 
	    
	        'Move('ColumnToColumn, 'UndoStatements) =>: 
	        'Move('ColumnToColumn, 'ClassName) =>: 
	        'Move('ColumnToColumn, 'CheckValidStatements) =>: 
	        'ShortCut
	}
	
	// to define this new class, don't we have to specify the 'FreCellColumnToColumn is a type of generic move?
	//	override val moveTaxonomy: Taxonomy =
	//    Taxonomy("GenericMove")
	//      .addSubtype("FreeCellColumnToColumn")
}