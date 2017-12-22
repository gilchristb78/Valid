package org.combinators.solitaire.bigforty

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.bigforty.Domain
import domain.{Move, Solitaire, SolitaireContainerTypes}
import org.scalatest.FunSpec
import test.Helper

import scala.collection.JavaConverters._

class BigFortyTests extends FunSpec {

  describe("Inhabitation") {
    val domainModel:Solitaire = new Domain()

    describe("Domain Model") {
      it("Tableau is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 10)
      }
      it("Foundation is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
      }
      it("Stock is size 1.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Stock).size == 1)
      }
      it("Waste is size 1.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Waste).size == 1)
      }


      describe("For synthesis") {
        val controllerRepository = new gameDomain(domainModel) with controllers {}
        import controllerRepository._

        val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
        val Gamma = controllerRepository.init(reflected, domainModel)
        val helper = new Helper()


        // Check parts of Game
        it ("Structural validation") {
          assert(helper.singleInstance[Seq[Statement]](Gamma.inhabit[Seq[Statement]](game(game.model))))
          assert(helper.singleInstance[Seq[Statement]](Gamma.inhabit[Seq[Statement]](game(game.view))))
          assert(helper.singleInstance[Seq[Statement]](Gamma.inhabit[Seq[Statement]](game(game.control))))
          assert(helper.singleInstance[Seq[Statement]](Gamma.inhabit[Seq[Statement]](game(game.deal))))
        }

        it ("Check for base classes") {
          assert(helper.singleClass("ConstraintHelper",    Gamma.inhabit[CompilationUnit](constraints(complete))))

          // helper classes
          assert(helper.singleClass("WastePile",           Gamma.inhabit[CompilationUnit]('WastePileClass)))
          assert(helper.singleClass("WastePileView",        Gamma.inhabit[CompilationUnit]('WastePileViewClass)))

          // note that there are two copies of game(complete) -- one solvable, and one that is not.
          assert(helper.singleClass("BigForty",                  Gamma.inhabit[CompilationUnit](game(complete))))
          assert(helper.singleClass("PileController",           Gamma.inhabit[CompilationUnit](controller(pile, complete))))
          assert(helper.singleClass("ColumnController",         Gamma.inhabit[CompilationUnit](controller(column, complete))))
          assert(helper.singleClass("DeckController",           Gamma.inhabit[CompilationUnit](controller(deck, complete))))
          assert(helper.singleClass("WastePileController",         Gamma.inhabit[CompilationUnit](controller('WastePile, complete))))

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

          // Ensure all moves in the domain generate move classes as Compilation Units
//          val combined = domainModel.getRules.drags.asScala ++ domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala
//          for (mv:Move <- combined) {
//            val sym = Constructor(mv.name)
//            assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
//          }
//
//          // would love to handle potential in automatic way; consider types of moves.
//          assert(helper.singleClass("PotentialReserveToTableau", Gamma.inhabit[CompilationUnit](move('ReserveToTableau :&: move.potential, complete))))
//          assert(helper.singleClass("PotentialReserveToFoundation", Gamma.inhabit[CompilationUnit](move('ReserveToFoundation :&: move.potential, complete))))
//
//          assert(helper.singleClass("PotentialTableauToFoundation", Gamma.inhabit[CompilationUnit](move('TableauToFoundation :&: move.potential, complete))))
//          assert(helper.singleClass("PotentialTableauToKingsFoundation", Gamma.inhabit[CompilationUnit](move('TableauToKingsFoundation :&: move.potential, complete))))
//          assert(helper.singleClass("PotentialReserveToKingsFoundation", Gamma.inhabit[CompilationUnit](move('ReserveToKingsFoundation :&: move.potential, complete))))
        }

      }
    }
  }
}
