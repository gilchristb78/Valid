package org.combinators.solitaire.klondike

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.klondike.Domain
import org.combinators.solitaire.shared._

class KlondikeTests (types:SemanticTypes) extends Helper(types) {

  describe("The possible inhabited domain models") {
    val domainModel:Solitaire = new Domain()
    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 7") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 7)
        }
        it("should have a foundation of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
        }
        it("should have a waste pile") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Waste).size == 1)
        }
        it("should have a deck") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Stock).size == 1)
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new KlondikeDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          containsClass(singleInstance(Gamma, 'SolitaireVariation), "Klondike")
          containsClass(singleInstance(Gamma, 'Controller ('BuildablePile)), "PileController")
          containsClass(singleInstance(Gamma, 'Controller ('WastePile)), "WastePileController")
          containsClass(singleInstance(Gamma, 'Controller ('Pile)), "PileController")
          containsClass(singleInstance(Gamma, 'Controller ('Deck)), "DeckController")

        }
      }
    }
  }
}
