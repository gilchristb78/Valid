package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.solitaire.shared
import _root_.java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared.compilation._

trait Controller extends Base with shared.Moves with generic.JavaCodeIdioms with UnitTestCaseGeneration with SemanticTypes {

  private val logger = Logging.getLogger(ActorSystem("Controller"), "Controller")
  logger.info("Controller logging activated...")

  // TODO: FOR NOW INSERT THE DEFAULT TEST CASE HERE
  // shared logic to process rules as needed for Solitaire extensions
  // Note: this creates Move classes for each of the moves that are
  // defined in either the presses, drags, or clicks sets 
  def createMoveClasses[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    // TODO: MOVE OUTSIDE TO MAKE MORE GENERIC
    //Only print test cases if testsetup is defined
    if(s.testSetup.nonEmpty){
      println("Test setup found for variation: " + s.name)
      updated = updated.addCombinator(new SolitaireTestSuite(s))
    }else{
      println("no setup found")
    }

    //val combined = s.getRules.drags.asScala ++ s.getRules.presses.asScala ++ s.getRules.clicks.asScala

    // If same move appears multiple times, bad things happen. Perhaps filter out to be safe?
    for (m <- s.moves.distinct) {
      val moveSymbol = Symbol(m.name)

      logger.debug ("    -- " + moveSymbol + " defined")
      updated = updated
        .addCombinator(new ClassNameDef(moveSymbol, m.name))
        .addCombinator(new ClassNameGenerator(moveSymbol, m.name))
        .addCombinator(new UndoGenerator(m, moveSymbol))
        .addCombinator(new DoGenerator(m, moveSymbol))
        .addCombinator(new MoveHelper(m, moveSymbol))
        .addCombinator(new StatementCombinator (m.constraints, moveSymbol))

      /**
        * A move typically contains a single source and a single destination. For some
        * moves, there are multiple destinations (typically a deal, or remove cards) and
        * that requires different combinator.
        */
      if (m.isSingleDestination) {
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

    s.moves.filter(m => m.gesture == Drag).foreach (mv => {
      val srcBase = s.structure(mv.source._1).head.name
      //val srcBase = mv.source._1.name
      val tgtBase = s.structure(mv.target.get._1).head.name
      val movable = mv.movableElement.name

      val moveString = mv.name
      val moveSymbol = Constructor(moveString)

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
      mv.moveType match {
        case SingleCard =>
          updated = updated
              .addCombinator (new PotentialMoveSingleCard(moveSymbol))

       case MultipleCards =>
          updated = updated
              .addCombinator (new PotentialMoveMultipleCards(moveSymbol, Java("Column").simpleName()))

        case _ => throw new RuntimeException("Invalid drag:" + mv.moveType)
      }
    })

    updated
  }


  def generateMoveLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    // identify all unique pairs and be sure to generate handler for
    // these cases. NOTE: TAKE FROM RULES NOT FROM S since that does not
    // have the proper instantiations of the elements inside
    var drag_handler_map:Map[ContainerType,List[Move]] = Map()
    var press_handler_map:Map[ContainerType,List[Move]] = Map()

    s.moves.filter(m => m.gesture == Drag).foreach (inner_move => {

      // handle release events
      val tgtBaseHolder = inner_move.target
      val tgtBase:ContainerType = tgtBaseHolder.get._1

       if (!drag_handler_map.contains(tgtBase)) {
        drag_handler_map += (tgtBase -> List(inner_move))
      } else {
        val old:List[Move] = drag_handler_map(tgtBase)
        val newList:List[Move] = old :+ inner_move
        drag_handler_map -= tgtBase
        drag_handler_map += (tgtBase -> newList)
      }

      // handle press events
      val srcBase = inner_move.source._1

      if (!press_handler_map.contains(srcBase)) {
        press_handler_map += (srcBase -> List(inner_move))
      } else {
        val old:List[Move] = press_handler_map(srcBase)
        val newList:List[Move] = old :+ inner_move
        press_handler_map -= srcBase
        press_handler_map += (srcBase -> newList)
      }
    })


    // add press moves which are independent of drag; keep track of native press by container type
    //
    // TODO: I expect this can become a one-liner....
    //var haveNativePress:Map[ContainerType,Boolean] = Map()

    val haveNativePress = s.moves.collect { case m if m.gesture == Press => m.source._1 }

    // find all source constraints for all moves and package together into single OrConstraint. If any move
    // is a ColumnMove (which means it is triggered by a press then drag) this block adds a ColumnMoveHandler
    // to properly allow some moves to be initiating of a drag.

    press_handler_map.keys.foreach { container =>
      val list: List[Move] = press_handler_map(container)

      // if any of the Moves is a ColumnMove (or rowMove), then we must create a pre-constraint filter for drags
      var multipleCardsMove:List[Move] = List.empty
      var pressMoves:List[Move] = List.empty
      for (m <- list) {
        m.moveType match {
          case MultipleCards => multipleCardsMove = m :: multipleCardsMove
          case _ => pressMoves = m :: pressMoves       // any other moves go here
        }
      }

      if (multipleCardsMove.nonEmpty) {
        val cons: List[Constraint] = list.map(x => x.source._2)
        val or: OrConstraint = OrConstraint(cons: _*)

        // every element inside has same TYPE so just take head
        //s.structure.values.head.map(e => e.name).foreach (typeName => {
        s.structure.flatMap(c => c._2.distinct).toSeq.distinct.foreach { e =>
          val typeName = e.name
          val tpe = Constructor(typeName)

          // if there are any lingering press events (i.e., not all drag) then we need to somehow
          // combine these two properly. Detect by inference.
          val terminal = if (haveNativePress.contains(container)) {
            // up to domain-version controller's to combine together to make final press...
            controller(tpe, controller.dragStart)
          } else {
            controller(tpe, controller.pressed)
          }

          // Only RowView objects create RowView movables; all others create ColumnView... This is
          // a bit of hack from framework. TODO: FIX
          logger.debug("TypeName:" + typeName)
          val dragType: SimpleName = if (typeName == "Row") {
            Java("RowView").simpleName()
          } else /* if (typeName == "Column") */ {
            Java("ColumnView").simpleName()
          }
          updated = updated
            .addCombinator(new ColumnMoveHandler(tpe, Java(typeName).simpleName(), or, terminal, dragType))
        }
      }
    }


    // key is Container, value is List of moves; this block deals with release events.
    drag_handler_map.keys.foreach{ k =>
      // iterate over moves in the handler_map(k)
      var lastID:Option[Constructor] = None
      drag_handler_map(k).foreach { m =>

        val moveString = m.name
        val moveSymbol:Type = Symbol(moveString)

        val curID:Constructor = dynamic(Symbol(UUID.randomUUID().toString))


        val viewType =
          m.moveType match {
            case SingleCard => press.card
            case MultipleCards => press.column    // TODO: HACK: FIX
            case _ => throw new RuntimeException("Invalid drag:" + m.moveType)
//            case RowMove => press.row
          }

        logger.debug (moveSymbol + ":" + viewType)
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

      val originalTarget = s.structure(k).head
      //val originalTarget = k.types().next()
      val typ:Type = Symbol(originalTarget.name)

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
      m.moveType match {
        case SingleCard => Java(s"movingCard").simpleName
        case MultipleCards => Java(s"movingCards").simpleName
        case _ => throw new RuntimeException("Invalid drag:" + m.moveType)
      }
    }
    val semanticType: Type = move(moveSymbol, move.draggingVariableCardName)
  }

  /** When given a Move (SingleCardMove or ColumnMove) ascribes proper Undo. */
  /** Same code, just by coincidence. */
  class UndoGenerator(m:Move, moveSymbol:Type) {
    def apply(generators: CodeGeneratorRegistry[Seq[Statement]]): Seq[Statement] =  {
      new SeqStatementCombinator(m).apply(generators)

    }
    val semanticType: Type = constraints(constraints.undo_generator) =>: move(moveSymbol, move.undoStatements)
  }

  /** When given a Move (SingleCardMove or ColumnMove) ascribes proper Do. */
  /** Same code, just by coincidence. */
  class DoGenerator(m:Move, moveSymbol:Type) {
    def apply(generators: CodeGeneratorRegistry[Seq[Statement]]): Seq[Statement] =  {
      new SeqStatementCombinator(m).apply(generators)

    }
    val semanticType: Type = constraints(constraints.do_generator) =>: move(moveSymbol, move.doStatements)
  }

  /** Every move class needs a constructor with helper fields. */
  class MoveHelper(m:Move, moveSymbol: Type) {
    def apply() : Seq[BodyDeclaration[_]] = {
      val s:String = new HelperDeclarationCombinator(m).apply(constraintCodeGenerators.helperGenerators)
      Java(s).classBodyDeclarations()

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
        Designate = Java(elementType.toString).simpleName(),   // was name.... TODO: HACK
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
  class IgnorePressedHandler(source:Type) {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (_: SimpleName, ignoreWidgetVariableName: SimpleName) =>
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

  // Guards to ensure statements execute only for ColumnView (multiCard move)
  @combinator object RowViewCheck {
    def apply: Expression = Java("w instanceof RowView").expression()
    val semanticType: Type = press.row
  }

  @combinator object CardViewCheck {
    def apply: Expression = Java("w instanceof CardView").expression()
    val semanticType: Type = press.card
  }
}
