package archway

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Move, Solitaire, SolitaireContainerTypes}
import domain.archway.{ArchwayContainerTypes, Domain}
import org.combinators.solitaire.archway.{ArchwayDomain, Controllers}
import org.combinators.solitaire.shared.{Helper, SemanticTypes}
import org.scalatest.FunSpec

import scala.collection.JavaConverters._
class ArchwayTests extends FunSpec {

  describe("Inhabitation") {
    val domainModel:Solitaire = new Domain()

    describe("Domain Model") {
      it("Tableau is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 4)
      }
      it("Aces foundation is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
      }
      it("Kings foundation is size 4.") {
        assert(domainModel.containers.get(ArchwayContainerTypes.KingsDown).size == 4)
      }
      it("Reserve is size 11.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Reserve).size == 11)
      }


      describe("For synthesis") {
        val controllerRepository = new ArchwayDomain(domainModel) with Controllers {}
        import controllerRepository._

        val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
        val Gamma= controllerRepository.init(reflected, domainModel)
        val helper = new Helper(controllerRepository)

        //

        it ("Check for base classes") {
          assert(helper.singleClass("ConstraintHelper",    Gamma.inhabit[CompilationUnit](constraints(complete))))

          // helper classes
          assert(helper.singleClass("AcesUpPile",           Gamma.inhabit[CompilationUnit]('AcesUpPileClass)))
          assert(helper.singleClass("KingsDownPile",        Gamma.inhabit[CompilationUnit]('KingsDownPileClass)))
          assert(helper.singleClass("AcesUpPileView",       Gamma.inhabit[CompilationUnit]('AcesUpPileViewClass)))
          assert(helper.singleClass("KingsDownPileView",    Gamma.inhabit[CompilationUnit]('KingsDownPileViewClass)))

          // note that there are two copies of game(complete) -- one solvable, and one that is not.
          assert(helper.singleClass("Archway",                  Gamma.inhabit[CompilationUnit](game(complete :&: game.solvable))))
          assert(helper.singleClass("AcesUpPileController",     Gamma.inhabit[CompilationUnit](controller('AcesUpPile, complete))))
          assert(helper.singleClass("KingsDownPileController",  Gamma.inhabit[CompilationUnit](controller('KingsDownPile, complete))))
          assert(helper.singleClass("PileController",           Gamma.inhabit[CompilationUnit](controller(pile, complete))))
          assert(helper.singleClass("ColumnController",         Gamma.inhabit[CompilationUnit](controller(column, complete))))

          // Ensure all moves in the domain generate move classes as Compilation Units
          for (mv:Move <- domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala) {
            val sym = Constructor(mv.name)
            assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
          }

          // potential moves are derived only from drag moves.
          for (mv:Move <- domainModel.getRules.drags.asScala) {
            val sym = Constructor(mv.name)
            assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))

            // based on domain model, we know whether potential move is a single-card move or a multiple-card move
            if (mv.isSingleCardMove) {
              assert(helper.singleClass("Potential" + mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.potential, complete))), "Can't synthesize:" + mv.name)
            } else {
              assert(helper.singleClass("Potential" + mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.potentialMultipleMove, complete))), "Can't synthesize:" + mv.name)
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

        // these are implied by the successful completion of 'game'
        it ("Structural validation") {
          assert(helper.singleInstance[SimpleName](Gamma.inhabit[SimpleName](variationName)))
          assert(helper.singleInstance[Name](Gamma.inhabit[Name](packageName)))
        }
      }
    }
  }
}
