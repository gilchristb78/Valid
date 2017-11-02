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
import _root_.java.util.UUID

import org.combinators.generic
import domain._
import domain.constraints.OrConstraint
import domain.moves._
import org.combinators.solitaire.shared.compilation.StatementCombinator

import scala.collection.JavaConverters._

trait Controller extends Base with shared.Moves with generic.JavaIdioms with SemanticTypes {

  // shared logic to process rules as needed for Solitaire extensions
  // Note: this creates Move classes for each of the moves that are
  // defined in either the presses, drags, or clicks sets 
  def createMoveClasses[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    val combined = s.getRules.drags.asScala ++ s.getRules.presses.asScala ++ s.getRules.clicks.asScala
    for (mv <- combined) {

      val moveString = mv.getName
      val moveSymbol = Symbol(moveString)


      // undo & do generation
      println ("    -- " + moveSymbol + " defined")
      updated = updated
        .addCombinator(new ClassNameDef(moveSymbol, moveString))
        .addCombinator(new ClassNameGenerator(moveSymbol, moveString))
        .addCombinator(new UndoGenerator(mv, moveSymbol))
        .addCombinator(new DoGenerator(mv, moveSymbol))
        .addCombinator(new MoveHelper(mv, new SimpleName(moveString), moveSymbol))
        .addCombinator(new StatementCombinator (mv.constraints(), moveSymbol))

      /**
        * A move typically contains a single source and a single destination. For some
        * moves, there are multiple destinations (typically a deal, or remove cards) and
        * that requires different combinator.
        */
      if (mv.isSingleDestination) {
        updated = updated.addCombinator(new SolitaireMove(moveSymbol))
      } else {
        updated = updated.addCombinator(new MultiMove(moveSymbol))
      }
    }

    updated
  }


  // shared logic to process rules as needed for Drag Moves
  def createDragLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    // HACK: NEEDS TO BE REMOVED
    // helper function for dealing with domain-specific mapping; that is,

    val rules_it = s.getRules.drags
    while (rules_it.hasNext) {
      val mv = rules_it.next()
      val srcBase = mv.getSource.getClass.getSimpleName
      val tgtBase = mv.getTarget.getClass.getSimpleName
      val movable = mv.getMovableElement.getClass.getSimpleName

      ///val moveString = srcBase + "To" + tgtBase
      val moveString = mv.getName
      val moveSymbol = Constructor(moveString)   // was Symbol... and had moveSymbol:Type


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
        .addCombinator(new PotentialDraggingVariableGenerator (mv, moveSymbol))

      // Dragging moves are either moving a single card or a single column. Must collect together
      // all possible 'pre' moves
      mv match {
        case _ : SingleCardMove =>
          updated = updated
              .addCombinator (new PotentialMoveSingleCard(moveSymbol))

       case _ : ColumnMove =>
          updated = updated
              .addCombinator (new PotentialMoveMultipleCards(moveSymbol))
      }
    }

    updated
  }

  def generateMoveLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    // identify all unique pairs and be sure to generate handler for
    // these cases. NOTE: TAKE FROM RULES NOT FROM S since that does not
    // have the proper instantiations of the elements inside
    var drag_handler_map:Map[Container,List[Move]] = Map()
    var press_handler_map:Map[Container,List[Move]] = Map()

    val inner_rules_it = s.getRules.drags
    while (inner_rules_it.hasNext) {
      val inner_move = inner_rules_it.next()

      // handle release events
      val tgtBaseHolder = inner_move.targetContainer
      val tgtBase = tgtBaseHolder.get

       if (!drag_handler_map.contains(tgtBase)) {
        drag_handler_map += (tgtBase -> List(inner_move))
      } else {
        val old:List[Move] = drag_handler_map(tgtBase)
        val newList:List[Move] = old :+ inner_move
        drag_handler_map -= tgtBase
        drag_handler_map += (tgtBase -> newList)
      }

      // handle press events
      val srcBase = inner_move.srcContainer

      if (!press_handler_map.contains(srcBase)) {
        press_handler_map += (srcBase -> List(inner_move))
      } else {
        val old:List[Move] = press_handler_map(srcBase)
        val newList:List[Move] = old :+ inner_move
        press_handler_map -= srcBase
        press_handler_map += (srcBase -> newList)
      }
    }

    // add press moves which are independent of drag
    val press_rules_it = s.getRules.presses()
    var haveNativePress = false
    while (press_rules_it.hasNext) {
      val inner_move = press_rules_it.next()

      haveNativePress = true
    }

    // find all source constraints for all moves and package together into single OrConstraint. If any move
    // is a ColumnMove (which means it is triggered by a press then drag) this block adds a ColumnMoveHandler
    // to properly allow some moves to be initiating of a drag.
    press_handler_map.keys.foreach { container =>
      val list: List[Move] = press_handler_map(container)

      // if any of the Moves is a ColumnMove, then we must create a pre-constraint filter for drags
      var columnMoves:List[ColumnMove] = List.empty
      var pressMoves:List[Move] = List.empty
      for (m <- list) {
        m match {
          case cm:ColumnMove => columnMoves = cm :: columnMoves
          case m:Move => pressMoves = m :: pressMoves       // any other moves go here
        }
      }

      if (columnMoves.nonEmpty) {
        val cons: List[Constraint] = list.map(x => x.sourceConstraint)
        val or: OrConstraint = new OrConstraint(cons: _*)

        val it: Iterator[String] = container.types.asScala
        while (it.hasNext) {
          val typeName: String = it.next
          val tpe = Constructor(typeName)

          // if there are any lingering press events (i.e., not all drag) then we need to somehow
          // combine these two properly. Detect by inference.
          val terminal = if (haveNativePress) {
            // up to domain-version controller's to combine together to make final press...
            controller(tpe, controller.dragStart)
          } else {
            controller(tpe, controller.pressed)
          }

          updated = updated
            .addCombinator(new ColumnMoveHandler(tpe, Java(typeName).simpleName(), or, terminal))
        }
      }
    }


    // key is Container, value is List of moves; this block deals with release events.
    drag_handler_map.keys.foreach{ k =>
      // iterate over moves in the handler_map(k)
      var lastID:Option[Constructor] = None
      drag_handler_map(k).foreach { m =>

        val moveString = m.getName
        val moveSymbol:Type = Symbol(moveString)

        val curID:Constructor = dynamic(Symbol(UUID.randomUUID().toString))

        val viewType =
          m match {
            case _ : SingleCardMove => press.card
            case _ : ColumnMove => press.column
          }

        println (moveSymbol + ":" + viewType)
        updated = updated
          .addCombinator (new IfBlock(viewType, widget(moveSymbol, complete), curID))

        if (lastID.nonEmpty) {
          val subsequentID:Constructor = dynamic(Symbol(UUID.randomUUID().toString))
          updated = updated
            .addCombinator (new StatementCombiner(lastID.get, curID, subsequentID))

          lastID = Some(subsequentID)
        } else {
          lastID = Some(curID)
        }
      }

      val originalTarget = k.types().next()
      val typ:Type = Symbol(originalTarget)

      val item = controller(typ, controller.released)
      updated = updated
        .addCombinator (new StatementConverter(lastID.get, item))
    }

    updated
  }

  // HACK
  // THESE SEEM LIKE NOT WORTH WRITING..
  // TODO: FIX ME
  class ClassNameGenerator(moveSymbol:Type, name:String) {
    def apply: SimpleName = Java(s"""$name""").simpleName()
    val semanticType: Type = move(moveSymbol, className)
  }

  class PotentialDraggingVariableGenerator(m:Move, moveSymbol:Type) {
    def apply(): SimpleName = {
      m match {
        case _ : SingleCardMove => Java(s"""movingCard""").simpleName()
        case _: ColumnMove     => Java(s"""movingColumn""").simpleName()
      }
    }
    val semanticType: Type = move(moveSymbol, move.draggingVariableCardName)
  }

  /** When given a Move (SingleCardMove or ColumnMove) ascribes proper Undo. */
  /** Same code, just by coincidence. */
  class UndoGenerator(m:Move, moveSymbol:Type) {
    def apply(): Seq[Statement] = {
      m match {
        case _ : FlipCardMove =>
          Java(s"""|Card c = source.get();
                   |c.setFaceUp (!c.isFaceUp());
                   |source.add(c);
                   |return true;""".stripMargin).statements()

        case _ : SingleCardMove => Java(s"""
                        |source.add(destination.get());
                        |return true;""".stripMargin).statements()

        // No means for undoing the reset of a deck.
        case _ : ResetDeckMove => Java(s"""|return false;""".stripMargin).statements()

        // reinsert the cards that had been removed into removedCards
        case _ : RemoveMultipleCardsMove =>
          Java(s"""|for (Stack s : destinations) {
                   |  s.add(removedCards.remove(0));
                   |}
                   |return true;""".stripMargin).statements()

        case _ : RemoveSingleCardMove =>
          Java(s"""|source.add(removedCard);
                   |return true;""".stripMargin).statements()

        case _ : DeckDealMove =>
          Java(s"""|for (Stack s : destinations) {
                   |  source.add(s.get());
                   |}
                   |return true;""".stripMargin).statements()

        case _ : ColumnMove  =>
          Java(s"""|destination.select(numInColumn);
                   |source.push(destination.getSelected());
                   |return true;""".stripMargin)
            .statements()
      }
    }
    val semanticType: Type = move(moveSymbol, move.undoStatements)
  }

  /** When given a Move (SingleCardMove or ColumnMove) ascribes proper Do. */
  /** Same code, just by coincidence. */
  class DoGenerator(m:Move, moveSymbol:Type) {
    def apply(): Seq[Statement] = {
      m match {
        case _ : FlipCardMove =>
          Java(s"""|Card c = source.get();
                   |c.setFaceUp (!c.isFaceUp());
                   |source.add(c);
                   |""".stripMargin).statements()

        case _ : SingleCardMove => Java(s"""destination.add(movingCard);""").statements()

        // remove cards and prent any attempt for undo.
        case _ : RemoveMultipleCardsMove =>
          Java(s"""|for (Stack s : destinations) {
                   |  removedCards.add(s.get());
                   |}""".stripMargin).statements()

        case _ : RemoveSingleCardMove =>
          Java(s"""removedCard = source.get();""".stripMargin).statements()

        case _ : ResetDeckMove =>
          Java(s"""|// Note destinations contain the stacks that are to
                   |// be reformed into a single deck.
                   |for (Stack s : destinations) {
                   |  while (!s.empty()) {
                   |	source.add(s.get());
                   |  }
                   |}""".stripMargin).statements()

        case _ : DeckDealMove =>
          Java(s"""|for (Stack s : destinations) {
                   |  s.add (source.get());
                   |}""".stripMargin).statements()

        case _ : ColumnMove =>
          Java(s"""destination.push(movingColumn);""").statements()
      }
    }
    val semanticType: Type = move(moveSymbol, move.doStatements)
  }

  /** Every move class needs a constructor with helper fields. */
  class MoveHelper(m:Move, name:SimpleName, moveSymbol: Type) {
    def apply() : Seq[BodyDeclaration[_]] = {
      m match {
        case _ : FlipCardMove =>
          Java(s"""|//Card movingCard;
                   |public $name(Stack from) {
                   |  this(from, from);
                   |}""".stripMargin).classBodyDeclarations()

        case _ : SingleCardMove =>
          Java(s"""|Card movingCard;
                   |public $name(Stack from, Card card, Stack to) {
                   |  this(from, to);
                   |  this.movingCard = card;
                   |}""".stripMargin).classBodyDeclarations()

        case _ : DeckDealMove => Seq.empty

        // place to store removed cards.
        case _ : RemoveMultipleCardsMove =>
          Java(s"""|java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();
                   |public $name(Stack dests[]) {
                   |  this(null, dests);
                   |}
                   |""".stripMargin).classBodyDeclarations()

        case _ : RemoveSingleCardMove =>
          Java(s"""|Card removedCard = null;
                   |public $name(Stack src) {
                   |  this(src, null);
                   |}
                   |""".stripMargin).classBodyDeclarations()

        case _ : ResetDeckMove => Seq.empty

          /**
            * Fundamental API for moving multiple cards is to have numInColumn holding number.
            * This becomes relevant in PotentialMoveOneCardFromStack...
            */
        case _ : ColumnMove =>
          Java(s"""|Column movingColumn;
                   |int numInColumn;
                   |public $name(Stack from, Column cards, Stack to) {
                   |  this(from, to);
                   |  this.movingColumn = cards;
                   |  this.numInColumn = cards.count();
                   |}""".stripMargin).classBodyDeclarations()
      }
    }

    val semanticType: Type = move(moveSymbol, move.helper)
  }

  /**
    *
    * @param elementType     Type of Element for which controller is synthesized.
    */
  class WidgetController(elementType: Type) {
    def apply(rootPackage: Name,
              designate: SimpleName,
              nameOfTheGame: SimpleName,
              autoMoves: Seq[Statement],
              mouseClicked: Seq[Statement],
              mouseReleased: Seq[Statement],
              mousePressed: ((SimpleName,SimpleName) => Seq[Statement])): CompilationUnit = {

      shared.controller.java.Controller.render(
        RootPackage = rootPackage,
        Designate = new SimpleName(elementType.toString()),   // was name.... TODO: HACK
        NameOfTheGame = nameOfTheGame,
        AutoMoves = autoMoves,
        MouseClicked = mouseClicked,
        MousePressed = mousePressed,
        MouseReleased = mouseReleased
      ).compilationUnit()

    }
    val semanticType: Type =  packageName =>:
      controller(elementType, className) =>:
      variationName =>:
      game(game.autoMoves) =>:
      controller(elementType, controller.clicked) =>:
      controller(elementType, controller.released)  =>:
      (drag(drag.variable, drag.ignore) =>: controller(elementType, controller.pressed)) =>:
      controller(elementType, complete)
  }

  // generative classes for each of the required elements. This seems much more generic than worth being buried here.
  // PLEASE SIMPLIFY. TODO
  // HACK
  class NameDef(cons: Constructor, value: String) {
    def apply(): SimpleName = Java(value).simpleName()
    val semanticType: Type = cons
  }

  class ClassNameDef(moveNameType: Type, value: String) extends NameDef(widget(moveNameType, className), value)

  class MovableElementNameDef(moveNameType: Type, value: String) extends NameDef(widget(moveNameType, widget.movable), value)
  class SourceWidgetNameDef(moveNameType: Type, value: String) extends NameDef(widget(moveNameType, widget.source), value)
  class TargetWidgetNameDef(moveNameType: Type, value: String) extends NameDef(widget(moveNameType, widget.target), value)

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
      packageName =>:
        widget(moveNameType, className) =>:
        widget(moveNameType, widget.movable) =>:
        widget(moveNameType, widget.source) =>:
        widget(moveNameType, widget.target) =>:
        widget(moveNameType, complete)
  }

  class IgnoreClickedHandler(source:Type) {
    def apply(): Seq[Statement] = Seq.empty
    val semanticType: Type = controller(source, controller.clicked)
  }

  /**
    * Some variations need to deny release.
    *
    * Simply grab the dragging source from the container and return the moving widget.
    */
  class IgnoreReleasedHandler(source:Type) {
    def apply(): Seq[Statement] = {
      Java(s"""c.getDragSource().returnWidget(w);""").statements()
    }

    val semanticType: Type = controller(source, controller.released)
  }

  /**
    * When a Press can be ignored, use this
    */
  class IgnorePressedHandler(source:Symbol) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""$ignoreWidgetVariableName = true;""").statements()
    }

    val semanticType: Type =
      drag(drag.variable, drag.ignore) =>: controller(source, controller.pressed)
  }

  /** Essential combinator for naming the ClassName for a controller. */
  class ControllerNaming(source:Type) {
    def apply(): SimpleName = Java(source.toString).simpleName()
    val semanticType: Type = controller(source, className)
  }

  class ReleaseHandlerDef(source:Type, stmts:Seq[Statement]) {
    def apply(): Seq[Statement] = stmts
    val semanticType: Type = controller (source, controller.released)
  }

  // Guards to ensure statements execute only for ColumnView (multiCard move)
  @combinator object ColumnViewCheck {
    def apply: Expression = Java("w instanceof ColumnView").expression()
    val semanticType: Type = press.column
  }

  @combinator object CardViewCheck {
    def apply: Expression = Java("w instanceof CardView").expression()
    val semanticType: Type = press.card
  }
}
