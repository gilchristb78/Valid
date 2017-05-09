package org.combinators.solitaire.freecell

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait PileController extends shared.Controller {

  // column move designated combinators
  @combinator object FreePileControllerDef extends PileController('FreePile)

  @combinator object FreeCellPile {
    def apply(): SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = 'Pile ('FreePile, 'ClassName)
  }

  // column move designated combinators
  @combinator object HomeControllerDef extends PileController('HomePile)

  @combinator object HomePile {
    def apply(): SimpleName = Java("Home").simpleName()
    val semanticType: Type = 'Pile ('HomePile, 'ClassName)
  }

  //   val semanticType: Type =
  //      'RootPackage =>:
  //        'Pile(pileNameType, 'ClassName) =>:
  //        'NameOfTheGame =>:
  //        'Pile(pileNameType, 'Clicked) :&: 'NonEmptySeq =>:
  //        'Pile(pileNameType, 'Released) :&: 'NonEmptySeq =>:
  //        ('Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Pile(pileNameType, 'Pressed) :&: 'NonEmptySeq) =>:
  //        'Controller(pileNameType)
  //
  //	@combinator object ShortCutPile {
  //		def apply(n0: Seq[Statement]): CompilationUnit = {
  //				Java("public class A{}").compilationUnit()
  //		}
  //		val semanticType: Type =
  //				'MoveWidget('FreePileToFreePile) =>:
  //					'ShortCut
  //	}


  @combinator object PilePressedHandler {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        controller.pile.java.FreeCellPilePressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile ('FreePile, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object FreeCellPileClickedHandler {
    def apply(): Seq[Statement] = {
      Seq.empty
    }

    val semanticType: Type = 'Pile ('FreePile, 'Clicked) :&: 'NonEmptySeq
  }

  @combinator object HomePilePressedHandler {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        controller.pile.java.HomePilePressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile ('HomePile, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object HomePileClickedHandler {
    def apply(): Seq[Statement] = {
      Seq.empty
    }

    val semanticType: Type = 'Pile ('HomePile, 'Clicked) :&: 'NonEmptySeq
  }

  // both moves are release-able on Columns.
  @combinator object ColumnToFreePileStatements extends MoveWidgetToWidgetStatements('ColumnToFreePile)
  @combinator object ColumnToHomePileStatements extends MoveWidgetToWidgetStatements('ColumnToHomePile)
  @combinator object FreePileToColumnStatements extends MoveWidgetToWidgetStatements('FreePileToColumn)
  @combinator object FreePileToFreePileStatements extends MoveWidgetToWidgetStatements('FreePileToFreePile)
  @combinator object FreePileToHomePileStatements extends MoveWidgetToWidgetStatements('FreePileToHomePile)
  @combinator object CFPN extends ClassNameDef('ColumnToFreePile, "ColumnToFreePile")
  @combinator object CFPM extends MovableElementNameDef('ColumnToFreePile, "Column")
  @combinator object CFPS extends SourceWidgetNameDef('ColumnToFreePile, "Column")
  @combinator object CFPT extends TargetWidgetNameDef('ColumnToFreePile, "Pile")
  @combinator object CHPN extends ClassNameDef('ColumnToHomePile, "ColumnToHomePile")
  @combinator object CHPM extends MovableElementNameDef('ColumnToHomePile, "Column")
  @combinator object CHPS extends SourceWidgetNameDef('ColumnToHomePile, "Column")
  @combinator object CHPT extends TargetWidgetNameDef('ColumnToHomePile, "Pile")
  @combinator object FPCN extends ClassNameDef('FreePileToColumn, "FreePileToColumn")
  @combinator object FPCM extends MovableElementNameDef('FreePileToColumn, "Card")
  @combinator object FPCS extends SourceWidgetNameDef('FreePileToColumn, "Pile")
  @combinator object FPCT extends TargetWidgetNameDef('FreePileToColumn, "Column")
  @combinator object FPFPN extends ClassNameDef('FreePileToFreePile, "FreePileToFreePile")
  @combinator object FPFPM extends MovableElementNameDef('FreePileToFreePile, "Card")
  @combinator object FPFPS extends SourceWidgetNameDef('FreePileToFreePile, "Pile")
  @combinator object FPFPT extends TargetWidgetNameDef('FreePileToFreePile, "Pile")
  @combinator object FPHPN extends ClassNameDef('FreePileToHomePile, "FreePileToHomePile")
  @combinator object FPHPM extends MovableElementNameDef('FreePileToHomePile, "Card")
  @combinator object FPHPS extends SourceWidgetNameDef('FreePileToHomePile, "Pile")
  @combinator object FPHPT extends TargetWidgetNameDef('FreePileToHomePile, "Pile")

  // val semanticType: Type =
  //      'RootPackage =>:
  //      'MoveElement(moveNameType, 'ClassName) =>:
  //      'MoveElement(moveNameType, 'MovableElementName) =>:
  //      'MoveElement(moveNameType, 'SourceWidgetName) =>:
  //      'MoveElement(moveNameType, 'TargetWidgetName) =>:
  //      'MoveWidget(moveNameType)

  // release must take into account both FROMPILE and FROMCOLUMN events.
  class ReleaseHandler(columnMoveType: Type, pileMoveType: Type, pileType: Type) {
    def apply(fromColumn: Seq[Statement], fromPile: Seq[Statement]): Seq[Statement] = {
      Java(
        s"""
           |// Column moving to Column on FreeCell tableau
           |if (w instanceof ColumnView) {
           |  ${fromColumn.mkString("\n")}
           |}
           |if (w instanceof CardView) {
           |  ${fromPile.mkString("\n")}
           |}
           """.stripMargin).statements()
    }

    val semanticType: Type =
      'MoveWidget (columnMoveType) =>:
        'MoveWidget (pileMoveType) =>:
        'Pile (pileType, 'Released) :&: 'NonEmptySeq
  }
  @combinator object FreeCellPileReleasedHandler extends ReleaseHandler('ColumnToFreePile, 'FreePileToFreePile, 'FreePile)
  @combinator object HomePileReleasedHandler extends ReleaseHandler('ColumnToHomePile, 'FreePileToHomePile, 'HomePile)
}