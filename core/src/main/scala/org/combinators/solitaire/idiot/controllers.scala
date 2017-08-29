package org.combinators.solitaire.idiot
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
import _root_.java.util.UUID
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {


  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> Idiot Controller dynamic combinators.")

     // structural 
     val ui = new UserInterface(s)

     val els_it = ui.controllers
     while (els_it.hasNext()) {
       val el = els_it.next()

       // Each of these controllers are expected in the game.
       if (el == "Deck") {
	 updated = updated.addCombinator (new DeckController(Symbol(el)))
       } else if (el == "Column") {
         updated = updated.addCombinator (new ColumnController(Symbol(el)))
       }
     }

     // not much to do, if no rules...
     if (s.getRules == null) {
       return updated
     }

     // All moves (regardless of press/drag/click) all have to define a class.
     // FIX ME FIX ME FIX ME
//     var combined = ListBuffer[Move]()
//     val it1 = s.getRules.drags
//     while (it1.hasNext()) {
 //       combined += it1.next()
//     }
//     val it2 = s.getRules.presses
//     while (it2.hasNext()) {
//        combined += it2.next()
//     }
//     val it3 = s.getRules.clicks
//     while (it3.hasNext()) {
//        combined += it3.next()
//     }
//
//     val rules_1 = combined.iterator
//     while (rules_1.hasNext) {
//       val move = rules_1.next()
//       println ("move:" + move.getSource)
//       val srcBase = move.getSource.getClass().getSimpleName()
//       val target = move.getTarget
//       if (target == null) {
//          println ("  empty target. Skip" + move);
//       } else {
//          val tgtBase = move.getTarget.getClass().getSimpleName()
//          val movable = move.getMovableElement.getClass().getSimpleName()
//
//          val moveString = srcBase + "To" + tgtBase
//          val moveSymbol = Symbol(moveString)
//
//          // undo & do generation
//          println ("    -- " + moveSymbol + " defined")
//          updated = updated
//             .addCombinator(new ClassNameDef(moveSymbol, moveString))
//             .addCombinator(new ClassNameGenerator(moveSymbol, moveString))
//             .addCombinator(new UndoGenerator(move, 'Move (moveSymbol, 'UndoStatements)))
//             .addCombinator(new DoGenerator(move, 'Move (moveSymbol, 'DoStatements)))
//             .addCombinator(new MoveHelper(move, new SimpleName(moveString), moveSymbol))
//             .addCombinator(new StatementCombinator (move.getConstraint,
//                             'Move (moveSymbol, 'CheckValidStatements)))
//
//	  move match {
//            case single : SingleCardMove => {
//                updated = updated.addCombinator(new SolitaireMove(moveSymbol))
//            }
//            case column : ColumnMove => {
 //               updated = updated.addCombinator(new SolitaireMove(moveSymbol))
 //           }
//            case deck : DeckDealMove => {
//                updated = updated.addCombinator(new MultiMove(moveSymbol))
//            }
//         }
//       }
//    }

     updated = createMoveClasses(updated, s)

     // this section attempts to construct code for all DRAG moves,
     // that is, moves from one source container to a target container.
     // Moves that do dot conform (press or click) are handled separately.
     val rules_it = s.getRules.drags
     while (rules_it.hasNext()) {
       val move = rules_it.next()
       println ("move:" + move.getSource)
       val srcBase = move.getSource.getClass().getSimpleName()
       val target = move.getTarget
       if (target == null) {
          println ("  empty target. Skip" + move);
       } else {

	  val tgtBase = move.getTarget.getClass().getSimpleName()
	  val movable = move.getMovableElement.getClass().getSimpleName()

	  val moveString = srcBase + "To" + tgtBase
	  val moveSymbol = Symbol(moveString)
	  println (moveSymbol + ":" + move + ":" + movable)

	  // create code for the move validation, based on the constraints with each move
//	  updated = updated
//	     .addCombinator (new StatementCombinator (move.getConstraint,
//	  	             'Move (moveSymbol, 'CheckValidStatements)))
	   
	  updated = updated
	     .addCombinator(new SourceWidgetNameDef(moveSymbol, srcBase)) 
	     .addCombinator(new TargetWidgetNameDef(moveSymbol, tgtBase))

	  // Each move is defined as follows:
	  updated = updated
	     .addCombinator(new MoveWidgetToWidgetStatements(moveSymbol))
//	     .addCombinator(new ClassNameDef(moveSymbol, moveString))
	     .addCombinator(new MovableElementNameDef(moveSymbol, movable))

	  // undo & do generation
	  updated = updated
//	     .addCombinator(new ClassNameGenerator(moveSymbol, moveString))
//	     .addCombinator(new UndoGenerator(move, 'Move (moveSymbol, 'UndoStatements)))
//             .addCombinator(new DoGenerator(move, 'Move (moveSymbol, 'DoStatements)))
	     .addCombinator(new PotentialDraggingVariableGenerator (move,
			    'Move (moveSymbol, 'DraggingCardVariableName)))
//	     .addCombinator(new MoveHelper(move, new SimpleName(moveString), moveSymbol))

	  // need to extract this from the domain model.
//	  move match {
//	    case single : SingleCardMove => {
//		updated = updated.addCombinator(new SolitaireMove(moveSymbol))
//	    }
//	    case column : ColumnMove => {
//		updated = updated.addCombinator(new SolitaireMove(moveSymbol))
//	    }
//	    case deck : DeckDealMove => {
//		updated = updated.addCombinator(new MultiMove(moveSymbol))
//	    }
//       }

	 // potential move structure varies based on kind of move
	 move match {
	  case single: SingleCardMove => {
	     updated = updated.addCombinator (new PotentialMove(moveSymbol))
 	  }
	  case column: ColumnMove     => {
	     updated = updated.addCombinator (new PotentialMoveOneCardFromStack(moveSymbol))
 	  }
	  case _ => {}
	}
       }
     }
  
     // NOTE : NEED TO CREATE UI-SPECIFIC MODEL FROM WHICH ALL ARE EXPORTED
     //
     // Source {Free, Column} x { Free, Column, Home }
     //  
     // Identify pre-move validation: by default (true).
     //     when click on column: check that potential column being extracted works
 
     // Each move has a source and a target. The SOURCE is the locus
     // for the PRESS while the TARGET is the locus for the RELEASE.
     // These are handling the PRESS events... SHOULD BE ABLE TO 
     // INFER THESE FROM THE AVAILABLE MOVES
     updated = updated
       .addCombinator (new SingleCardMoveHandler('Column))         // ONLY PILE PRESS
       .addCombinator (new DealToTableauHandler())
       .addCombinator (new TryRemoveCardHandler('Column, 'Column))
// DELETE. In IDIOT, CLICK on a column means seek a higher card.
//       .addCombinator (new IgnoreClickedHandler('Column, 'Column))


   // get all types from the various containers (somehow). FIX ME FIX ME
   //   updated = updated
   //       .addCombinator (new ColumnController('Column))

   // Potential moves clarify structure (by type not instance). FIX ME
   // FIX ME FIX ME FIX ME
   updated = updated
       .addCombinator (new PotentialTypeConstructGen('ColumnToColumn))

   // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
   updated = updated
       .addCombinator (new ControllerNaming('Column, 'Column, "Idiot"))   

   // identify all unique pairs and be sure to generate handler for 
   // these cases. NOTE: TAKE FROM RULES NOT FROM S since that doesn't
   // have the proper instantiations of the elements inside
   var drag_handler_map:Map[Container,List[Move]] = Map()
   val inner_rules_it = s.getRules.drags
   while (inner_rules_it.hasNext()) {
      val inner_move = inner_rules_it.next()

      val tgtBaseHolder = inner_move.getTargetContainer
      val srcBase = inner_move.getSourceContainer

      val tgtBase = tgtBaseHolder.get()
      // make sure has value
      if (!drag_handler_map.contains(tgtBase)) {
	 drag_handler_map += (tgtBase -> List(inner_move))
      } else {
	 val old:List[Move] = drag_handler_map(tgtBase)
	 val newList:List[Move] = old :+ inner_move
	 drag_handler_map -= tgtBase
	 drag_handler_map += (tgtBase -> newList)
     }
   }

   // key is Container, value is List of moves
   drag_handler_map.keys.foreach{ k =>  
     // print( "Key = " + k )   // key is Container
     // println(" Value = " + handler_map(k))   // value is List of movesa

      // iterate over moves in the handler_map(k)
      var lastID:Option[Symbol] = None
      drag_handler_map(k).foreach { m =>

        val srcBase = m.getSourceContainer
        val srcElementBase = m.getSource.getClass().getSimpleName()
        val tgtElementBase = m.getTarget.getClass().getSimpleName()
  
        val moveString = srcElementBase + "To" + tgtElementBase
        val curID = Symbol("ComponentOf-" + UUID.randomUUID().toString())

        val moveSymbol = Symbol(moveString)
        val viewType =
           m match {
              case singleMove : SingleCardMove => 'GuardCardView
              case colummMove : ColumnMove => 'GuardColumnView
              case dealMove : DeckDealMove => 'GuardCardView // HACK
           }
           
        updated = updated
           .addCombinator (new IfBlock(viewType, 'MoveWidget(moveSymbol), curID)) 

        if (lastID.nonEmpty) {  
           val subsequentID = Symbol("ComponentOf-" + UUID.randomUUID().toString())
           updated = updated
              .addCombinator (new StatementCombiner(lastID.get, curID, subsequentID))
       
           lastID = Some (subsequentID)
        } else {
           lastID = Some(curID)
        }
      }


      val originalTarget = k.types().next()
      val mappedElement = originalTarget
      val typ = Symbol(mappedElement) 
      val item = typ (Symbol(originalTarget), 'Released)
      updated = updated
         .addCombinator (new StatementConverter(lastID.get, item))
   }


// Make the parameter a Type and then it can be passed in.

//  class ReleaseHandlerDef(typ:Symbol, entity:Symbol, stmts:Seq[Statement]) {
//   def apply(): Seq[Statement] = stmts
//
//   val semanticType: Type = typ (entity, 'Released) :&: 'NonEmptySeq
//}

  // CASE STUDY: Add Automove logic at end of release handlers

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
      case deck: DeckDealMove     => 'GuardCardView    // HACK
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
 * When dealing card(s) from the stock to all elements in Tableau
 */
class DealToTableauHandler() {
 def apply():Seq[Statement] = {
        Java(s"""|m = new DeckToColumn(theGame.deck, theGame.fieldColumns);
                 |if (m.doMove(theGame)) {
		 |   theGame.pushMove(m);
                 |}""".stripMargin).statements()
  }

  val semanticType: Type = 'Deck ('Pressed) :&: 'NonEmptySeq
}

class TryRemoveCardHandler(widgetType:Symbol, source:Symbol) {
  def apply():Seq[Statement] = {
      Java(s"""|Column srcColumn = (Column) src.getModelElement();
	       |if (((org.combinators.solitaire.idiot.Idiot)theGame).isHigher(srcColumn)) {
	       |Move m = new RemoveSingleCard(srcColumn);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}
	       |}""".stripMargin).statements()
  }

  val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
}


/** 
 * When a single card is being removed from the top card of a pile.
 */
class SingleCardMoveHandler(source:Symbol) {
  def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
		 |Column srcColumn = (Column) src.getModelElement();
		 |
		 |// Return in the case that the pile clicked on is empty
		 |if (srcColumn.count() == 0) {
        	 |  return;
		 |}
		 |$widgetVariableName = src.getCardViewForTopCard(me);
		 |if ($widgetVariableName == null) {
		 |  return;
		 |}""".stripMargin).statements()
  }

 val semanticType: Type =
    'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
      'Column (source, 'Pressed) :&: 'NonEmptySeq
}

class IgnoreClickedHandler(widgetType:Symbol, source:Symbol) {
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
  }

//class ClassNameGenerator(moveSymbol:Symbol, name:String) {
//    def apply: SimpleName = Java(s"""$name""").simpleName()
//    val semanticType: Type = 'Move (moveSymbol, 'ClassName)
//  }

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
//class UndoGenerator(m:Move, constructor:Constructor) {
//    def apply(): Seq[Statement] = {
//    m match {
//      case single: SingleCardMove => Java(s"""source.add(destination.get());""").statements()
//
//      case deck: DeckDealMove => Java(s"""
//		|for (Stack s : destinations) {
//		|  source.add(s.get());
//		|}""".stripMargin).statements()
//
//      case column: ColumnMove     => 
//         Java(s"""|destination.select(numInColumn);
//                  |source.push(destination.getSelected());""".stripMargin)
//            .statements()
//    }
//  }
//    val semanticType: Type = constructor
//  }
//
/** When given a Move (SingleCardMove or ColumnMove) ascribes proper Do. */
/** Same code, just by coincidence. */
//class DoGenerator(m:Move, constructor:Constructor) {
//    def apply(): Seq[Statement] = {
//    m match {
//      case single: SingleCardMove => Java(s"""destination.add(movingCard);""").statements()
//     
//      case deck: DeckDealMove => Java(s"""
//		|for (Stack s : destinations) {
//		|  s.add (source.get());
//		|}""".stripMargin).statements()
//
//      case column: ColumnMove     => Java(s"""destination.push(movingColumn);""").statements()
//    }
//  }
//    val semanticType: Type = constructor
//  }

/** Every move class needs a constructor with helper fields. */
//class MoveHelper(m:Move, name:SimpleName, moveSymbol: Symbol) {
//  def apply() : Seq[BodyDeclaration[_]] = {
//      m match {
//	case single : SingleCardMove => 
//          Java(s"""|Card movingCard;
//                   |public $name(Stack from, Card card, Stack to) {
//		   |  this(from, to);
//		   |  this.movingCard = card;
// 		   |}""".stripMargin).classBodyDeclarations()
//
//        case deck : DeckDealMove => Seq.empty
//
//	case column : ColumnMove     =>
//          Java(s"""|Column movingColumn;
//		   |int numInColumn;
//	 	   |public $name(Stack from, Column cards, Stack to) {
//		   |  this(from, to);
//		   |  this.movingColumn = cards;
//		   |  this.numInColumn = cards.count();
//		   |}""".stripMargin).classBodyDeclarations()
//    }
//  } 
//
// val semanticType: Type = 'Move (moveSymbol, 'HelperMethods)
//}

}


