package org.combinators.solitaire.castle

import org.combinators.cls.interpreter._
import domain.castle.Domain
import domain.{Solitaire, SolitaireContainerTypes}
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class CastleTests extends FunSpec {


  describe("The possible inhabited domain models") {
    val domainModel: Solitaire = new Domain()

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 8") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 8)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Foundation).next().size() == 4)
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
