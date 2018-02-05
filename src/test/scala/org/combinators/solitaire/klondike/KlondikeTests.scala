package org.combinators.solitaire.klondike

import org.combinators.cls.interpreter._
import domain.{Solitaire, SolitaireContainerTypes, klondike}
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class KlondikeTests extends FunSpec {

  describe("The possible inhabited domain models") {
    val domainModel: Solitaire = new klondike.KlondikeDomain()
    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 7") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 7)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Foundation).next().size() == 4)
        }
        it("should have a waste pile") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Waste).next().size() == 1)
        }
        it("should have a deck") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Stock).next().size() == 1)
        }

        describe("For synthesis") {
          val controllerRepository = new KlondikeDomain(domainModel) with controllers {}

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)

          // Handles all of the default structural elements from the domain model
          it ("Check Standard FreeCellDomain Model") {
            new SolitaireDomainTest().validateDomain(Gamma, domainModel)
          }
        }
      }
    }
  }

  describe("Variation deal by three") {
    val domainModel: Solitaire = new klondike.DealByThreeKlondikeDomain()
    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 7") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 7)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Foundation).next().size() == 4)
        }
        it("should have a waste pile") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Waste).next().size() == 1)
        }
        it("should have a deck") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Stock).next().size() == 1)
        }

        describe("For synthesis") {
          val controllerRepository = new KlondikeDomain(domainModel) with controllers {}

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)

          // Handles all of the default structural elements from the domain model
          it("Check Standard FreeCellDomain Model") {
            new SolitaireDomainTest().validateDomain(Gamma, domainModel)
          }
        }
      }
    }
  }

  describe("Variation EastCliff") {
    val domainModel: Solitaire = new klondike.EastCliff()
    describe("(using the only possible domain model)") {
      describe("the domain model") {

        // notable variation changes. Can't reset deck in EastCliff
        assert(domainModel.asInstanceOf[klondike.KlondikeDomain].numRedeals == 0)

        it("should have a tableau of size 7") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 7)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Foundation).next().size() == 4)
        }
        it("should have a waste pile") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Waste).next().size() == 1)
        }
        it("should have a deck") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Stock).next().size() == 1)
        }

        describe("For synthesis") {
          val controllerRepository = new KlondikeDomain(domainModel) with controllers {}

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)

          // Handles all of the default structural elements from the domain model
          it ("Check Standard FreeCellDomain Model") {
            new SolitaireDomainTest().validateDomain(Gamma, domainModel)
          }
        }
      }
    }
  }
}
