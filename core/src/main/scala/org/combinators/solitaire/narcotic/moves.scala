package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait Moves extends shared.Controller with shared.Moves {
  @combinator object RemoveCard {
    def apply(rootPackage: NameExpr, numPiles: Expression): CompilationUnit = {
      moves.java.MoveRemoveCard.render(rootPackage, numPiles).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'NumPiles =>: 'MoveRemoveCards
  }

  @combinator object PileMove {
    def apply(rootPackage: NameExpr, pileCondition: Seq[Statement]): CompilationUnit = {
      moves.java.PileMove.render(rootPackage, pileCondition).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'PileToPileCondition =>: 'PileMove
  }

  @combinator object PileToPileCondition {
    def apply(): Seq[Statement] = {
      moves.java.PileToPileCondition.render().statements()
    }
    val semanticType: Type = 'PileToPileCondition
  }

  @combinator object DeckPressedHandler {
    def apply(rootPackage: NameExpr, nameOfTheGame: NameExpr): Seq[Statement] = {
      moves.java.DeckPressed.render(rootPackage, nameOfTheGame).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Deck('Pressed)
  }

  @combinator object NarcoticPileController extends PileController('NarcoticPile)

  @combinator object NarcoticPile {
    def apply(): NameExpr = {
      Java("Narcotic").nameExpression()
    }
    val semanticType: Type = 'Pile('NarcoticPile, 'ClassName)
  }

  @combinator object PilePressedHandler {
    def apply(): (NameExpr, NameExpr) => Seq[Statement] = {
      (widgetVariableName: NameExpr, ignoreWidgetVariableName: NameExpr) =>
        moves.java.PilePressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }
    val semanticType: Type =
      'Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile('NarcoticPile, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object PiledClickedHandler {
    def apply(rootPackage: NameExpr, nameOfTheGame: NameExpr): Seq[Statement] = {
      moves.java.PileClicked.render(rootPackage, nameOfTheGame).statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'Pile('NarcoticPile, 'Clicked) :&: 'NonEmptySeq
  }

  @combinator object PileReleasedHandler {
    def apply(rootPackage: NameExpr): Seq[Statement] = {
      moves.java.PileReleased.render(rootPackage).statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'Pile('NarcoticPile, 'Released) :&: 'NonEmptySeq
  }
}