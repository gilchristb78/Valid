package org.combinators.solitaire.idiot

import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.{Solitaire, SolitaireContainerTypes}
import domain.idiot.Domain
import org.combinators.solitaire.shared._
import org.scalatest.FunSpec

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.CompilationUnit

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

          Gamma.combinators.foreach (println (_))

          val inhabitants = Gamma.inhabit[SimpleName](variationName).interpretedTerms.values.flatMap(_._2).iterator

          print (inhabitants.next())

          val cc = Gamma.inhabit[CompilationUnit](constraints(complete)).interpretedTerms.values.flatMap(_._2).iterator
          print (cc.next())

          val cc3 = Gamma.inhabit[CompilationUnit](controller(column, complete)).interpretedTerms.values.flatMap(_._2).iterator
          print (cc3.next())

       //   val cc2 = Gamma.inhabit[CompilationUnit](controller(deck, complete)).interpretedTerms.values.flatMap(_._2).iterator
       //   print (cc2.next())

//          controller(elementType, controller.clicked) =>:
//            controller(elementType, controller.released) =>:
//            (drag(drag.variable, drag.ignore) =>: controller(elementType, controller.pressed)) =>:


          val cc2 = Gamma.inhabit[Seq[Statement]](controller(deck, controller.clicked)).interpretedTerms.values.flatMap(_._2).iterator
          print ("CLICK:" + cc2.next())

          val cc4 = Gamma.inhabit[Seq[Statement]](controller(deck, controller.released)).interpretedTerms.values.flatMap(_._2).iterator
          print ("RELEASE:" + cc4.next())

          val cc5 = Gamma.inhabit[(SimpleName,SimpleName) => Seq[Statement]](drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)).interpretedTerms.values.flatMap(_._2).iterator
          print ("PRESS:" + cc5.next())

          val cc6 = Gamma.inhabit[Seq[Statement]](game(game.autoMoves)).interpretedTerms.values.flatMap(_._2).iterator
          print ("Auto:" + cc6.next())

          val cc7 = Gamma.inhabit[SimpleName](controller(deck, className)).interpretedTerms.values.flatMap(_._2).iterator
          println ("Deck:" + cc7.next())

          val cc8 = Gamma.inhabit[Name](packageName).interpretedTerms.values.flatMap(_._2).iterator
          println ("pkg:" + cc8.next())

          val cc9 = Gamma.inhabit[SimpleName](variationName).interpretedTerms.values.flatMap(_._2).iterator
          println ("var:" + cc9.next())

          //           val jobs = Gamma.InhabitationBatchJob[CompilationUnit](game(complete :&: game.solvable))
//            .addJob[CompilationUnit](constraints(complete))
//            .addJob[CompilationUnit](controller(deck, complete))
//            .addJob[CompilationUnit](controller(column, complete))
//            .addJob[CompilationUnit](move('RemoveCard :&: move.generic, complete))
//            .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
//            .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
//
//            .addJob[CompilationUnit](move('RemoveCard :&: move.potential, complete))
//            .addJob[CompilationUnit](move('MoveCard :&: move.potential, complete))
//            .addJob[CompilationUnit](move('DealDeck :&: move.potential, complete))

//          assert(helper.singleInstance[SimpleName](Gamma, variationName).toString == "Idiot")



          //          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, game(complete)), "Idiot")
//          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, controller(column, complete)), "ColumnController")
//          helper.containsClass(helper.singleInstance[CompilationUnit](Gamma, controller(deck, complete)), "DeckController")
        }
      }
    }
  }
}
