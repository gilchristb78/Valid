package org.combinators.solitaire.narcotic

import org.combinators.cls.interpreter._
import org.combinators.solitaire.domain.{StockContainer, Tableau}
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

class NarcoticTests extends FunSpec {

  describe("The possible inhabited domain models") {
    val domainModel = org.combinators.solitaire.narcotic.narcotic

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it ("should have a tableau of size 4") {
          val tableau = domainModel.structure.get(Tableau)
          assert(tableau.size == 4)
        }
        it ("should have a deck") {
          val deck = domainModel.structure.get(StockContainer)
          assert(deck.size == 1)
        }

        describe("For synthesis") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}

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
}