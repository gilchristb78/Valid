package org.combinators.solitaire.freecell
import com.github.javaparser.ast.CompilationUnit

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration

import domain._
import domain.constraints._
import domain.moves._


trait PileController extends shared.Controller with shared.Moves {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> PileController dynamic combinators.")

  // val semanticType: Type =
  //      'RootPackage =>:
  //      'MoveElement(moveNameType, 'ClassName) =>:
  //      'MoveElement(moveNameType, 'MovableElementName) =>:
  //      'MoveElement(moveNameType, 'SourceWidgetName) =>:
  //      'MoveElement(moveNameType, 'TargetWidgetName) =>:
  //      'MoveWidget(moveNameType)
     val rules_it = s.getRules.iterator
     while (rules_it.hasNext()) {
       val move = rules_it.next()
       val srcBase = move.getSource.getClass().getSimpleName()
       val tgtBase = move.getTarget.getClass().getSimpleName()
       val movable = move.getMovableElement.getClass().getSimpleName()

       val moveString = srcBase + "To" + tgtBase
       val moveSymbol = Symbol(moveString)
       println (moveSymbol + ":" + move + ":" + movable)

       // have to "map" HomePile => Pile, and FreePile => Pile.
       if (srcBase == "HomePile" || srcBase == "FreePile") {
         println ("     with Pile")
 	   updated = updated
              .addCombinator(new SourceWidgetNameDef(moveSymbol, "Pile"))
       } else {
         println ("     with " + srcBase)
           updated = updated
              .addCombinator(new SourceWidgetNameDef(moveSymbol, srcBase))
       }

       if (tgtBase == "HomePile" || tgtBase == "FreePile") {
         println ("     with Pile")
           updated = updated
              .addCombinator(new TargetWidgetNameDef(moveSymbol, "Pile"))
       } else {
         println ("     with " + srcBase)
           updated = updated
              .addCombinator(new TargetWidgetNameDef(moveSymbol, tgtBase))
       }

       // Each move is defined as follows:
       updated = updated
          .addCombinator(new MoveWidgetToWidgetStatements(moveSymbol))
          .addCombinator(new ClassNameDef(moveSymbol, moveString))
          .addCombinator(new MovableElementNameDef(moveSymbol, movable))

       // undo & do generation
       updated = updated
          .addCombinator(new ClassNameGenerator(moveSymbol, moveString))
          .addCombinator(new UndoGenerator(move, 
				'Move (moveSymbol, 'UndoStatements)))
          .addCombinator(new DoGenerator(move,
				'Move (moveSymbol, 'DoStatements)))
          .addCombinator(new PotentialDraggingVariableGenerator (move,
                                'Move (moveSymbol, 'DraggingCardVariableName)))
          .addCombinator(new MoveHelper(move, new SimpleName(moveString), moveSymbol))

          .addCombinator(new SolitaireMove(moveSymbol))
// @combinator object FreeCellColumnToColumnMoveObject extends Move('ColumnToColumn)


     }
    
   updated
  }


class ClassNameGenerator(moveSymbol:Symbol, name:String) {
    def apply: SimpleName = Java(s"""$name""").simpleName()
    val semanticType: Type = 'Move (moveSymbol, 'ClassName)
  }

class PotentialDraggingVariableGenerator(m:Move, constructor:Constructor) {
  def apply(): SimpleName = {
    m match {
      case single: SingleCardMove => Java(s"""movingCard""").simpleName()
      case column: ColumnMove     => Java(s"""movingColumn""").simpleName()
    }
  }
    val semanticType: Type = constructor
  }

/** When given a Move (SingleCardMove or ColumnMove) ascribes proper Undo. */
/** Same code, just by coincidence. */
class UndoGenerator(m:Move, constructor:Constructor) {
    def apply(): Seq[Statement] = {
    m match {
      case single: SingleCardMove => Java(s"""source.add(destination.get());""").statements()
      case column: ColumnMove     => Java(s"""source.add(destination.get());""").statements()
    }
  }
    val semanticType: Type = constructor
  }

/** When given a Move (SingleCardMove or ColumnMove) ascribes proper Do. */
/** Same code, just by coincidence. */
class DoGenerator(m:Move, constructor:Constructor) {
    def apply(): Seq[Statement] = {
    m match {
      case single: SingleCardMove => Java(s"""destination.add(movingCard);""").statements()
      case column: ColumnMove     => Java(s"""destination.push(movingColumn);""").statements()
    }
  }
    val semanticType: Type = constructor
  }

/** Every move class needs a constructor with helper fields. */
class MoveHelper(m:Move, name:SimpleName, moveSymbol: Symbol) {
  def apply() : Seq[BodyDeclaration[_]] = {
      m match {
	case single : SingleCardMove => 
          Java(s"""|Card movingCard;
                   |public $name(Stack from, Card card, Stack to) {
		   |  this(from, to);
		   |  this.movingCard = card;
 		   |}""".stripMargin).classBodyDeclarations()
	case column : ColumnMove     =>
          Java(s"""|Column movingColumn;
		   |int numInColumn;
	 	   |public $name(Stack from, Column cards, Stack to) {
		   |  this(from, to);
		   |  this.movingColumn = cards;
		   |  this.numInColumn = cards.count();
		   |}""".stripMargin).classBodyDeclarations()
    }
  } 

 val semanticType: Type = 'Move (moveSymbol, 'HelperMethods)
}

// column move designated combinators
  @combinator object ColumnControllerDef extends ColumnController('FreeCellColumn)

  @combinator object FreeCellColumn {
    def apply(): SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = 'Column ('FreeCellColumn, 'ClassName)
  }


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
    def apply(): Seq[Statement] = Seq.empty
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
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = 'Pile ('HomePile, 'Clicked) :&: 'NonEmptySeq
  }


  // release must take into account both FROMPILE and FROMCOLUMN events.
   class ReleaseHandler(columnMoveType: Symbol, pileMoveType: Symbol, pileType: Symbol) {
     def apply(fromColumn: Seq[Statement], fromPile: Seq[Statement]): Seq[Statement] = {
       Java(
         s"""
            |// Column moving to Column on FreeCell tableau
            |if (w instanceof ColumnView) {
            |     // ${columnMoveType}.mkString(",")
            |  ${fromColumn.mkString("\n")}
            |}
            |if (w instanceof CardView) {
            |     // ${pileMoveType}.mkString(",")
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


