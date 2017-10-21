package archway

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.archway.{ArchwayContainerTypes, Domain}
import org.combinators.solitaire.archway.{ArchwayDomain, Controllers, Game}
import org.combinators.solitaire.shared.Helper

class ArchwayTests extends Helper {

  describe("Inhabitation") {
    val domainModel:Solitaire = new Domain()

    describe("Domain Model") {
      it("Tableau is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 4)
      }
      it("Aces foundation is size 4.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
      }
      it("Kings foundation is size 4.") {
        assert(domainModel.containers.get(ArchwayContainerTypes.KingsDown).size == 4)
      }
      it("Reserve is size 11.") {
        assert(domainModel.containers.get(SolitaireContainerTypes.Reserve).size == 11)
      }

      lazy val archway_repository = new ArchwayDomain(domainModel) with Controllers {}
      lazy val Gamma = archway_repository.init(
        ReflectedRepository(archway_repository, classLoader = this.getClass.getClassLoader), domainModel)

      containsClass(singleInstance(Gamma, domainModel, 'SolitaireVariation), "Archway")
      containsClass(singleInstance(Gamma, domainModel, 'Controller ('AcesUpPile)), "AcesUpPileController")
      containsClass(singleInstance(Gamma, domainModel, 'Controller ('KingsDownPile)), "KingsDownPileController")
      containsClass(singleInstance(Gamma, domainModel, 'Controller ('Column)), "ColumnController")
      containsClass(singleInstance(Gamma, domainModel, 'Controller ('Pile)), "PileController")

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
