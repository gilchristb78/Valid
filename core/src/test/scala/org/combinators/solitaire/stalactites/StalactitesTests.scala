package org.combinators.solitaire.stalactites

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.stalactites.Domain
import org.combinators.solitaire.shared.{Helper, SemanticTypes}

class StalactitesTests (types:SemanticTypes) extends Helper(types) {

  describe("The possible inhabited domain models") {

    val domainModel:Solitaire = new Domain()

    describe ("(using the only possible domain model)") {
      describe("the domain model") {
        it ("should have a tableau of size 8") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 8)
        }
        it ("should have a foundation of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Foundation).size == 4)
        }
        it ("should have a reserve of size 2") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Reserve).size == 2)
        }

        describe ("(when used to create a repository)") {
          describe("the inhabited solitaire variation main classes") {
            lazy val fc_repository = new gameDomain(domainModel) with controllers {}
            lazy val Gamma = fc_repository.init(
              ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader),
              domainModel)

//            containsClass(singleInstance(Gamma, 'Augment('increment, 'SolitaireVariation)), "Stalactites")
//            checkExistence(Gamma, domainModel, 'Controller ('Pile), "PileController")
//            checkExistence(Gamma, domainModel, 'Controller ('ReservePile), "ReservePileController")
//            checkExistence(Gamma, domainModel, 'Controller ('Column), "ColumnController")
          }
        }
      }
    }
  }
}
      //    .addJob[CompilationUnit]('HomePileClass)
      //    .addJob[CompilationUnit]('FreePileClass)
      //    .addJob[CompilationUnit]('HomePileViewClass)
      //    .addJob[CompilationUnit]('FreePileViewClass)
      //    .addJob[CompilationUnit]('Move('MoveColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard  :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceFreePileCard :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('ShuffleFreePile :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard :&: 'PotentialMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'PotentialMove, 'CompleteMove))
