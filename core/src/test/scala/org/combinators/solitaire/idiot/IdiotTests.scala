package org.combinators.solitaire.idiot

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.idiot.Domain
import org.combinators.solitaire.shared._
import org.scalatest.FunSpec

class IdiotTests extends FunSpec  {

  describe("The possible inhabited domain models") {
    val domainModel:Solitaire = new Domain()

    describe("(using the only possible domain model)") {
      describe("the domain model") {
        it ("should have a tableau of size 4") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Tableau).size == 4)
        }
        it ("should have a deck") {
          assert(domainModel.containers.get(SolitaireContainerTypes.Stock).size == 1)
        }


        describe("(when used to create a repository)") {
          val controllerRepository = new gameDomain(domainModel) with controllers {}
          import controllerRepository._

          val reflected = ReflectedRepository(controllerRepository, classLoader = controllerRepository.getClass.getClassLoader)
          val Gamma = controllerRepository.init(reflected, domainModel)
          val helper = new Helper(controllerRepository)

          //reflected.combinators.foreach (println (_))

          val inhabitants = Gamma.inhabit[Any]('NameOfTheGame).interpretedTerms
          print (inhabitants.values.head)

          //assert(helper.singleInstance[SimpleName](Gamma, variationName).toString == "Idiot")

//          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, game(complete)), "Idiot")
//          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, controller(column, complete)), "ColumnController")
//          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, controller(deck, complete)), "DeckController")
        }
      }
    }
  }
}
