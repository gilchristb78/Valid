package org.combinators.solitaire.idiot

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.idiot.Domain
import org.combinators.solitaire.shared._

class IdiotTests extends Helper {

  describe("The possible inhabited domain models") {
    val domainModel:Solitaire = new Domain()

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it("should have a tableau of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 4)
        }
        it("should have a deck") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Stock).size == 1)
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new gameDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          containsClass(singleInstance(Gamma, domainModel, 'SolitaireVariation), "Idiot")
          containsClass(singleInstance(Gamma, domainModel, 'Controller('Column)), "ColumnController")
          containsClass(singleInstance(Gamma, domainModel, 'Controller('Deck)), "DeckController")
        }
      }
    }
  }
}
