package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared


trait Controllers {
  class ColumnController(columnNameType: Type) {
    def apply(rootPackage: NameExpr,
      columnDesignate: NameExpr,
      nameOfTheGame: NameExpr,
      columnMouseClicked: Seq[Statement],
      columnMouseReleased: Seq[Statement],
      columnMousePressed: Seq[Statement]): CompilationUnit = {
      shared.controller.java.ColumnController.render(
        RootPackage = rootPackage,
        ColumnDesignate = columnDesignate,
        NameOfTheGame = nameOfTheGame,
        ColumnMouseClicked =  columnMouseClicked,
        ColumnMousePressed = columnMousePressed,
        ColumnMouseReleased =  columnMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        columnNameType =>:
        'NameOfTheGame =>:
        'Column(columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Column(columnNameType, 'Released) :&: 'NonEmptySeq =>:
        'Column(columnNameType, 'Pressed) :&: 'NonEmptySeq =>:
        'Controller(columnNameType)
  }

  class PileController(pileNameType: Type) {
    def apply(rootPackage: NameExpr,
      pileDesignate: NameExpr,
      nameOfTheGame: NameExpr,
      pileMouseClicked: Seq[Statement],
      pileMouseReleased: Seq[Statement],
      pileMousePressed: (NameExpr, NameExpr) => Seq[Statement]): CompilationUnit = {
      shared.controller.java.PileController.render(
        RootPackage = rootPackage,
        PileDesignate = pileDesignate,
        NameOfTheGame = nameOfTheGame,
        PileMouseClicked =  pileMouseClicked,
        PileMousePressed = pileMousePressed,
        PileMouseReleased =  pileMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        pileNameType =>:
        'NameOfTheGame =>:
        'Pile(pileNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Pile(pileNameType, 'Released) :&: 'NonEmptySeq =>:
        ('Pair('WidgetName, 'IgnoreWidgetVariableName) =>: 'Pile(pileNameType, 'Pressed) :&: 'NonEmptySeq) =>:
        'Controller(pileNameType)
  }

  @combinator object DeckController {
    def apply(rootPackage: NameExpr,
      nameOfTheGame: NameExpr,
      deckMousePressed: Seq[Statement]): CompilationUnit = {
      shared.controller.java.DeckController.render(
        RootPackage = rootPackage,
        NameOfTheGame = nameOfTheGame,
        DeckMousePressed = deckMousePressed
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'NameOfTheGame =>:
        'Deck('Pressed) =>:
        'Controller('Deck)
  }

  class MoveWidgetToWidgetController(moveNameType: Type) {
    def apply(rootPackage: NameExpr,
      theMove: NameExpr,
      movingWidgetName: NameExpr,
      sourceWidgetName: NameExpr,
      targetWidgetName: NameExpr): CompilationUnit = {
      shared.controller.java.MoveWidgetToWidget.render(
        RootPackage = rootPackage,
        TheMove = theMove,
        MovingWidgetName = movingWidgetName,
        SourceWidgetName = sourceWidgetName,
        TargetWidgetName = targetWidgetName
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
      moveNameType =>:
      'MovableElementName =>:
      'SourceWidgetName =>:
      'TargetWidgetName =>:
      'Controller(moveNameType)
  }
}
