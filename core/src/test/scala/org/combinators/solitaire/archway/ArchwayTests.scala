package archway

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.Solitaire
import org.combinators.solitaire.archway.{ArchwayDomain, Controllers, Game}
import org.combinators.solitaire.shared.Helper

class ArchwayTests extends Helper {

  describe("Inhabitation") {
    lazy val domainModelRepository = new Game {}
    lazy val GammaDomainModel =
      ReflectedRepository(domainModelRepository, classLoader = this.getClass.getClassLoader)
    lazy val possibleDomainModels: InhabitationResult[Solitaire] =
      GammaDomainModel.inhabit[Solitaire]('Variation('Archway))

    it("Not infinite.") {
      assert(!possibleDomainModels.isInfinite)
    }
    it("Returns one result.") {
      assert(possibleDomainModels.terms.values.flatMap(_._2).size == 1)
    }

    describe("Domain Model") {
      lazy val domainModel = possibleDomainModels.interpretedTerms.index(0)
      it("Tableau is size 4.") {
        assert(domainModel.getTableau.size == 4)
      }
      it("Aces foundation is size 4.") {
        assert(domainModel.getFoundation.size == 4)
      }
      it("Kings foundation is size 4.") {
        assert(domainModel.getContainer("KingsDownFoundation").size == 4)
      }
      it("Reserve is size 11.") {
        assert(domainModel.getReserve.size == 11)
      }

      lazy val archway_repository = new ArchwayDomain(domainModel) with Controllers {}
      lazy val Gamma = archway_repository.init(
        ReflectedRepository(archway_repository, classLoader = this.getClass.getClassLoader), domainModel)

      checkExistence(Gamma, domainModel, 'SolitaireVariation :&: 'Solvable, "Archway")
      checkExistence(Gamma, domainModel, 'Controller ('AcesUpPile), "AcesUpPileController")
      checkExistence(Gamma, domainModel, 'Controller ('KingsDownPile), "KingsDownPileController")
      checkExistence(Gamma, domainModel, 'Controller ('Column), "ColumnController")
      checkExistence(Gamma, domainModel, 'Controller ('Pile), "PileController")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToTableau :&: 'GenericMove, 'CompleteMove), "ReserveToTableau")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToFoundation :&: 'GenericMove, 'CompleteMove), "ReserveToFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('TableauToFoundation :&: 'GenericMove, 'CompleteMove), "TableauToFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('TableauToKingsFoundation :&: 'GenericMove, 'CompleteMove), "TableauToKingsFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToKingsFoundation :&: 'GenericMove, 'CompleteMove), "ReserveToKingsFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToTableau :&: 'PotentialMove, 'CompleteMove), "ReserveToTableau")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToFoundation :&: 'PotentialMove, 'CompleteMove), "ReserveToFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('TableauToFoundation :&: 'PotentialMove, 'CompleteMove), "TableauToFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('TableauToKingsFoundation :&: 'PotentialMove, 'CompleteMove), "TableauToKingsFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('ReserveToKingsFoundation :&: 'PotentialMove, 'CompleteMove), "ReserveToKingsFoundation")
//      checkExistence(Gamma, domainModel, 'Move ('TableauToTableau :&: 'GenericMove, 'CompleteMove), "TableauToTableau")

    }
  }
}
