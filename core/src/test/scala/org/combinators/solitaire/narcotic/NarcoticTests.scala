package org.combinators.solitaire.narcotic

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import com.github.javaparser.ast.stmt.Statement
import domain.Solitaire
import org.combinators.solitaire.shared._

class NarcoticTests extends Helper {

  describe("The possible inhabited domain models") {
    lazy val domainModelRepository = new game {}
    lazy val GammaDomainModel =
      ReflectedRepository(domainModelRepository, classLoader = this.getClass.getClassLoader)
    lazy val possibleDomainModels: InhabitationResult[Solitaire] =
      GammaDomainModel.inhabit[Solitaire]('Variation ('Narcotic))

    it("should not be infinite") {
      assert(!possibleDomainModels.isInfinite)
    }
    it("should include exactly one result") {
      assert(possibleDomainModels.terms.values.flatMap(_._2).size == 1)
    }

    describe("(using the only possible domain model)") {
      lazy val domainModel:Solitaire = possibleDomainModels.interpretedTerms.index(0)
      describe("the domain model") {
        it("should have a tableau of size 4") {
          assert(domainModel.getTableau.size == 4)
        }
        it("should have a deck") {
          assert(domainModel.getStock.size == 1)
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new gameDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          checkExistenceTarget(Gamma, domainModel, 'Deck1)
          checkExistenceTarget(Gamma, domainModel, 'Deck2)
          checkExistenceTarget(Gamma, domainModel, 'Deck('Pressed))
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new gameDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          checkExistence(Gamma, domainModel, 'SolitaireVariation,          "Narcotic")
          checkExistence(Gamma, domainModel, 'Controller ('Pile),          "PileController")
          checkExistence(Gamma, domainModel, 'Controller ('Deck),          "DeckController")
        }
      }
    }
  }
}
