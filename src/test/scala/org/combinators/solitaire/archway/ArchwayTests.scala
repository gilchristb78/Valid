package org.combinators.solitaire.archway

import org.combinators.cls.interpreter._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.archway.{ArchwayContainerTypes, Domain}
import org.combinators.solitaire.domain.{Foundation, Reserve, Tableau}
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

import scala.collection.JavaConverters._
class ArchwayTests extends FunSpec {

  describe("Inhabitation") {
    val domainModel = archway

    describe("KlondikeDomain Model") {
      it("Tableau is size 4.") {
        assert(domainModel.structure.get(Tableau).size == 4)
      }
      it("Aces foundation is size 4.") {
        assert(domainModel.structure.get(Foundation).size == 4)
      }
      it("Kings foundation is size 4.") {
        assert(domainModel.structure.get(KingsDownFoundation).size == 4)
      }
      it("Reserve contains 13 piles.") {
        assert(domainModel.structure.get(Reserve).size == 13)
      }

      describe("For synthesis") {
        val controllerRepository = new ArchwayDomain(domainModel) with controllers {}

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
