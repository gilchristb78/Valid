package pysolfc.shared

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.constraints.{Falsehood, OrConstraint}
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
    * All widget elements with specialized logic need their own subclasses.
    *
    * @param s       solitaire domain
    * @param map     dragMap
    */
  class PythonLocalClassConstruction(s:Solitaire, map:Map[Container, List[Move]]) {

    def apply(generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts:Seq[Python] = Seq.empty

      // the DragMap container has as its key the container for which a drag is completing. This will
      // therefore miss those widgets which are press-only (or initiating of drags only).
      var processedContainers:Seq[Container] = Seq.empty
      map.keys.foreach { container =>
        val list: List[Move] = map(container)

        // only grab the constraints (source or target) if the respective source or target is container
        var srcCons:Seq[Constraint] = Seq.empty
        var targetCons:Seq[Constraint] = Seq.empty

        for (m <- list) {
          if (m.getSourceContainer.isSame(container)) {
            srcCons = srcCons :+ m.getSourceConstraint
          }
          if (m.getTargetContainer.isPresent) {
            if (m.getTargetContainer.get.isSame(container)) {
              targetCons = targetCons :+ m.getTargetConstraint
            }
          }
        }
//        val srcCons: List[Constraint] = list.map(x => x.getSourceConstraint)
//        val targetCons: List[Constraint] = list.map(x => x.getTargetConstraint)

        val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
        val targetOr: OrConstraint = new OrConstraint(targetCons: _*)

        val one:Python = oneClass(s, generators, container, srcOr, targetOr)
        processedContainers = processedContainers :+ container

        // what about containers which are never the start of a move
        stmts = stmts :+ one
      }

      // find those containers which had been involved, but only as recipient.
      map.keys.foreach { container =>
        val list: List[Move] = map(container)
        val srcCons: List[Constraint] = list.map(x => x.getSourceConstraint)

        list.foreach { move =>
          val src: Container = move.getSourceContainer
          if (!processedContainers.contains(src)) {
            processedContainers = processedContainers :+ src
            val srcOr: OrConstraint = new OrConstraint(srcCons: _*)
            val two: Python = oneClass(s, generators, src, srcOr, new OrConstraint(new Falsehood()))

            stmts = stmts :+ two
          }
        }
      }

      // convert into one mega statement
      Python(stmts.mkString("\n"))
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(pysol.classes)
  }


  /**
    * This becomes the 'de factor' controller. Type contained within the container is the key name to
    * focus on.
    *
    * Make sure not to construct the same class TWICE. This might happen, for example, when there
    * is an element to which you cannot Drag, but which can be the start of a drag.
    */
  def oneClass(s:Solitaire, generators: CodeGeneratorRegistry[Python], container:Container, srcOr:OrConstraint, targetOr:OrConstraint): Python = {
   // var name = "None"
    //var base = "None"

    // these are the or constraints for release
    val press: Python = generators(srcOr).get
    val release: Python = generators(targetOr).get

    // get the type of element contained within the container.
    val element:Element = container.iterator().next
    val tpe:String = container.types.next
    val name:String = "My" + tpe
    var base = "None"

    // map Domain types into PySolFC types. This is in wrong place. Make Code Generator extension
    // HACK
    element match {
      case c:Column =>
        base = "SequenceRowStack"

      case p:Pile =>
        base = "ReserveStack"  // in general, a pile is just a pile

      case bp:BuildablePile =>
        base = "SequenceRowStack"

      case _ =>
        base = "ReserveStack"
    }

    container match {
      case f:Foundation =>
        base = "AbstractFoundationStack"   // but when within a Foundation, it becomes AbstractFoundationStack

      case _ => // ignore everything else.
    }

    // Makes a quick mapping from container type into class names
//    if (container == s.containers.get(SolitaireContainerTypes.Foundation)) {
//      name = "MyFoundation"
//      base = "AbstractFoundationStack"
//    }
//    if (container == s.containers.get(SolitaireContainerTypes.Tableau)) {
//      name = "MyTableau"
//      base = "SequenceRowStack"
//    }
//    if (container == s.containers.get(SolitaireContainerTypes.Stock)) {
//      name = "MyTalon"
//      base = "TalonStack"
//    }
//    if (container == s.containers.get(SolitaireContainerTypes.Reserve)) {
//      name = "MyReerve"
//      base = "ReserveStack"
//    }
//    // unlikely to have DRAG bring release to waste pile; here for press
//    if (container == s.containers.get(SolitaireContainerTypes.Waste)) {
//      name = "MyWaste"
//      base = "SequenceRowStack"
//    }

    val stmts = s"""
                   |class ${name}Stack($base):
                   |    def canMoveCards(self, cards):
                   |        # protect against empty moves
                   |        if len(cards) == 0:
                   |            return False
                   |        return $press
                   |
                   |    def acceptsCards(self, from_stack, cards):
                   |        # protect against empty moves
                   |        if len(cards) == 0:
                   |            return False
                   |        return $release
                   |""".stripMargin

    Python(stmts)
  }



}
