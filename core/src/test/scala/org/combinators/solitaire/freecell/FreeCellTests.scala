package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.Solitaire
import org.scalatest._

class FreeCellTests extends FunSpec {

  describe("The possible inhabited domain models") {
    lazy val domainModelRepository = new game {}
    lazy val GammaDomainModel =
      ReflectedRepository(domainModelRepository, classLoader = this.getClass.getClassLoader)
    lazy val possibleDomainModels: InhabitationResult[Solitaire] =
      GammaDomainModel.inhabit[Solitaire]('Variation ('FreeCell))

    it("should not be infinite") {
      assert(!possibleDomainModels.isInfinite)
    }
    it("should include exactly one result") {
      assert(possibleDomainModels.terms.values.flatMap(_._2).size == 1)
    }

    describe("(using the only possible domain model)") {
      lazy val domainModel = possibleDomainModels.interpretedTerms.index(0)
      describe("the domain model") {
        it("should have a tableau of size 8") {
          assert(domainModel.getTableau.size == 8)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.getFoundation.size == 4)
        }

        describe("(when used to create a repository)") {
          describe("the inhabited solitaire variation main classes") {
            lazy val fc_repository = new gameDomain(domainModel)
            lazy val Gamma = ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader)
            lazy val job = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
            lazy val results = job.run()
            it("should not be infinite") {
              assert(!results.isInfinite)
            }
            lazy val interpretedResults = results.interpretedTerms.values.flatMap(_._2)
            it("should include excatly one result") {
              assert(interpretedResults.size == 1)
            }
            it("should include a class named FreeCell") {
              assert(interpretedResults.head.getClassByName("FreeCell").isPresent)
            }
          }

          describe("the inhabited column controllers") {
            lazy val controllerRepository = new gameDomain(domainModel) with columnController with pilecontroller {}
            lazy val Gamma =
              controllerRepository.init(
                ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
                domainModel)
            lazy val job = Gamma.InhabitationBatchJob[CompilationUnit]('Controller('Column))
            lazy val results = job.run()
            it("should not be infinite") {
              assert(!results.isInfinite)
            }
            lazy val interpretedResults = results.interpretedTerms.values.flatMap(_._2)
            it("should include excatly one result") {
              assert(interpretedResults.size == 1)
            }
            it("should include a class named ColumnController") {
              assert(interpretedResults.head.getClassByName("ColumnController").isPresent)
            }
          }

          describe("the inhabited pile controllers") {
            lazy val controllerRepository = new gameDomain(domainModel) with columnController with pilecontroller {}
            lazy val Gamma =
              controllerRepository.init(
                ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
                domainModel)
            lazy val job =
              Gamma.InhabitationBatchJob[CompilationUnit]('Controller('FreePile))
                      .addJob[CompilationUnit]('Controller('HomePile))
            lazy val (freePileControllerResults, homePileControllerResults) = job.run()
            it("should not be infinite") {
              assert(!freePileControllerResults.isInfinite)
              assert(!homePileControllerResults.isInfinite)
            }
            it("should each include excatly one result") {
              assert(freePileControllerResults.terms.values.flatMap(_._2).size == 1)
              assert(homePileControllerResults.terms.values.flatMap(_._2).size == 1)
            }
            it("should include a class named FreePileController") {
              assert(freePileControllerResults.interpretedTerms.index(0).getClassByName("FreePileController").isPresent)
            }
            it("should include a class named HomePileController") {
              assert(homePileControllerResults.interpretedTerms.index(0).getClassByName("HomePileController").isPresent)
            }
          }
        }
      }
    }
  }
}
      //    .addJob[CompilationUnit]('HomePileClass)
      //    .addJob[CompilationUnit]('FreePileClass)
      //    .addJob[CompilationUnit]('HomePileViewClass)
      //    .addJob[CompilationUnit]('FreePileViewClass)
      //    .addJob[CompilationUnit]('Move('MoveColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard  :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceFreePileCard :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('ShuffleFreePile :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard :&: 'PotentialMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'PotentialMove, 'CompleteMove))
