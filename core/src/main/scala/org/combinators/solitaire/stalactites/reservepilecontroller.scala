package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.expr.{Expression, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared

trait ReservePileController extends shared.Controller with generic.JavaCodeIdioms {
//
//  // column move designated combinators
//  @combinator object ReservePileControllerDef extends WidgetController('ReservePile)
//
//  @combinator object ReservePile {
//    def apply(): SimpleName = Java("ReservePile").simpleName()
//    val semanticType: Type = 'Pile ('ReservePile, 'ClassName)
//  }
//
//  // ignore presses on the reserve pile (for now)...
//  @combinator object ReservePilePressedHandler {
//    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
//      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
//        controller.reservePile.java.ReservePilePressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
//    }
//    val semanticType: Type =
//      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
//        'Pile ('ReservePile, 'Pressed) :&: 'NonEmptySeq
//  }
//
//  @combinator object ReservePiledClickedHandler {
//    def apply(): Seq[Statement] = Seq.empty
//    val semanticType: Type = 'Pile ('ReservePile, 'Clicked) :&: 'NonEmptySeq
//  }
//
//  // move widget statements: 'MoveWidget(moveNameType)
//  @combinator object ColumnToReservePileStatements extends MoveWidgetToWidgetStatements('ColumnToReservePile)
//
//  @combinator object CRPN extends ClassNameDef('ColumnToReservePile, "ColumnToReserve")
//  @combinator object CRPS extends SourceWidgetNameDef('ColumnToReservePile, "Column")
//  @combinator object CRPM extends MovableElementNameDef('ColumnToReservePile, "Column")
//  @combinator object CRPT extends TargetWidgetNameDef('ColumnToReservePile, "Pile")
//
//  // move widget statements: 'MoveWidget(moveNameType)
//  @combinator object ReservePileToReservePileStatements extends MoveWidgetToWidgetStatements('ReservePileToReservePile)
//
//
//  @combinator object RPRPN extends ClassNameDef('ReservePileToReservePile, "ReservePileToReserve")
//  @combinator object RPRPS extends SourceWidgetNameDef('ReservePileToReservePile, "Pile")
//  @combinator object RPRPM extends MovableElementNameDef('ReservePileToReservePile, "Card")
//  @combinator object RPRPT extends TargetWidgetNameDef('ReservePileToReservePile, "Pile")
//
//
//  // w instanceof ColumnView
//  @combinator object ColumnViewCheckRP {
//    def apply: Expression = Java("w instanceof ColumnView").expression()
//    val semanticType: Type = 'GuardColumnViewRP
//  }
//
//  @combinator object CardViewCheckRP {
//    def apply: Expression = Java("w instanceof CardView").expression()
//    val semanticType: Type = 'GuardCardViewRP
//  }
//
//  @combinator object IfStartRP1 extends IfBlock('GuardColumnViewRP, 'MoveWidget ('ColumnToReservePile), 'CombinedRP1)
//  @combinator object IfStartRP2 extends IfBlock('GuardCardViewRP, 'MoveWidget ('ReservePileToReservePile), 'CombinedRP2)
//  @combinator object CombinedHandlersRP extends StatementCombiner('CombinedRP1, 'CombinedRP2, 'Pile ('ReservePile, 'Released))
//
//  //
//  //
//  //
//  //  @combinator object ReservePileReleasedHandler {
//  //		def apply(fromColumn:Seq[Statement]): Seq[Statement] = {
//  //				Java("""
//  //						// Column moving to ReservePile
//  //						if (w instanceof ColumnView) {
//  //						""" + fromColumn.mkString("\n") + """;
//  //						}
//  //						""").statements();
//  //
//  //		}
//  //		val semanticType: Type =
//  //				'MoveWidget('ColumnToReservePile) =>: 'Pile('ReservePile, 'Released) :&: 'NonEmptySeq
//  //	}
//

}
