package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait Controller extends Base {

  class ColumnController(columnNameType: Type) {
    def apply(rootPackage: Name,
      columnDesignate: SimpleName,
      nameOfTheGame: SimpleName,
      columnMouseClicked: Seq[Statement],
      columnMouseReleased: Seq[Statement],
      columnMousePressed: (SimpleName, SimpleName) => Seq[Statement]): CompilationUnit = {

      shared.controller.java.ColumnController.render(
        RootPackage = rootPackage,
        ColumnDesignate = columnDesignate,
        NameOfTheGame = nameOfTheGame,
        AutoMoves = Seq.empty,
        ColumnMouseClicked = columnMouseClicked,
        ColumnMousePressed = columnMousePressed,
        ColumnMouseReleased = columnMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Column (columnNameType, 'ClassName) =>:
        'NameOfTheGame =>:
        'Column (columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Column (columnNameType, 'Released) =>: // no longer need ... :&: 'NonEmptySeq (I think)....
        ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Column (columnNameType, 'Pressed) :&: 'NonEmptySeq) =>:
        'Controller (columnNameType)
  }


  class PileController(pileNameType: Type) {
    def apply(rootPackage: Name,
      pileDesignate: SimpleName,
      nameOfTheGame: SimpleName,
      pileMouseClicked: Seq[Statement],
      pileMouseReleased: Seq[Statement],
      pileMousePressed: (SimpleName, SimpleName) => Seq[Statement]): CompilationUnit = {
      shared.controller.java.PileController.render(
        RootPackage = rootPackage,
        PileDesignate = pileDesignate,
        NameOfTheGame = nameOfTheGame,
        PileMouseClicked = pileMouseClicked,
        PileMousePressed = pileMousePressed,
        PileMouseReleased = pileMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Pile (pileNameType, 'ClassName) =>:
        'NameOfTheGame =>:
        'Pile (pileNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Pile (pileNameType, 'Released) =>:
        ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Pile (pileNameType, 'Pressed) :&: 'NonEmptySeq) =>:
        'Controller (pileNameType)
  }

  @combinator object DeckController {
    def apply(rootPackage: Name,
      nameOfTheGame: SimpleName,
      deckMousePressed: Seq[Statement]): CompilationUnit = {
      shared.controller.java.DeckController.render(
        RootPackage = rootPackage,
        NameOfTheGame = nameOfTheGame,
        DeckMousePressed = deckMousePressed
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'Deck ('Pressed) =>: 'Controller ('Deck)
  }


  // generative classes for each of the required elements
  class NameDef(moveNameType: Type, moveElementDescriptor: Type, value: String) {
    def apply(): SimpleName = Java(value).simpleName()
    val semanticType: Type = 'MoveElement (moveNameType, moveElementDescriptor)
  }

  class ClassNameDef(moveNameType: Type, value: String) extends NameDef(moveNameType, 'ClassName, value)
  class MovableElementNameDef(moveNameType: Type, value: String) extends NameDef(moveNameType, 'MovableElementName, value)
  class SourceWidgetNameDef(moveNameType: Type, value: String) extends NameDef(moveNameType, 'SourceWidgetName, value)
  class TargetWidgetNameDef(moveNameType: Type, value: String) extends NameDef(moveNameType, 'TargetWidgetName, value)

  // this provides just the sequence of statements....
  class MoveWidgetToWidgetStatements(moveNameType: Type) {
    def apply(rootPackage: Name,
      theMove: SimpleName,
      movingWidgetName: SimpleName,
      sourceWidgetName: SimpleName,
      targetWidgetName: SimpleName): Seq[Statement] = {

      shared.controller.java.MoveWidgetToWidgetStatements.render(
        RootPackage = rootPackage,
        TheMove = theMove,
        MovingWidgetName = movingWidgetName,
        SourceWidgetName = sourceWidgetName,
        TargetWidgetName = targetWidgetName
      ).statements()
    }
    val semanticType: Type =
      'RootPackage =>:
        'MoveElement (moveNameType, 'ClassName) =>:
        'MoveElement (moveNameType, 'MovableElementName) =>:
        'MoveElement (moveNameType, 'SourceWidgetName) =>:
        'MoveElement (moveNameType, 'TargetWidgetName) =>:
        'MoveWidget (moveNameType)
  }
}
