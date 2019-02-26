package org.combinators.solitaire.castle

import org.combinators.cls.interpreter._
import org.combinators.solitaire.domain.{Foundation, Tableau}
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

class CastleTests extends FunSpec {
  describe("The possible inhabited domain models") {
    val domainModel = castle

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 8") {
          assert(castle.structure.get(Tableau).size == 8)
        }
        it("should have a foundation of size 4") {
          assert(castle.structure.get(Foundation).size == 4)
        }

        describe("For synthesis") {
          val controllerRepository = new CastleDomain(domainModel) with controllers {}

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
