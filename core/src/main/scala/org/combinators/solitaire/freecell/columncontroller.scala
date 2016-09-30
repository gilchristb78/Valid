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

trait ColumnController extends shared.Controller  {

	// column move designated combinators
	@combinator object ColumnControllerDef extends ColumnController ('FreeCellColumn)

	@combinator object FreeCellColumn {
		def apply(): NameExpr = {
				Java("FreeCell").nameExpression()
		}
		val semanticType: Type = 'Column('FreeCellColumn, 'ClassName)
	}

	//	    val semanticType: Type =
	//      'RootPackage =>:
	//        'Column(columnNameType, 'ClassName) =>:
	//        'NameOfTheGame =>:
	//        'Column(columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
	//        'Column(columnNameType, 'Released) :&: 'NonEmptySeq =>:
	//        'Column(columnNameType, 'Pressed) :&: 'NonEmptySeq =>:
	//        'Controller(columnNameType)


	@combinator object ColumnPressedHandler {
		def apply(): (NameExpr, NameExpr) => Seq[Statement] = {
				(widgetVariableName: NameExpr, ignoreWidgetVariableName: NameExpr) =>
				controller.column.java.ColumnPressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
		}
		val semanticType: Type =
				'Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
					'Column('FreeCellColumn, 'Pressed) :&: 'NonEmptySeq
	}

	@combinator object ColumnClickedHandler {
		def apply(): Seq[Statement] = {
				Seq.empty
		}
		val semanticType: Type = 'Column('FreeCellColumn, 'Clicked) :&: 'NonEmptySeq
	}

	// both moves are release-able on Columns.
	@combinator object ColumnToColumnStatements extends MoveWidgetToWidgetStatements ('ColumnToColumn)
	@combinator object PileToColumnStatements extends MoveWidgetToWidgetStatements ('FreePileToColumn)


	@combinator object CCN extends ClassNameDef('ColumnToColumn, "FreeCellColumnToColumn")
	@combinator object CCM extends MovableElementNameDef('ColumnToColumn, "Column")
	@combinator object CCS extends SourceWidgetNameDef('ColumnToColumn, "Column")
	@combinator object CCT extends TargetWidgetNameDef('ColumnToColumn, "Column")

	@combinator object PCN extends ClassNameDef('FreePileToColumn, "FreePileToColumn")
	@combinator object PCM extends MovableElementNameDef('FreePileToColumn, "Card")
	@combinator object PCS extends SourceWidgetNameDef('FreePileToColumn, "Pile")
	@combinator object PCT extends TargetWidgetNameDef('FreePileToColumn, "Column")

	// val semanticType: Type =
	//      'RootPackage =>:
	//      'MoveElement(moveNameType, 'ClassName) =>:
	//      'MoveElement(moveNameType, 'MovableElementName) =>:
	//      'MoveElement(moveNameType, 'SourceWidgetName) =>:
	//      'MoveElement(moveNameType, 'TargetWidgetName) =>:
	//      'MoveWidget(moveNameType)

	// release must take into account both FROMPILE and FROMCOLUMN events.
	@combinator object ColumnReleasedHandler {
		def apply(fromColumn:Seq[Statement], fromPile:Seq[Statement]): Seq[Statement] = {
				Java("""
						// Column moving to Column on FreeCell tableau
						if (w instanceof ColumnView) {
						""" + fromColumn.mkString("\n") + """;
						}
						if (w instanceof CardView) {
						""" + fromPile.mkString("\n") + """
						}  
						""").statements();

		}
		val semanticType: Type =
				'MoveWidget('ColumnToColumn) =>: 'MoveWidget('FreePileToColumn) =>: 'Column('FreeCellColumn, 'Released) :&: 'NonEmptySeq
	}

}