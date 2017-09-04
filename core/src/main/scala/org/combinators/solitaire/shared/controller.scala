package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import _root_.java.util.UUID
import org.combinators.generic
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controller extends Base with shared.Moves with generic.JavaIdioms  {

  // shared logic to process rules as needed for Solitaire extensions
  // Note: this creates Move classes for each of the moves that are
  // defined in either the presses, drags, or clicks sets 
  def createMoveClasses[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
     var updated = gamma
     var combined = ListBuffer[Move]()
     val it1 = s.getRules.drags
     while (it1.hasNext()) {
        combined += it1.next()
     }
     val it2 = s.getRules.presses
     while (it2.hasNext()) {
        combined += it2.next()
     }
     val it3 = s.getRules.clicks
     while (it3.hasNext()) {
        combined += it3.next()
     }

     val rules_1 = combined.iterator
     while (rules_1.hasNext) {
       val move = rules_1.next()
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

          // undo & do generation
          println ("    -- " + moveSymbol + " defined")
          updated = updated
             .addCombinator(new ClassNameDef(moveSymbol, moveString))
             .addCombinator(new ClassNameGenerator(moveSymbol, moveString))
             .addCombinator(new UndoGenerator(move, 'Move (moveSymbol, 'UndoStatements)))
             .addCombinator(new DoGenerator(move, 'Move (moveSymbol, 'DoStatements)))
             .addCombinator(new MoveHelper(move, new SimpleName(moveString), moveSymbol))
             .addCombinator(new StatementCombinator (move.getConstraint,
                             'Move (moveSymbol, 'CheckValidStatements)))

          move match {
            case single : SingleCardMove => {
                updated = updated.addCombinator(new SolitaireMove(moveSymbol))
            }
            case column : ColumnMove => {
                updated = updated.addCombinator(new SolitaireMove(moveSymbol))
            }
            case deck : DeckDealMove => {
                updated = updated.addCombinator(new MultiMove(moveSymbol))
            } 
	    case move : ResetDeckMove => {
                updated = updated.addCombinator(new MultiMove(moveSymbol))
            }
          }
        }
     }

   updated
}


  // shared logic to process rules as needed for Drag Moves
  def createDragLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
   var updated = gamma

   // HACK: NEEDS TO BE REMOVED
   // helper function for dealing with domain-specific mapping; that is, 

   val rules_it = s.getRules.drags
   while (rules_it.hasNext()) {
       val move = rules_it.next()
       val srcBase = move.getSource.getClass().getSimpleName()
       val tgtBase = move.getTarget.getClass().getSimpleName()
       val movable = move.getMovableElement.getClass().getSimpleName()

       val moveString = srcBase + "To" + tgtBase
       val moveSymbol = Symbol(moveString)

       // capture information about the source and target of each move
       updated = updated
           .addCombinator(new SourceWidgetNameDef(moveSymbol, srcBase))
           .addCombinator(new TargetWidgetNameDef(moveSymbol, tgtBase))

       // Each move is defined as follows:
       updated = updated
          .addCombinator(new MoveWidgetToWidgetStatements(moveSymbol))
          .addCombinator(new MovableElementNameDef(moveSymbol, movable))

       // potential moves must resolve dragging variables 
       updated = updated
          .addCombinator(new PotentialDraggingVariableGenerator (move,
                                'Move (moveSymbol, 'DraggingCardVariableName)))

      // potential move structure varies based on kind of move: not
      // yet dealing with DeckDealMove...
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

     updated
}

def generateMoveLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
  var updated = gamma

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

   // NOTE:This only is used to deal with release events. Note that in FreeCell
   // all events are drag events.

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
      val typ = Symbol(originalTarget)     
      val item = typ (Symbol(originalTarget), 'Released)
      val cmp = 'Column ('Column, 'Released)
      val isSame = (cmp == item)
      print ("try item:" + item + "," + lastID.get + ",comp=" + isSame + ".\n")
      updated = updated
         .addCombinator (new StatementConverter(lastID.get, item))
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

      case deck: ResetDeckMove => Seq.empty

      case deck: DeckDealMove => Java(s"""
                |for (Stack s : destinations) {
                |  source.add(s.get());
                |}""".stripMargin).statements()

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

      case deck: ResetDeckMove => Java(s"""
		|// Note destinations contain the stacks that are to
		|// be reformed into a single deck.
		|for (Stack s : destinations) {
		|  while (!s.empty()) {
		|	deck.add(s.get());
		|  }
		|}""".stripMargin).statements()

      case deck: DeckDealMove => Java(s"""
                |for (Stack s : destinations) {
                |  s.add (source.get());
                |}""".stripMargin).statements()

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

        case deck : DeckDealMove => Seq.empty

	case deck : ResetDeckMove => Seq.empty

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

class WidgetController(elementType: Type, symbol:Symbol) {
    def apply(rootPackage: Name,
      designate: SimpleName,
      nameOfTheGame: SimpleName,
      mouseClicked: Seq[Statement],
      mouseReleased: Seq[Statement],
      mousePressed: (SimpleName, SimpleName) => Seq[Statement]): CompilationUnit = {

      shared.controller.java.Controller.render(
        RootPackage = rootPackage,
        Designate = new SimpleName(elementType.toString),
        NameOfTheGame = nameOfTheGame,
        AutoMoves = Seq.empty,
        MouseClicked = mouseClicked,
        MousePressed = mousePressed,
        MouseReleased = mouseReleased
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>:
        symbol (elementType, 'ClassName) =>:
        'NameOfTheGame =>:
        symbol (elementType, 'Clicked) :&: 'NonEmptySeq =>:
        symbol (elementType, 'Released) =>: // no longer need ... :&: 'NonEmptySeq (I think)....
        ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: symbol (elementType, 'Pressed) :&: 'NonEmptySeq) =>:
        'Controller (elementType)
  }

/******************************
  class toRemoveColumnController(columnNameType: Type) {
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


  class toRemovePileController(pileNameType: Type) {
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

******************************/

/********************
  class WidgetControllerJustPress(nameType: Type, symbol: Symbol) {
    def apply(rootPackage: Name,
      nameOfTheGame: SimpleName,
      mousePressed: Seq[Statement]): CompilationUnit = {
      shared.controller.java.DeckController.render(
        RootPackage = rootPackage,
        NameOfTheGame = nameOfTheGame,
        DeckMousePressed = mousePressed
      ).compilationUnit()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: symbol ('Pressed) =>: 'Controller (nameType)
  }
************************/

  class DeckController(deckNameType: Type) {
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
      'RootPackage =>: 'NameOfTheGame =>: 'Deck ('Pressed) =>: 'Controller (deckNameType)
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

class IgnoreClickedHandler(widgetType:Symbol, source:Symbol) {
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
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

@combinator object CardViewCheck {
  def apply: Expression = Java("w instanceof CardView").expression()
  val semanticType: Type = 'GuardCardView
}


}
