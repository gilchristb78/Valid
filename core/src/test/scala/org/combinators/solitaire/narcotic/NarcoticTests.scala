package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import domain.{Move, Solitaire, SolitaireContainerTypes}
import domain.narcotic.Domain
import org.scalatest.FunSpec
import test.Helper

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

        describe("For synthesis") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}
          import controllerRepository._

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)
          val helper = new Helper()

          it ("Check for base classes") {
            assert(helper.singleClass("ConstraintHelper",    Gamma.inhabit[CompilationUnit](constraints(complete))))

            assert(helper.singleClass("Narcotic",                 Gamma.inhabit[CompilationUnit](game(complete))))
            assert(helper.singleClass("PileController",           Gamma.inhabit[CompilationUnit](controller(pile, complete))))
            assert(helper.singleClass("DeckController",           Gamma.inhabit[CompilationUnit](controller(deck, complete))))

            // Ensure all moves in the domain generate move classes as Compilation Units
           for (mv:Move <- domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala) {
              val sym = Constructor(mv.getName)
              assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
            }

            // potential moves are derived only from drag moves.
            for (mv:Move <- domainModel.getRules.drags.asScala) {
              val sym = Constructor(mv.getName)
              assert(helper.singleClass(mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))

              // based on domain model, we know whether potential move is a single-card move or a multiple-card move
              if (mv.isSingleCardMove) {
                assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potential, complete))), "Can't synthesize:" + mv.getName)
              } else {
                assert(helper.singleClass("Potential" + mv.getName, Gamma.inhabit[CompilationUnit](move(sym :&: move.potentialMultipleMove, complete))), "Can't synthesize:" + mv.getName)
              }
            }
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
