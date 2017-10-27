package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import domain.{Move, Solitaire, SolitaireContainerTypes}
import domain.narcotic.Domain
import org.combinators.solitaire.shared._
import org.scalatest.FunSpec
import scala.collection.JavaConverters._

class NarcoticTests extends FunSpec {

  describe("The possible inhabited domain models") {
   val domainModel:Solitaire = new Domain()

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 4)
        }
        it("should have a deck") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Stock).size == 1)
        }

//          .addJob[CompilationUnit](move('RemoveAllCards :&: move.generic, complete))
//          .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
//          .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
//          .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
//
//          // only need potential moves for those that are DRAGGING...
//          .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
//

        describe("For synthesis") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}
          import controllerRepository._

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma= controllerRepository.init(reflected, domainModel)
          val helper = new Helper(controllerRepository)

          it ("Check for base classes") {
            assert(helper.singleClass("ConstraintHelper",    Gamma.inhabit[CompilationUnit](constraints(complete))))

            assert(helper.singleClass("Narcotic",                 Gamma.inhabit[CompilationUnit](game(complete))))
            assert(helper.singleClass("PileController",           Gamma.inhabit[CompilationUnit](controller(pile, complete))))
            assert(helper.singleClass("DeckController",           Gamma.inhabit[CompilationUnit](controller(deck, complete))))

            // Ensure all moves in the domain generate move classes as Compilation Units
            val combined = domainModel.getRules.drags.asScala ++ domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala
            for (mv:Move <- combined) {
              val sym = Constructor(mv.name)
              assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
            }

            // some potentials remain for the Narcotic variation.
            assert(helper.singleClass("PotentialMoveCard", Gamma.inhabit[CompilationUnit](move('MoveCard :&: move.potential, complete))))
          }

          // these are implied by the successful completion of 'game'
          it ("Structural validation") {
            assert(helper.singleInstance[SimpleName](Gamma.inhabit[SimpleName](variationName)))
            assert(helper.singleInstance[Name](Gamma.inhabit[Name](packageName)))
          }
        }
      }
    }
  }
}
