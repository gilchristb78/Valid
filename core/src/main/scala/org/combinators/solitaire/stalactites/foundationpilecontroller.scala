package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.body.{BodyDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait FoundationPileController extends shared.Controller  {

	// column move designated combinators
	@combinator object FoundationPileControllerDef extends PileController ('FoundationPile)
	
//	'RootPackage =>:
//        'Pile(pileNameType, 'Clicked) :&: 'NonEmptySeq =>:
//        'Pile(pileNameType, 'Released) :&: 'NonEmptySeq =>:
//        ('Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Pile(pileNameType, 'Pressed) :&: 'NonEmptySeq) =>:
//        'Controller(pileNameType)
	
	@combinator object FoundationPile {
    def apply(): NameExpr = {
      Java("FoundationPile").nameExpression()
    }
    val semanticType: Type = 'Pile('FoundationPile, 'ClassName)
  }

	// ignore presses on the foundation pile
  @combinator object PilePressedHandler {
    def apply(): (NameExpr, NameExpr) => Seq[Statement] = {
      (widgetVariableName: NameExpr, ignoreWidgetVariableName: NameExpr) =>

      Java(ignoreWidgetVariableName.toString() + " = false;").statements()
    }
    val semanticType: Type =
      'Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile('FoundationPile, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object PiledClickedHandler {
    def apply(rootPackage: NameExpr, nameOfTheGame: NameExpr): Seq[Statement] = {
     Seq.empty
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'Pile('FoundationPile, 'Clicked) :&: 'NonEmptySeq
  }

  // move widget statements: 'MoveWidget(moveNameType)
  @combinator object ColumnToFoundationPileStatements extends MoveWidgetToWidgetStatements ('ColumnToFoundationPile)

	@combinator object CFPN extends ClassNameDef('ColumnToFoundationPile, "ColumnToFoundation")
	@combinator object CFPS extends SourceWidgetNameDef('ColumnToFoundationPile, "Column")
	@combinator object CFPM extends MovableElementNameDef('ColumnToFoundationPile, "Column")
	@combinator object CFPT extends TargetWidgetNameDef('ColumnToFoundationPile, "Pile")
  
  @combinator object FoundationPileReleasedHandler {
		def apply(fromColumn:Seq[Statement]): Seq[Statement] = {
				Java("""
						// Column moving to FoundationPile
						if (w instanceof ColumnView) {
						""" + fromColumn.mkString("\n") + """;
						}
						""").statements();

		}
		val semanticType: Type =
				'MoveWidget('ColumnToFoundationPile) =>: 'Pile('FoundationPile, 'Released) :&: 'NonEmptySeq
	}
  
	
}