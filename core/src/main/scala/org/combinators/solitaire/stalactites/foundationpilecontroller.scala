package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared

trait FoundationPileController extends shared.Controller with generic.JavaIdioms {
//
//  // column move designated combinators
//  @combinator object FoundationPileControllerDef extends WidgetController('FoundationPile)
//
//  /** For FoundationPile controller, this declares name of the class. */
//  @combinator object FoundationPile {
//    def apply(): SimpleName = Java("FoundationPile").simpleName()
//    val semanticType: Type = 'Pile ('FoundationPile, 'ClassName)
//  }
//
//  // ignore presses on the foundation pile
//  @combinator object FoundationPilePressedHandler {
//    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
//      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
//        Java(s"$ignoreWidgetVariableName = false;").statements()
//    }
//    val semanticType: Type =
//      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
//        'Pile ('FoundationPile, 'Pressed) :&: 'NonEmptySeq
//  }
//
//  @combinator object FoundationPileClickedHandler {
//    def apply(rootPackage: Name, nameOfTheGame: SimpleName): Seq[Statement] = Seq.empty
//    val semanticType: Type =
//      'RootPackage =>: 'NameOfTheGame =>: 'Pile ('FoundationPile, 'Clicked) :&: 'NonEmptySeq
//  }
//
//  // move widget statements: 'MoveWidget(moveNameType)
//  @combinator object ColumnToFoundationPileStatements extends MoveWidgetToWidgetStatements('ColumnToFoundationPile)
//
//  @combinator object CFPN extends ClassNameDef('ColumnToFoundationPile, "ColumnToFoundation")
//  @combinator object CFPS extends SourceWidgetNameDef('ColumnToFoundationPile, "Column")
//  @combinator object CFPM extends MovableElementNameDef('ColumnToFoundationPile, "Column")
//  @combinator object CFPT extends TargetWidgetNameDef('ColumnToFoundationPile, "Pile")
//
//  // move widget statements: 'MoveWidget(moveNameType)
//  @combinator object ReservePileToFoundationPileStatements extends MoveWidgetToWidgetStatements('ReservePileToFoundationPile)
//
//  @combinator object RPFPN extends ClassNameDef('ReservePileToFoundationPile, "ReservePileToFoundation")
//  @combinator object RPFPS extends SourceWidgetNameDef('ReservePileToFoundationPile, "Pile")
//  @combinator object RPFPM extends MovableElementNameDef('ReservePileToFoundationPile, "Card")
//  @combinator object RPFPT extends TargetWidgetNameDef('ReservePileToFoundationPile, "Pile")
//
//
//  // w instanceof ColumnView
//  @combinator object ColumnViewCheckFP {
//    def apply: Expression = Java("w instanceof ColumnView").expression()
//    val semanticType: Type = 'GuardColumnViewFP
//  }
//
//  @combinator object CardViewCheckFP {
//    def apply: Expression = Java("w instanceof CardView").expression()
//    val semanticType: Type = 'GuardCardViewFP
//  }
//
//  @combinator object IfStart1 extends IfBlock('GuardColumnViewFP, 'MoveWidget ('ColumnToFoundationPile), 'CombinedFP1)
//
//  @combinator object IfStart2 extends IfBlock('GuardCardViewFP, 'MoveWidget ('ReservePileToFoundationPile), 'CombinedFP2)
//
//  @combinator object CombinedHandlers extends StatementCombiner('CombinedFP1, 'CombinedFP2, 'Pile ('FoundationPile, 'Released))

  //	// seeming hack since the pair 'Pile('FoundationPile, 'Released) can't be valid Constructor for StatementCombiner?
  //	@combinator object Final {
  //	   def apply(in:Seq[Statement]):Seq[Statement] = {
  //	     in
  //	   }
  //
  //	   val semanticType: Type = 'CombinedZZ =>: 'Pile('FoundationPile, 'Released)
  //	}


  //	// seeming hack since the pair 'Pile('FoundationPile, 'Released) can't be valid Constructor for StatementCombiner?
  //	@combinator object FinalSet {
  //	   def apply():Seq[Statement] = {
  //	     Seq.empty
  //	   }
  //
  //	   val semanticType: Type =  'Pile('FoundationPile, 'Released)
  //	}

}
