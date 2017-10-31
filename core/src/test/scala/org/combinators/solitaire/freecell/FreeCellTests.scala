package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Name, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Move, Solitaire, SolitaireContainerTypes}
import domain.freeCell.Domain
import org.combinators.solitaire.shared.{Helper, SemanticTypes}
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class FreeCellTests extends FunSpec {

  describe("The possible inhabited domain models") {
    val domainModel:Solitaire = new Domain()

    describe ("(using the only possible domain model)") {
      describe("the domain model") {
        it ("should have a tableau of size 8") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 8)
        }
        it ("should have a foundation of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
        }

        describe("For synthesis") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}
          import controllerRepository._

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma= controllerRepository.init(reflected, domainModel)
          val helper = new Helper(controllerRepository)

          //

          it ("Check for base classes") {
            assert(helper.singleClass("ConstraintHelper",    Gamma.inhabit[CompilationUnit](constraints(complete))))

            // helper classes
            assert(helper.singleClass("HomePile",        Gamma.inhabit[CompilationUnit]('HomePileClass)))
            assert(helper.singleClass("FreePile",        Gamma.inhabit[CompilationUnit]('FreePileClass)))
            assert(helper.singleClass("HomePileView",    Gamma.inhabit[CompilationUnit]('HomePileViewClass)))
            assert(helper.singleClass("FreePileView",    Gamma.inhabit[CompilationUnit]('FreePileViewClass)))

            // note that there are two copies of game(complete) -- one solvable, and one that is not.
            assert(helper.singleClass("FreeCell",            Gamma.inhabit[CompilationUnit](game(complete :&: game.solvable))))
            assert(helper.singleClass("FreePileController",  Gamma.inhabit[CompilationUnit](controller('FreePile, complete))))
            assert(helper.singleClass("HomePileController",  Gamma.inhabit[CompilationUnit](controller('HomePile, complete))))
            assert(helper.singleClass("ColumnController",    Gamma.inhabit[CompilationUnit](controller(column, complete))))

            // Ensure all moves in the domain generate move classes as Compilation Units
            val combined = domainModel.getRules.presses.asScala ++ domainModel.getRules.clicks.asScala
            for (mv:Move <- combined) {
              val sym = Constructor(mv.name)
              assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))
            }

            // potential moves are derived from DRAG events only
            for (mv:Move <- domainModel.getRules.drags.asScala) {
              val sym = Constructor(mv.name)
              assert(helper.singleClass(mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.generic, complete))))

              // based on domain model, we know whether potential move is a single-card move or a multiple-card move
              if (mv.isSingleCardMove) {
                assert(helper.singleClass("Potential" + mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.potential, complete))))
              } else {
                assert(helper.singleClass("Potential" + mv.name, Gamma.inhabit[CompilationUnit](move(sym :&: move.potentialMultipleMove, complete))))
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
