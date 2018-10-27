package pysolfc.shared

import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.PythonSemanticTypes

trait Structure extends PythonSemanticTypes {

  //
  //  /** Generic class to synthesize a given Python block as a semantic type. */
  //  class PythonStructure(stmts: Python, tpe: Constructor) {
  //    def apply: Python = stmts
  //
  //    val semanticType: Type = tpe
  //  }
  //

  // ultimately every stack extension needs to provide the following:
  //  class SequenceStack_StackMethods:
  //    def _isSequence(self, cards):
  //      # Are the cards (being moved) in a basic sequence for our stack ?
  //      raise SubclassResponsibility
  //
  //    def acceptsCards(self, from_stack, cards):
  //      if not self.basicAcceptsCards(from_stack, cards):
  //        return False
  //      # cards must be an acceptable sequence
  //      if not self._isAcceptableSequence(cards):
  //        return False
  //
  //      # now determine whether allowed to place on top.
  //      # self.cards is destination, while cards is movingColumn
  //
  //      # [topcard + cards] must be an acceptable sequence
  //      if self.cards and not self._isAcceptableSequence([self.cards[-1]] + cards):
  //        return False
  //      return True
  //
  //    def canMoveCards(self, cards):
  //      return self.basicCanMoveCards(cards) and self._isMoveableSequence(cards)

  // entities known to subclasses:
  //    * s.talon        the deck
  //    * s.foundation   the foundation
  //    * s.rows         the tableau
  //    * s.reserves     the reserves
  //    * s.waste        when used with a deck, the waste pile


  // defines Structure.
  def constructHelperClasses[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = gamma

    // drag will be responsible for the release; press will be responsible for source constraints.
    // I believe flip and deal moves have to be handled in special way.
    var drag_handler_map: Map[ContainerType, List[Move]] = Map()
    var press_handler_map: Map[ContainerType, List[Move]] = Map()

//    val inner_rules_it = s.getRules.drags
//    while (inner_rules_it.hasNext) {
//      val inner_move = inner_rules_it.next()
    s.moves.filter(m => m.gesture == Drag).foreach (inner_move => {

      // handle release events
      val tgtBaseHolder = inner_move.target
      val tgtBase:ContainerType = tgtBaseHolder.get._1

      if (!drag_handler_map.contains(tgtBase)) {
        drag_handler_map += (tgtBase -> List(inner_move))
      } else {
        val old: List[Move] = drag_handler_map(tgtBase)
        val newList: List[Move] = old :+ inner_move
        drag_handler_map -= tgtBase
        drag_handler_map += (tgtBase -> newList)
      }
    })

//    val press_rules_it = s.getRules.presses
//    while (press_rules_it.hasNext) {
//      val press_move = press_rules_it.next()
    s.moves.filter(m => m.gesture == Press).foreach (press_move => {
      // handle press events
      val srcBase = press_move.source._1 // TODO: PICK UP HERE

      if (!press_handler_map.contains(srcBase)) {
        press_handler_map += (srcBase -> List(press_move))
      } else {
        val old: List[Move] = press_handler_map(srcBase)
        val newList: List[Move] = old :+ press_move
        press_handler_map -= srcBase
        press_handler_map += (srcBase -> newList)
      }
    })

    // TODO: Press events that do not involve STOCK are to be handled by LocalClassConstruction;
    // only

    updated = updated
      .addCombinator(new PythonFieldConstruction(s, drag_handler_map))
      .addCombinator(new PythonLocalClassConstruction(s, drag_handler_map, press_handler_map))
      .addCombinator(new PythonStockConstruction(s, press_handler_map))

    updated
  }

  class PythonFieldConstruction(solitaire: Solitaire, map:Map[ContainerType, List[Move]]) {
    //var clazzes:Seq[Python] = Seq.empty
    def apply: Python = {
//      map.keys.foreach { container =>
//        clazzes = clazzes :+ oneField(solitaire, container)
//      }
//
//      Python(clazzes.mkString("\n"))
      Python("# Structure would be added here.")
    }

    val semanticType: Type = game(pysol.structure)
  }


  /**
    * Handle press events...
    *
    *
    * handle press actions. These currently fall into a number of cases:
    * (a) DealDeckMove; (b) ResetDeck; (c) FlipCardMove; (d) RemoveSingleCardMove; (e) RemoveMultipleCardsMove
    */
  class PythonStockConstruction(s:Solitaire, map:Map[ContainerType, List[Move]]) {

    def apply(generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts:Seq[Python] = Seq.empty

      // the pressMap container has as its key the container for which a press is initiated. For
      // Deck Deal, the target receives the cards. For ResetDeck, the target represents the source
      map.keys.foreach { ct =>
        val list: List[Move] = map(ct)

        ct match {
          case StockContainer =>

            val one: Python = talonClass(s, generators, StockContainer, list)
            stmts = stmts :+ one

          case _ =>

        }

        // what about containers which are never the start of a move

      }

      // convert into one mega statement
      Python(stmts.mkString("\n"))
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(pysol.pressClasses)
  }

  /**
    * All widget elements with specialized logic need their own subclasses.
    *
    * Decks need to handled specially. In PySolFC, there is a fundamental TalonWaste combination, which
    * is useful, but this doesn't match all games, so we likely have to detach entirely and create our
    * own custom Talon class
    *
    * @param s           solitaire domain
    * @param dragMap     dragMap of events
    */
  class PythonLocalClassConstruction(s:Solitaire, dragMap:Map[ContainerType, List[Move]], pressMap:Map[ContainerType, List[Move]]) {

    def apply(generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts:Seq[Python] = Seq.empty

      // the DragMap container has as its key the container for which a drag is completing. This will
      // therefore miss those widgets which are press-only (or initiating of drags only).
      var processedContainers:Seq[ContainerType] = Seq.empty

      dragMap.keys.foreach { container =>
        val list: List[Move] = dragMap(container)

        // only grab the constraints (source or target) if the respective source or target is container
        var srcCons:Seq[Constraint] = Seq.empty
        var targetCons:Seq[Constraint] = Seq.empty

        for (m <- list) {
          if (m.source._1 == container) {
            srcCons = srcCons :+ m.source._2
          }

          if (m.target.isDefined) {
            if (m.target.get._1 == container) {
              targetCons = targetCons :+ m.target.get._2
            }
          }
        }
//        val srcCons: List[Constraint] = list.map(x => x.getSourceConstraint)
//        val targetCons: List[Constraint] = list.map(x => x.getTargetConstraint)

        val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
        val targetOr: OrConstraint = new OrConstraint(targetCons: _*)

        val one:Python = oneClass(s, generators, container, srcOr, targetOr, pressMap.get(container))
        processedContainers = processedContainers :+ container

        // what about containers which are never the start of a move
        stmts = stmts :+ one
      }

      // find those containers which had been involved, but only as recipient.
      dragMap.keys.foreach { container =>
        val list: List[Move] = dragMap(container)
        val srcCons: List[Constraint] = list.map(x => x.source._2)

        list.foreach { move =>
          val src:ContainerType = move.source._1
          if (!processedContainers.contains(src)) {
            processedContainers = processedContainers :+ src

            val srcOr: OrConstraint = OrConstraint(srcCons: _*)
            val two: Python = oneClass(s, generators, src, srcOr, OrConstraint(Falsehood), pressMap.get(container))

            stmts = stmts :+ two
          }
        }
      }

      // convert into one mega statement
      Python(stmts.mkString("\n"))
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(pysol.dragClasses)
  }

  @combinator object CombinePressDrag {
    def apply(head:Python, tail:Python):Python = {

      Python(s"""|${head.getCode}
                 |${tail.getCode}""".stripMargin)
    }
    val semanticType:Type = game(pysol.pressClasses) =>: game(pysol.dragClasses) =>: game(pysol.classes)
  }


  /**
    * This becomes the 'de factor' controller. Type contained within the container is the key name to
    * focus on.
    *
    * Make sure not to construct the same class TWICE. This might happen, for example, when there
    * is an element to which you cannot Drag, but which can be the start of a drag.
    *
    * Note each visible talon needs own class.
    *
    * There MAY be optional press actions for these containers, which are passed in, and would be handled
    * by raw clickHandler
    */
  def oneClass(s:Solitaire, generators: CodeGeneratorRegistry[Python], container:ContainerType,
               srcOr:OrConstraint, targetOr:OrConstraint, pressList:Option[List[Move]]): Python = {

    // these are the OR constraints for press/release drag moves
    val press: Python = generators(srcOr).get
    val release: Python = generators(targetOr).get

    // these are the actions triggered just by a Press.
    val justPressedHeaderStmts =
      s"""
         |# -1 means continue as drag; 1 means handled; 0 means ignore?
         |def clickHandler(self, event):
       """.stripMargin
    var actions:String = ""
    if (pressList.isDefined) {

      for (m <- pressList.get) {
        val cons: Python = generators(m.source._2).get
        val src = m.source._1

        // TODO: hack to expand
        val source = src match {
          case Tableau => "tableau()"
          case Waste => "waste()"
        }

        // here is where we deal with these ones...
        m.moveType match {
          case RemoveMultipleCards =>

            actions = actions +
              s"""
                 |from_stack = self.cards
                 |if $cons:
                 |    numRemoved = 0
                 |    for st in $source:
                 |        if len(st.cards) > 0:
                 |            numRemoved = numRemoved + 1
                 |            st.moveMove(1, garbage())
                 |    return numRemoved
                 |else:
                 |    return -1
              """.stripMargin

          case RemoveSingleCard =>
            actions = actions +
              s"""
                 |from_stack = self.cards
                 |if $cons:
                 |    if len(self.cards) > 0:
                 |        numRemoved = numRemoved + 1
                 |        self.moveMove(1, garbage())
                 |        return 1
                 |    else:
                 |        return 0
                 |else:
                 |    return -1
              """.stripMargin

          case FlipCard =>
            actions = actions +
              s"""
                 |from_stack = self.cards
                 |if $cons:
                 |    if len(self.cards) > 0:
                 |        self.flipMove()
                 |        return 1
                 |    else:
                 |        return 0
                 |else:
                 |    return -1
              """.stripMargin
        }
      }
    }
    val justPressedStmts = Python(actions)
    val justPress:Python = if (actions == "") {
      Python("")
    } else {
      Python(
        s"""
           |$justPressedHeaderStmts
           |${justPressedStmts.indent}
       """.stripMargin)
    }

    // get the type of element contained within the container.
    val element = s.structure(container).head
    val tpe:String = element.name    // TODO: CHECK does this make sense?
    //val tpe:String = container.types.next
    val name:String = "My" + tpe
    var base = "None"

    // map KlondikeDomain types into PySolFC types. This is in wrong place. Make Code Generator extension
    // HACK. TODO: FIX THIS
    element match {
      case Column =>
        base = "SequenceRowStack"

      case Pile =>
        base = "ReserveStack"  // in general, a pile is just a pile

      case BuildablePile =>
        base = "SequenceRowStack"

      case _ =>
        base = "ReserveStack"
    }

    container match {
      case Foundation =>
        base = "AbstractFoundationStack"   // but when within a Foundation, it becomes AbstractFoundationStack

      case st:Stock =>
        // have to extend Talon which has different kinds of extensions.
        val talonStmts =
          s"""
             |
           """.stripMargin
        return Python(talonStmts)

      case _ => // ignore everything else.
    }

    val stmts = s"""
                   |class ${name}Stack($base):
                   |    def canMoveCards(self, cards):
                   |        # protect against empty moves
                   |        if len(cards) == 0:
                   |            return False
                   |        return $press
                   |
                   |${justPress.indent.getCode}
                   |
                   |    def acceptsCards(self, from_stack, cards):
                   |        # protect against empty moves
                   |        if len(cards) == 0:
                   |            return False
                   |        return $release
                   |""".stripMargin

    Python(stmts)
  }

  /**
    * This becomes the 'de factor' controller for the Talon. Instead of using 'or' to allow conditions, we
    * need to synthesize full groups
    *
    * SAMPLE:
    *
    * class MyTalonStack(TalonStack):
    *
    * def canDealCards(self):
    *    return True
		*
	  * # can act on mouse press
    * def dealCards(self, sound=False, shuffle=False):
    *   if self.cards:
    *       self.dealToStacks(tableau())
    *    else:
    *        # reset Deck
    *        for st in tableau():
    *            if len(st.cards) > 0:
    *                for card in st.cards:
    *                    card.showBack()
    *                st.moveMove(len(st.cards), self)
    *
    * if CONDITION:
    *    DOMOVE
    * if CONDITION:
    *    DOMOVE
    */
  def talonClass(s:Solitaire, generators: CodeGeneratorRegistry[Python], container:ContainerType, list:List[Move]): Python = {

    // For each of the cases, have to generate constraints
    var actions:String = ""
    for (m <- list) {
      val cons:Python = generators(m.source._2).get
      val tgt = m.target.get

      // hack to expand. TODO: FIX THIS CENTRALLY
      val target = tgt._1 match {
        case Tableau => "tableau()"
        case Waste => "waste()"
      }

      m.moveType match {
        case DealDeck(_) =>
            actions = actions +
              s"""
                 |from_stack = self.cards
                 |if $cons:
                 |    self.dealToStacks($target)
                 |    return
              """.stripMargin

        case ResetDeck =>
            actions = actions +
              s"""
                 |from_stack = self.cards
                 |if $cons:
                 |    for st in $target:
                 |        if len(st.cards) > 0:
                 |            for card in st.cards:
                 |                card.showBack()
                 |            st.moveMove(len(st.cards), self)
                 |    return
                 |""".stripMargin
      }
    }

    // Only interested in DeckMove and ResetDeckMove for now

    // get the type of element contained within the container.
    val element = s.structure(container).head
    val tpe:String = element.name
    val name:String = "My" + tpe
    val dealAction:Python = Python(actions)

    val stmts = s"""
                   |class ${name}Stack(TalonStack):
                   |
                   |    def __init__(self, x, y, game, max_rounds=-1, num_deal=1):
                   |        TalonStack.__init__(self, x, y, game, max_rounds=max_rounds, num_deal=num_deal)
                   |
                   |    def canDealCards(self):
                   |        return True
                   |
                   |    # can act on mouse press
                   |    def dealCards(self, sound=False, shuffle=False):
                   |${dealAction.indent.indent.getCode}
                   |
                   |""".stripMargin

    Python(stmts)
  }
}
