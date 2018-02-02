package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import domain.ui.UserInterface
import domain.{Move, Solitaire}
import org.combinators.cls.types.syntax._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.scalatest.FunSpec
import test.Helper

import scala.collection.JavaConverters._

trait SolitaireDomainTargets extends org.combinators.solitaire.shared.JavaSemanticTypes {
}

class SolitaireDomainTest extends FunSpec with SolitaireDomainTargets {
  def validateDomain(Gamma: ReflectedRepository[_], domainModel: Solitaire) = {

    val helper = new Helper()

    // Ensure all extended classes exist
    for (m <- domainModel.domainElements().asScala) {
      val name: String = m.getClass.getSimpleName
      assert(helper.singleClass(name, Gamma.inhabit[CompilationUnit](classes(name))))
    }

    for (v <- domainModel.domainViews().asScala) {
      val name: String = v.name
      assert(helper.singleClass(name, Gamma.inhabit[CompilationUnit](classes(name))))
    }

    assert(helper.singleClass(domainModel.name, Gamma.inhabit[CompilationUnit](game(complete))))
    assert(helper.singleClass("ConstraintHelper", Gamma.inhabit[CompilationUnit](constraints(complete))))

    val ui = new UserInterface(domainModel)
    val els_it = ui.controllers
    while (els_it.hasNext) {
      val el:String = els_it.next()

      val elt: Constructor = Constructor(el)

      //  make sure not visible
      assert(helper.singleClass(el + "Controller", Gamma.inhabit[CompilationUnit](controller(elt, complete))))
    }

    // Ensure all moves in the domain generate move classes as Compilation Units
    for (mv: Move <- domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala) {
      val sym = Constructor(mv.getName)
      assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
    }

    // potential moves are derived only from drag moves.
    for (mv: Move <- domainModel.getRules.drags.asScala) {
      val sym = Constructor(mv.getName)
      assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))

      // based on domain model, we know whether potential move is a single-card move or a multiple-card move
      if (mv.isSingleCardMove) {
        assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potential, complete))), "Can't synthesize:" + mv.getName)
      } else {
        assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potentialMultipleMove, complete))), "Can't synthesize:" + mv.getName)
      }
    }

    // these are implied by the successful completion of 'game'
    assert(helper.singleInstance[SimpleName](Gamma.inhabit[SimpleName](variationName)))
    assert(helper.singleInstance[Name](Gamma.inhabit[Name](packageName)))
  }
}

