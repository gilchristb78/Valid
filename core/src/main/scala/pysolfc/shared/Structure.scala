package pysolfc.shared

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.constraints.OrConstraint
import domain._
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.PythonSemanticTypes

import scala.collection.JavaConverters._

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

  // defines Structure.
  def constructHelperClasses[G <: SolitaireDomain](gamma: ReflectedRepository[G], s: Solitaire): ReflectedRepository[G] = {
    var updated = gamma

    // drag will be responsible for the release; press will be responsible for source constraints.
    // I believe flip and deal moves have to be handled in special way.
    var drag_handler_map: Map[Container, List[Move]] = Map()
    var press_handler_map: Map[Container, List[Move]] = Map()

    val inner_rules_it = s.getRules.drags
    while (inner_rules_it.hasNext) {
      val inner_move = inner_rules_it.next()

      // handle release events
      val tgtBaseHolder = inner_move.getTargetContainer
      val tgtBase = tgtBaseHolder.get

      if (!drag_handler_map.contains(tgtBase)) {
        drag_handler_map += (tgtBase -> List(inner_move))
      } else {
        val old: List[Move] = drag_handler_map(tgtBase)
        val newList: List[Move] = old :+ inner_move
        drag_handler_map -= tgtBase
        drag_handler_map += (tgtBase -> newList)
      }

      // handle press events
      val srcBase = inner_move.getSourceContainer

      if (!press_handler_map.contains(srcBase)) {
        press_handler_map += (srcBase -> List(inner_move))
      } else {
        val old: List[Move] = press_handler_map(srcBase)
        val newList: List[Move] = old :+ inner_move
        press_handler_map -= srcBase
        press_handler_map += (srcBase -> newList)
      }
    }

//
//    drag_handler_map.keys.foreach { container =>
//      val list: List[Move] = drag_handler_map(container)
//
//      val srcCons: List[Constraint] = list.map(x => x.sourceConstraint)
//      val targetCons: List[Constraint] = list.map(x => x.targetConstraint)
//
//      val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
//      val targetOr: OrConstraint = new OrConstraint(targetCons: _*)
//
//      updated = updated
//        .addCombinator(new PythonLocalClassConstruction(s, container, srcOr, targetOr))
//        .addCombinator(new PythonFieldConstruction(s, container))
//    }

    updated = updated
        .addCombinator(new PythonFieldConstruction(s, drag_handler_map))
        .addCombinator(new PythonLocalClassConstruction(s, drag_handler_map))

    updated
  }

  class PythonFieldConstruction(solitaire: Solitaire, map:Map[Container, List[Move]]) {
    var clazzes:Seq[Python] = Seq.empty
    def apply: Python = {
      map.keys.foreach { container =>
        clazzes = clazzes :+ oneField(solitaire, container)
      }

      Python(clazzes.mkString("\n"))
    }

    val semanticType: Type = game(pysol.structure)
  }


  class PythonLocalClassConstruction(s:Solitaire, map:Map[Container, List[Move]]) {

    def apply(generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts:Seq[Python] = Seq.empty
       map.keys.foreach { container =>
        val list: List[Move] = map(container)

        val srcCons: List[Constraint] = list.map(x => x.getSourceConstraint)
        val targetCons: List[Constraint] = list.map(x => x.getTargetConstraint)

        val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
        val targetOr: OrConstraint = new OrConstraint(targetCons: _*)

        val one:Python = oneClass(s, generators, container, srcOr, targetOr)
         stmts = stmts :+ one
      }

      // convert into one mega statement
      Python(stmts.mkString("\n"))
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(pysol.classes)
  }

  def oneField (s:Solitaire, container:Container):Python = {
     var clazz = ""

      // Makes a quick mapping from container type into class names
      if (container == s.containers.get(SolitaireContainerTypes.Foundation)) {
        clazz = clazz +
          s"""
             |Foundation_Class = MyFoundationStack
                   """.stripMargin
      }
      if (container == s.containers.get(SolitaireContainerTypes.Tableau)) {
        clazz = clazz +
          s"""
             |RowStack_Class = MyTableauStack
           """.stripMargin
      }

      // unlikely to have DRAG bring release to waste pile; here for press
      if (container == s.containers.get(SolitaireContainerTypes.Waste)) {
        clazz = clazz +
          s"""
             |Waste_Class = MyWasteStack
                   """.stripMargin
      }

      Python(clazz)
    }

  def oneClass(s:Solitaire, generators: CodeGeneratorRegistry[Python], container:Container, srcOr:OrConstraint, targetOr:OrConstraint): Python = {
      var name = "None"
      var base = "None"

      // these are the or constraints for release
      val press: Python = generators(srcOr).get
      val release: Python = generators(targetOr).get

      // Makes a quick mapping from container type into class names
      if (container == s.containers.get(SolitaireContainerTypes.Foundation)) {
        name = "MyFoundation"
        base = "AbstractFoundationStack"
      }
      if (container == s.containers.get(SolitaireContainerTypes.Tableau)) {
        name = "MyTableau"
        base = "SequenceRowStack"
      }
      if (container == s.containers.get(SolitaireContainerTypes.Stock)) {
        name = "MyTalon"
        base = "TalonStack"
      }
      // unlikely to have DRAG bring release to waste pile; here for press
      if (container == s.containers.get(SolitaireContainerTypes.Waste)) {
        name = "MyWaste"
        base = "SequenceRowStack"
      }

      val stmts = s"""
           |class ${name}Stack($base):
           |    def canMoveCards(self, cards):
           |        # protect against empty moves
           |        if len(cards) == 0:
           |            return False
           |        if $press:
           |            return True
           |        return False
           |
           |    def acceptsCards(self, from_stack, cards):
           |        # protect against empty moves
           |        if len(cards) == 0:
           |            return False
           |        if $release:
           |            return True
           |        return False
           |""".stripMargin

      Python(stmts)
    }


  /**
    * Knows that suits are identified by suit=i for 0..3
    */
  def layout_place_foundation(c: Container): Python = {

    var combined = ""
    for (r <- c.placements().asScala) {
      combined = combined +
        s"""
           |s.foundations.append(self.Foundation_Class(${r.x}, ${r.y}, self, suit=${r.idx}, max_move=0))
           |""".stripMargin
    }
    Python(combined)
  }

  // THESE ARE ROWS. How to show orientation
  def layout_place_tableau(c:Container): Python = {
    var combined = ""

    // tableau typically can be oriented vertically or horizontally
    /** Orientation. By default, vertical downwards. */
    val element:Element = c.iterator().next()

    val offsets = if (element.getVerticalOrientation) {
      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = 0, l.YOFFSET"
    } else {
      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0"
    }

    for (r <- c.placements().asScala) {
      combined = combined +
        s"""
           |stack = self.RowStack_Class(${r.x}, ${r.y}, self)
           |$offsets
           |s.rows.append(stack)""".stripMargin
    }
    Python(combined)
  }


  // trying to constrct a talong doesn't easily work.
  def layout_place_stock(c:Container): Python = {
    var combined = ""
    for (r <- c.placements().asScala) {
      combined = combined + s"""
                               |s.talon = TalonStack(${r.x}, ${r.y}, self)
                               |""".stripMargin
    }
    Python(combined)
  }

  /** Waste takes its structure from existing classWasteStack. Need to deal with rounds/num deal at a time. */
  def layout_place_stock_and_waste(stock:Container, waste:Container): Python = {
    var combined = ""

    for (r <- stock.placements().asScala) {
      combined = combined +
        s"""
           |s.talon =  WasteTalonStack(${r.x}, ${r.y}, self, max_rounds=1, num_deal=1)
                  """.stripMargin
    }

    for (r <- waste.placements().asScala) {
      combined = combined +
        s"""
           |s.waste =  WasteStack(${r.x}, ${r.y}, self)
            """.stripMargin
    }
    Python(combined)
  }

  /** Some games use a stock only to store cards which are all dealt out. */
  def layout_invisible_stock(stock:Container): Python = {
    Python(s"""
              |x, y = self.getInvisibleCoords()
              |s.talon = TalonStack(x, y, self)
              |""".stripMargin)
  }
}
