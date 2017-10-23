package org.combinators.solitaire.narcotic

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import com.github.javaparser.ast.stmt.Statement
import domain.{Solitaire, SolitaireContainerTypes}
import domain.narcotic.Domain
import org.combinators.solitaire.shared._

class NarcoticTests (types:SemanticTypes) extends Helper(types) {

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

          checkExistenceTarget(Gamma, domainModel, 'Deck1)
          checkExistenceTarget(Gamma, domainModel, 'Deck2)
          checkExistenceTarget(Gamma, domainModel, 'Deck('Pressed))
        }

        describe("(when used to create a repository)") {
          lazy val controllerRepository = new gameDomain(domainModel) with controllers {}
          lazy val Gamma = controllerRepository.init(
            ReflectedRepository(controllerRepository, classLoader = this.getClass.getClassLoader),
            domainModel)

          containsClass(singleInstance(Gamma,  'SolitaireVariation), "Narcotic")
          containsClass(singleInstance(Gamma, 'Controller ('Pile)), "PileController")
          containsClass(singleInstance(Gamma, 'Controller ('Deck)), "DeckController")

        }
      }
    }
  }
}
