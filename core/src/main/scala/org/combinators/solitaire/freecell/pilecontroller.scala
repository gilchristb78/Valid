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
     }
   
     // Each move has a source and a target. The SOURCE is the locus
     // for the PRESS while the TARGET is the locus for the RELEASE.
     updated = updated
       .addCombinator (new IgnorePressedHandler('Pile, 'HomePile))
       .addCombinator (new IgnoreClickedHandler('Pile, 'HomePile))
       .addCombinator (new SingleCardMoveHandler('FreePile))
       .addCombinator (new IgnoreClickedHandler('Pile, 'FreePile))
       .addCombinator (new IgnoreClickedHandler('Column, 'Column))  // FCC

   // get all types from the various containers (somehow)
   updated = updated
           .addCombinator (new PileController('HomePile))
           .addCombinator (new PileController('FreePile))
           .addCombinator (new ColumnController('Column))

   // these identify the controller names
   updated = updated
           .addCombinator (new ControllerNaming('Pile, 'FreePile, "FreeCell"))
           .addCombinator (new ControllerNaming('Pile, 'HomePile, "Home"))
           .addCombinator (new ControllerNaming('Column, 'Column, "FreeCell"))   
   updated
  }

class ControllerNaming(typ:Symbol, subType:Symbol, ident:String) {
   def apply(): SimpleName = Java(ident).simpleName()
   val semanticType: Type = typ (subType, 'ClassName) 
}

/**
 * When a Press can be ignored, use this 
 */
class IgnorePressedHandler(widgetType:Symbol, source:Symbol) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""$ignoreWidgetVariableName = true;""").statements()
    }

    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        widgetType (source, 'Pressed) :&: 'NonEmptySeq
  }

/** 
 * When a single card is being removed from the top card of a pile.
 */
class SingleCardMoveHandler(source:Symbol) {
  def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
		 |Pile srcPile = (Pile) src.getModelElement();
		 |
		 |// Return in the case that the pile clicked on is empty
		 |if (srcPile.count() == 0) {
        	 |  return;
		 |}
		 |$widgetVariableName = src.getCardViewForTopCard(me);
		 |if ($widgetVariableName == null) {
		 |  return;
		 |}""".stripMargin).statements()
  }

  val semanticType: Type =
    'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
      'Pile (source, 'Pressed) :&: 'NonEmptySeq
}

class IgnoreClickedHandler(widgetType:Symbol, source:Symbol) {
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
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
      case column: ColumnMove     => 
         Java(s"""|destination.select(numInColumn);
                  |source.push(destination.getSelected());""".stripMargin)
            .statements()
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


