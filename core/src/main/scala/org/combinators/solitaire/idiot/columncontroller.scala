package org.combinators.solitaire.idiot

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.body.{BodyDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait ColumnController extends shared.Controller {

	// column move designated combinators
	@combinator object ColumnControllerDef extends ColumnController ('IdiotColumn)

	@combinator object IdiotColumn {
		def apply(): NameExpr = {
				Java("Idiot").nameExpression()
		}
		val semanticType: Type = 'Column('IdiotColumn, 'ClassName)
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
				moves.java.ColumnPressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
				  //Seq.empty
		}
		val semanticType: Type =
				'Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
					'Column('IdiotColumn, 'Pressed) :&: 'NonEmptySeq
	}

	@combinator object ColumnClickedHandler {
		def apply(pkg:NameExpr, name:NameExpr): Seq[Statement] = {
				moves.java.ColumnClicked.render(pkg, name).statements()
		     //Seq.empty
		}
		val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Column('IdiotColumn, 'Clicked) :&: 'NonEmptySeq
	}

	// release must take into account both FROMPILE and FROMCOLUMN events.
	@combinator object ColumnReleasedHandler {
		def apply(rootPackage: NameExpr): Seq[Statement] = {
				moves.java.ColumnReleased.render(rootPackage).statements()
		}
		val semanticType: Type =
				'RootPackage =>: 'Column('IdiotColumn, 'Released) :&: 'NonEmptySeq
	}

}