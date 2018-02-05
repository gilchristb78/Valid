package org.combinators.solitaire.bigforty

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter._
import domain.bigforty.Domain
import domain.{Solitaire, SolitaireContainerTypes}
import org.combinators.TypeNameStatistics
import org.combinators.solitaire.shared.SolitaireDomainTest
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class BigFortyTests extends FunSpec {

  describe("Inhabitation") {
    val domainModel:Solitaire = new Domain()

    describe("KlondikeDomain Model") {
      it("Tableau is size 4.") {
        assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Tableau).next().size() == 10)
      }
      it("Foundation is size 4.") {
        assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Foundation).next().size() == 4)
      }
      it("Stock is size 1.") {
        assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Stock).next().size() == 1)
      }
      it("Waste is size 1.") {
        assert(domainModel.containers.asScala.filter(x => x.`type` == SolitaireContainerTypes.Waste).next().size() == 1)
      }


      describe("For synthesis") {
        val controllerRepository = new gameDomain(domainModel) with controllers {}

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
