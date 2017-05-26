package org.combinators.solitaire.freecell

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared


// 'FreeCellColumn
// change to just 'Column

trait ColumnController extends shared.Controller with generic.JavaIdioms {

  @combinator object ColumnPressedHandler {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        controller.column.java.ColumnPressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }
    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Column ('FreeCellColumn, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object ColumnClickedHandler {
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = 'Column ('FreeCellColumn, 'Clicked) :&: 'NonEmptySeq
  }

  // w instanceof ColumnView
  @combinator object ColumnViewCheck {
    def apply: Expression = Java("w instanceof ColumnView").expression()
    val semanticType: Type = 'GuardColumnView
  }

  @combinator object CardViewCheck {
    def apply: Expression = Java("w instanceof CardView").expression()
    val semanticType: Type = 'GuardCardView
  }

  @combinator object IfStart1 extends IfBlock('GuardColumnView, 'MoveWidget ('ColumnToColumn), 'Combined1)

  @combinator object IfStart2 extends IfBlock('GuardCardView, 'MoveWidget ('FreePileToColumn), 'Combined2)

  @combinator object CombinedHandlers extends StatementCombiner('Combined1, 'Combined2, 'Combined3)

  // Column('FreeCellColumn, 'Released))
  @combinator object CombinedHandlers2 extends StatementCombiner('Combined3, 'AutoMoveColumn, 'Column ('FreeCellColumn, 'Released))

  //	@combinator object CombinedHandlers extends StatementCombiner('MoveWidget('ColumnToColumn), 'MoveWidget('FreePileToColumn), 'Combined1)
  //
  //	@combinator object CombinedHandlers2 extends StatementCombiner('IfCombined1, 'AutoMoveColumn, 'Column('FreeCellColumn, 'Released))

  @combinator object AutoMoveSequence {
    def apply(pkgName: Name, name: SimpleName): Seq[Statement] = {
      Java(s"""(($pkgName.$name)theGame).tryAutoMoves();""").statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'AutoMoveColumn
  }


}
