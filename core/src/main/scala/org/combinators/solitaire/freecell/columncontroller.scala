package org.combinators.solitaire.freecell

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared

trait ColumnController extends shared.Controller with generic.JavaIdioms {

  // column move designated combinators
  @combinator object ColumnControllerDef extends ColumnController('FreeCellColumn)

  @combinator object FreeCellColumn {
    def apply(): SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = 'Column ('FreeCellColumn, 'ClassName)
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

  // both moves are release-able on Columns.
  @combinator object ColumnToColumnStatements extends MoveWidgetToWidgetStatements('ColumnToColumn)
  @combinator object PileToColumnStatements extends MoveWidgetToWidgetStatements('FreePileToColumn)

  @combinator object CCN extends ClassNameDef('ColumnToColumn, "FreeCellColumnToColumn")
  @combinator object CCM extends MovableElementNameDef('ColumnToColumn, "Column")
  @combinator object CCS extends SourceWidgetNameDef('ColumnToColumn, "Column")
  @combinator object CCT extends TargetWidgetNameDef('ColumnToColumn, "Column")

  @combinator object PCN extends ClassNameDef('FreePileToColumn, "FreePileToColumn")
  @combinator object PCM extends MovableElementNameDef('FreePileToColumn, "Card")
  @combinator object PCS extends SourceWidgetNameDef('FreePileToColumn, "Pile")
  @combinator object PCT extends TargetWidgetNameDef('FreePileToColumn, "Column")

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