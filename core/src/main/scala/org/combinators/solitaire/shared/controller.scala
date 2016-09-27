package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared

import de.tu_dortmund.cs.ls14.twirl.Java

trait Controller {
  class ColumnController(columnNameType: Type) {
    def apply(rootPackage: NameExpr,
      columnDesignate: NameExpr,
      nameOfTheGame: NameExpr,
      autoMoves : Seq[MethodDeclaration],
      columnMouseClicked: Seq[Statement],
      columnMouseReleased: Seq[Statement],
      columnMousePressed: (NameExpr, NameExpr) => Seq[Statement]): CompilationUnit = {
      
      // ((Idiot) theGame).tryAutoMoves();
      val statements = Java("(("+ rootPackage.getName() + "." + nameOfTheGame.getName() + ") theGame).tryAutoMoves();").statements()
      
      shared.controller.java.ColumnController.render(
        RootPackage = rootPackage,
        ColumnDesignate = columnDesignate,
        NameOfTheGame = nameOfTheGame,
        AutoMoves = statements,
        ColumnMouseClicked =  columnMouseClicked,
        ColumnMousePressed = columnMousePressed,
        ColumnMouseReleased =  columnMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Column(columnNameType, 'ClassName) =>:
        'NameOfTheGame =>:
        'Column(columnNameType, 'AutoMovesAvailable)=>: 
        'Column(columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Column(columnNameType, 'Released) :&: 'NonEmptySeq =>:
        ('Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Column(columnNameType, 'Pressed) :&: 'NonEmptySeq) =>:
        'Controller(columnNameType)
  }

  class ColumnControllerNoAutoMove(columnNameType: Type) {
    def apply(rootPackage: NameExpr,
      columnDesignate: NameExpr,
      nameOfTheGame: NameExpr,
      columnMouseClicked: Seq[Statement],
      columnMouseReleased: Seq[Statement],
      columnMousePressed: (NameExpr, NameExpr) => Seq[Statement]): CompilationUnit = {
      
      // ((Idiot) theGame).tryAutoMoves();
      val statements = Java("(("+ rootPackage.getName() + "." + nameOfTheGame.getName() + ") theGame).tryAutoMoves();").statements()
       
      shared.controller.java.ColumnController.render(
        RootPackage = rootPackage,
        ColumnDesignate = columnDesignate,
        NameOfTheGame = nameOfTheGame,
        AutoMoves = Seq.empty,
        ColumnMouseClicked =  columnMouseClicked,
        ColumnMousePressed = columnMousePressed,
        ColumnMouseReleased =  columnMouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        'Column(columnNameType, 'ClassName) =>:
        'NameOfTheGame =>:
        'Column(columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Column(columnNameType, 'Released) :&: 'NonEmptySeq =>:
        ('Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Column(columnNameType, 'Pressed) :&: 'NonEmptySeq) =>:
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
        'Pile(pileNameType, 'ClassName) =>:
        'NameOfTheGame =>:
        'Pile(pileNameType, 'Clicked) :&: 'NonEmptySeq =>:
        'Pile(pileNameType, 'Released) :&: 'NonEmptySeq =>:
        ('Pair('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Pile(pileNameType, 'Pressed) :&: 'NonEmptySeq) =>:
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
      'RootPackage =>:  'NameOfTheGame =>: 'Deck('Pressed) =>: 'Controller('Deck)
  }

  
  
  // generative classes for each of the required elements
  class ClassNameDef(moveNameType:Type, value:String) {
		def apply(): NameExpr = {
				Java(value).nameExpression
		}
		val semanticType: Type = 'MoveElement(moveNameType, 'ClassName) 
	}
  
  // generative classes for each of the required elements
  class MovableElementNameDef(moveNameType:Type, value:String) {
		def apply(): NameExpr = {
				Java(value).nameExpression
		}
		val semanticType: Type = 'MoveElement(moveNameType, 'MovableElementName) 
	}
  
  // generative classes for each of the required elements
  class SourceWidgetNameDef(moveNameType:Type, value:String) {
		def apply(): NameExpr = {
				Java(value).nameExpression
		}
		val semanticType: Type = 'MoveElement(moveNameType, 'SourceWidgetName) 
	}
	
  // generative classes for each of the required elements
  class TargetWidgetNameDef(moveNameType:Type, value:String) {
		def apply(): NameExpr = {
				Java(value).nameExpression
		}
		val semanticType: Type = 'MoveElement(moveNameType, 'TargetWidgetName) 
	}
  
  // this provides just the sequence of statements....
  class MoveWidgetToWidgetStatements(moveNameType: Type) {
    def apply(rootPackage: NameExpr,
      theMove: NameExpr,
      movingWidgetName: NameExpr,
      sourceWidgetName: NameExpr,
      targetWidgetName: NameExpr): Seq[Statement] = {
      
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
      'MoveElement(moveNameType, 'ClassName) =>:
      'MoveElement(moveNameType, 'MovableElementName) =>:
      'MoveElement(moveNameType, 'SourceWidgetName) =>:
      'MoveElement(moveNameType, 'TargetWidgetName) =>:
      'MoveWidget(moveNameType)
  }
  
//  class MoveWidgetToWidgetController(moveNameType: Type) {
//    def apply(rootPackage: NameExpr,
//      theMove: NameExpr,
//      movingWidgetName: NameExpr,
//      sourceWidgetName: NameExpr,
//      targetWidgetName: NameExpr): CompilationUnit = {
//      shared.controller.java.MoveWidgetToWidget.render(
//        RootPackage = rootPackage,
//        TheMove = theMove,
//        MovingWidgetName = movingWidgetName,
//        SourceWidgetName = sourceWidgetName,
//        TargetWidgetName = targetWidgetName
//      ).compilationUnit()
//    }
//    val semanticType: Type =
//      'RootPackage =>:
//      moveNameType =>:
//      'MovableElementName =>:
//      'SourceWidgetName =>:
//      'TargetWidgetName =>:
//      'MoveWidget(moveNameType)
//  }
}
