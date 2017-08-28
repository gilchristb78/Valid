package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
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
import org.combinators.generic
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controller extends Base with shared.Moves {

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
      // domain makes division between "HomePile" and "FreePile" but the
      // common element within KS will be Pile.
//      def mapString(s:String):String = {
//         if (s == "HomePile") {
//           "Pile"
//         } else if (s == "FreePile") {
//           "Pile"
//         } else {
//           s
//         }
//      }


   val rules_it = s.getRules.drags
   while (rules_it.hasNext()) {
       val move = rules_it.next()
       val srcBase = move.getSource.getClass().getSimpleName()
       val tgtBase = move.getTarget.getClass().getSimpleName()
       val movable = move.getMovableElement.getClass().getSimpleName()

       val moveString = srcBase + "To" + tgtBase
       val moveSymbol = Symbol(moveString)
       //println (moveSymbol + ":" + move + ":" + movable)

       // create code for the move validation, based on the constraints with each move
        updated = updated
          .addCombinator (new StatementCombinator (move.getConstraint,
                          'Move (moveSymbol, 'CheckValidStatements)))
 
       // need mapString to deal with types in Domain that do not
       // translate into types in the Framework. I.e., "HomePile" -> Pile
       // HACK: WHAT TO DO? 
       updated = updated
           .addCombinator(new SourceWidgetNameDef(moveSymbol, srcBase))
//                                                    mapString(srcBase)))
           .addCombinator(new TargetWidgetNameDef(moveSymbol, tgtBase))
//                                                    mapString(tgtBase)))

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

      // potential move structure varies based on kind of move
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
}
