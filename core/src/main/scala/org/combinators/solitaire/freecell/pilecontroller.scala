package org.combinators.solitaire.freecell
import com.github.javaparser.ast.CompilationUnit

// name clash
import com.github.javaparser.ast.`type`.{Type => JType}

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
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
import org.combinators.generic

import domain._
import domain.constraints._
import domain.moves._


trait PileController extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> PileController dynamic combinators.")

      // helper function for dealing with domain-specific mapping
      def mapString(s:String):String = {
         if (s == "HomePile") {
	   "Pile"
         } else if (s == "FreePile") {
           "Pile"
         } else {
           s
         }
      }
      
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

       updated = updated
           .addCombinator(new SourceWidgetNameDef(moveSymbol, 
                                                    mapString(srcBase)))
           .addCombinator(new TargetWidgetNameDef(moveSymbol,
                                                    mapString(tgtBase)))

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

        // potential move structure
        move match {
          case single: SingleCardMove => {
             updated = updated
               .addCombinator (new PotentialMove(moveSymbol))
          }
          case column: ColumnMove     => {
             updated = updated
               .addCombinator (new PotentialMoveOneCardFromStack(moveSymbol))
          }
        }
     }
   
     // Each move has a source and a target. The SOURCE is the locus
     // for the PRESS while the TARGET is the locus for the RELEASE.
     // These are handling the PRESS events...
     updated = updated
       .addCombinator (new IgnorePressedHandler('Pile, 'HomePile))
       .addCombinator (new IgnoreClickedHandler('Pile, 'HomePile))
       .addCombinator (new SingleCardMoveHandler('FreePile))
       .addCombinator (new IgnoreClickedHandler('Pile, 'FreePile))
       .addCombinator (new IgnoreClickedHandler('Column, 'Column))  

   // get all types from the various containers (somehow). TO BE DONE
   updated = updated
       .addCombinator (new PileController('HomePile))
       .addCombinator (new PileController('FreePile))
       .addCombinator (new ColumnController('Column))

   // Potential moves clarify structure (by type not instance). FIX ME
   // FIX ME FIX ME FIX ME
   updated = updated
       .addCombinator (new PotentialTypeConstructGen('ColumnToColumn))
       .addCombinator (new PotentialTypeConstructGen('ColumnToFreePile))
       .addCombinator (new PotentialTypeConstructGen('ColumnToHomePile))

   // these identify the controller names
   updated = updated
       .addCombinator (new ControllerNaming('Pile, 'FreePile, "FreeCell"))
       .addCombinator (new ControllerNaming('Pile, 'HomePile, "Home"))
       .addCombinator (new ControllerNaming('Column, 'Column, "FreeCell"))   

   // identify all unique pairs and be sure to generate handler for 
   // these cases. NOTE: TAKE FROM RULES NOT FROM S since that doesn't
   // have the proper instantiations of the elements inside
   var handler_map:Map[Object,List[Move]] = Map()
   val inner_rules_it = s.getRules.iterator
   while (inner_rules_it.hasNext()) {
      val inner_move = inner_rules_it.next()

      val tgtBase = inner_move.getTargetContainer

//      val srcBase = inner_move.getSourceContainer
//      val srcElementBase = inner_move.getSource.getClass().getSimpleName()
//      val tgtElementBase = inner_move.getTarget.getClass().getSimpleName()
      
//      val mappedElement =  mapString(tgtElementBase)     // take care of 'HomePile -> 'Pile

//      val moveString = srcElementBase + "To" + tgtElementBase
//      val moveSymbol = Symbol(moveString)

     // make sure has value
     if (!handler_map.contains(tgtBase)) {
         handler_map += (tgtBase -> List(inner_move))
     } else {
         val old:List[Move] = handler_map(tgtBase)
         val newList:List[Move] = old :+ inner_move
         handler_map -= tgtBase
         handler_map += (tgtBase -> newList)
     }
   }

   handler_map.keys.foreach{ k =>  
         print( "Key = " + k )
         println(" Value = " + handler_map(k))
   }


   // NOW, goal is to convert into the following fixed examples
   updated = updated
      .addCombinator (new IfBlock('GuardColumnView, 'MoveWidget('ColumnToColumn), 'Combined1))
      .addCombinator (new IfBlock('GuardCardView, 'MoveWidget('FreePileToColumn), 'Combined2))
      .addCombinator (new StatementCombiner('Combined1, 'Combined2, 'Combined3))

   // Note: this removes the automove capability, but that is not yet in domain model
   updated = updated
      .addCombinator (new StatementConverter('Combined3, 'Column ('Column, 'Released)))

   updated = updated
      .addCombinator (new IfBlock('GuardColumnView, 'MoveWidget('ColumnToFreePile), 'Combined4))
      .addCombinator (new IfBlock('GuardCardView, 'MoveWidget('FreePileToFreePile), 'Combined5))
      .addCombinator (new StatementCombiner ('Combined4, 'Combined5, 'Combined6))
      .addCombinator (new StatementConverter('Combined6, 'Pile('FreePile, 'Released)))

   updated = updated
      .addCombinator (new IfBlock('GuardColumnView, 'MoveWidget('ColumnToHomePile), 'Combined7))
      .addCombinator (new IfBlock('GuardCardView, 'MoveWidget('FreePileToHomePile), 'Combined8))
      .addCombinator (new StatementCombiner ('Combined7, 'Combined8, 'Combined9))
      .addCombinator (new StatementConverter('Combined9, 'Pile('HomePile, 'Released)))

   
   // FINAL FINAL will be 'Column ('Column, 'Released))
   // convert as needed StatementConverter
  
   updated
  }

class ControllerNaming(typ:Symbol, subType:Symbol, ident:String) {
   def apply(): SimpleName = Java(ident).simpleName()
   val semanticType: Type = typ (subType, 'ClassName) 
}

class ReleaseHandlerDef(typ:Symbol, entity:Symbol, stmts:Seq[Statement]) {
   def apply(): Seq[Statement] = stmts

   val semanticType: Type = typ (entity, 'Released) :&: 'NonEmptySeq
}


// Guards to ensure statements execute only for ColumnView (multiCard move)
@combinator object ColumnViewCheck {
  def apply: Expression = Java("w instanceof ColumnView").expression()
  val semanticType: Type = 'GuardColumnView
}

// Guards to ensure statements execute only for CardView (singleCard move)
@combinator object CardViewCheck {
  def apply: Expression = Java("w instanceof CardView").expression()
  val semanticType: Type = 'GuardCardView
}

class GuardGenerator(m:Move) {
  def toCode(): Symbol  = {
    m match {
      case single: SingleCardMove => 'GuardCardView
      case column: ColumnMove     => 'GuardColumnView
    }
  }
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

// Note: while I can have code within the apply() method, the semanticType
// is static, so that must be passed in as is. These clarify that a
// potential moveOneCardFromStack is still a Column Type.
class PotentialTypeConstructGen(constructor:Constructor) {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move (constructor, 'TypeConstruct)
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

}


