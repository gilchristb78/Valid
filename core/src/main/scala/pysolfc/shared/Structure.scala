package pysolfc.shared

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.{Constructor, Type}
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.constraints.OrConstraint
import domain.{Constraint, Container, Move, Solitaire, _}
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.python.{ConstraintExpander, PythonSemanticTypes, constraintCodeGenerators}

trait Structure extends PythonSemanticTypes {


  /** Generic class to synthesize a given Python block as a semantic type. */
  class PythonStructure(stmts: Python, tpe: Constructor) {
    def apply: Python = stmts

    val semanticType: Type = tpe
  }


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

    var stmts = ""
    var clazzes = ""

    // drag will be responsible for the release; press will be responsible for source constraints.
    // I believe flip and deal moves have to be handled in special way.
    var drag_handler_map: Map[Container, List[Move]] = Map()
    var press_handler_map: Map[Container, List[Move]] = Map()

    val inner_rules_it = s.getRules.drags
    while (inner_rules_it.hasNext) {
      val inner_move = inner_rules_it.next()

      // handle release events
      val tgtBaseHolder = inner_move.targetContainer
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
      val srcBase = inner_move.srcContainer

      if (!press_handler_map.contains(srcBase)) {
        press_handler_map += (srcBase -> List(inner_move))
      } else {
        val old: List[Move] = press_handler_map(srcBase)
        val newList: List[Move] = old :+ inner_move
        press_handler_map -= srcBase
        press_handler_map += (srcBase -> newList)
      }
    }

    drag_handler_map.keys.foreach { container =>
      val list: List[Move] = drag_handler_map(container)

      val srcCons: List[Constraint] = list.map(x => x.sourceConstraint)
      val targetCons: List[Constraint] = list.map(x => x.targetConstraint)

      val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
      val targetOr: OrConstraint = new OrConstraint(targetCons: _*)

      // these are the or constraints for release
      val srcApp = new ConstraintExpander(srcOr, Constructor("Something"))
      val press: Python = srcApp.apply(constraintCodeGenerators.generators)

      val targetApp = new ConstraintExpander(targetOr, Constructor("SomethingElse"))
      val release: Python = targetApp.apply(constraintCodeGenerators.generators)

      var name = "None"
      var base = "None"

      // Makes a quick mapping from container type into class names
      if (container == s.containers.get(SolitaireContainerTypes.Foundation)) {
        name = "MyFoundation"
        base = "AbstractFoundationStack"
        clazzes = clazzes +
                  s"""
                     |Foundation_Class = MyFoundationStack
                   """.stripMargin
      }
      if (container == s.containers.get(SolitaireContainerTypes.Tableau)) {
        name = "MyTableau"
        base = "SequenceRowStack"
        clazzes = clazzes +
          s"""
             |RowStack_Class = MyTableauStack
           """.stripMargin
      }
      if (container == s.containers.get(SolitaireContainerTypes.Stock)) {
        name = "MyTalon"
        base = "TalonStack"
      }

      // unlikely to have DRAG bring release to waste pile; here for press
      if (container == s.containers.get(SolitaireContainerTypes.Waste)) {
        name = "MyWaste"
        base = "SequenceRowStack"
        clazzes = clazzes +
          s"""
             |Waste_Class = MyWasteStack
                   """.stripMargin
      }

      stmts = stmts +
        s"""
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
    }

    updated = updated
      .addCombinator(new PythonStructure(Python(stmts), game(pysol.classes)))
      .addCombinator(new PythonStructure(Python(clazzes), game(pysol.structure)))

    updated
  }
}
