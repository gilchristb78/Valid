package org.combinators.solitaire.klondike

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.Solitaire
import org.combinators.solitaire.shared._

class KlondikeTests extends Helper {

  describe("The possible inhabited domain models") {
    lazy val domainModelRepository = new game {}
    lazy val GammaDomainModel =
      ReflectedRepository(domainModelRepository, classLoader = this.getClass.getClassLoader)
    lazy val possibleDomainModels: InhabitationResult[Solitaire] =
      GammaDomainModel.inhabit[Solitaire]('Variation ('Klondike))

    it("should not be infinite") {
      assert(!possibleDomainModels.isInfinite)
    }
    it("should include exactly one result") {
      assert(possibleDomainModels.terms.values.flatMap(_._2).size == 1)
    }

    describe("(using the only possible domain model)") {
      lazy val domainModel:Solitaire = possibleDomainModels.interpretedTerms.index(0)
      describe("the domain model") {
        it("should have a tableau of size 7") {
          assert(domainModel.getTableau.size == 7)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.getFoundation.size == 4)
        }
        it("should have a waste pile") {
          assert(domainModel.getWaste.size == 1)
        }
        it("should have a deck") {
          assert(domainModel.getStock.size == 1)
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new KlondikeDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          checkExistence(Gamma, domainModel, 'SolitaireVariation,          "Klondike")
          checkExistence(Gamma, domainModel, 'Controller ('BuildablePile), "BuildablePileController")
          checkExistence(Gamma, domainModel, 'Controller ('WastePile),     "WastePileController")
          checkExistence(Gamma, domainModel, 'Controller ('Pile),          "PileController")
          checkExistence(Gamma, domainModel, 'Controller ('Deck),          "DeckController")
        }
      }
    }
  }
}
