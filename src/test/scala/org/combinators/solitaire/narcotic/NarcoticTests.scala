package org.combinators.solitaire.narcotic

import org.combinators.cls.interpreter._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.narcotic.Domain
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class NarcoticTests extends FunSpec {

  describe("The possible inhabited domain models") {
    val domainModel: Solitaire = new Domain()

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it ("should have a tableau of size 4") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 4)
        }
        it ("should have a deck") {
          assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Stock).next().size() == 1)
        }

        describe("For synthesis") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)

          // Handles all of the default structural elements from the domain model
          it("Check Standard Domain Model") {
            new SolitaireDomainTest().validateDomain(Gamma, domainModel)
          }

        }
      }
    }
  }
}