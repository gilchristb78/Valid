package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import org.combinators.cls.types.syntax._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.scalatest.FunSpec
import test.Helper

trait SolitaireDomainTargets extends org.combinators.solitaire.shared.JavaSemanticTypes {
}

class SolitaireDomainTest extends FunSpec with SolitaireDomainTargets {
  def validateDomain(Gamma: ReflectedRepository[_], domainModel: Solitaire) = {

    val helper = new Helper()

    // Ensure all extended classes exist
    domainModel.specializedElements.foreach(e =>
      assert(helper.singleClass(e.name, Gamma.inhabit[CompilationUnit](classes(e.name))))
    )

    assert(helper.singleClass(domainModel.name, Gamma.inhabit[CompilationUnit](game(complete))))
    assert(helper.singleClass("ConstraintHelper", Gamma.inhabit[CompilationUnit](constraints(complete))))

    domainModel.structure.flatMap(pair => pair._2).foreach(elem => {
      val el: String = elem.name

      val elt: Constructor = Constructor(el)

      //  make sure not visible
      assert(helper.singleClass(el + "Controller", Gamma.inhabit[CompilationUnit](controller(elt, complete))))
    })

    // TODO: Get testing back into shape
//    // Ensure all moves in the domain generate move classes as Compilation Units
//    for (mv: Move <- domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala) {
//      val sym = Constructor(mv.getName)
//      assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
//    }
//
//    // potential moves are derived only from drag moves.
//    for (mv: Move <- domainModel.getRules.drags.asScala) {
//      val sym = Constructor(mv.getName)
//      assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
//
//      // based on domain model, we know whether potential move is a single-card move or a multiple-card move
//      if (mv.isSingleCardMove) {
//        assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potential, complete))), "Can't synthesize:" + mv.getName)
//      } else {
//        assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potentialMultipleMove, complete))), "Can't synthesize:" + mv.getName)
//      }
//    }

    // these are implied by the successful completion of 'game'
    assert(helper.singleInstance[SimpleName](Gamma.inhabit[SimpleName](variationName)))
    assert(helper.singleInstance[Name](Gamma.inhabit[Name](packageName)))
  }
}

