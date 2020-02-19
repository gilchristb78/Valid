package org.combinators.solitaire.shared.cls

import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.JavaSemanticTypes
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain.{StockContainer, _}

object Synthesizer extends JavaSemanticTypes {

  /**
    * Compute all targets to be synthesized.
    *
    * @param model      FreeCellDomain Model which contains the information about the solitaire variation
    * @return
    */
  def allTargets(model:Solitaire): Seq[Constructor] = {
      standardTargets() ++
      computeControllersFromDomain(model) ++
      computeSpecialClasses(model) ++
      computeMovesFromDomain(model) ++
      generateTestCases(model)
        .distinct
  }

  def newTargets():Seq[Constructor] = {
    Seq(sinan(sinan.begin))
  }

  /**
    * Standard targets in all variations
    *
    * Take out game(complete) since (for some unknown reason) I need to start with one already produced,
    * so sine this one is always present, we take it out
    *
    * @return       Default Targets suitable for KombatSolitaire framework
    */
  def standardTargets() :Seq[Constructor] =
    Seq(game(complete),
        constraints(complete))

//
//  // awkward. Must map to SemanticTypes; annoying lower case, which could be fixed by just using the same
//  // Capitalization in the JavaSemanticTypes
//  // TODO: Fix this
//  def map(element: String): Type = {
//    if (element == "BuildablePile") return Constructor("buildablePile")
//    if (element == "Card") return  Constructor("card")
//    if (element == "Column") return  Constructor("column")
//    if (element == "Deck") return  Constructor("deck")
//    if (element == "Pile") return  Constructor("pile")
//
//    Constructor(element)
//  }

  /**
    * Determine controllers from the containers. Unfortunately, need to map the names
    * based on the semantic Types.
    *
    * TODO: Possible we can eliminate this by changing JavaSemanticTypes to use actual class name?
    *
    * @param model    FreeCellDomain Model which contains the information about the solitaire variation
    * @return
    */
  def computeControllersFromDomain(model:Solitaire) : Seq[Constructor] = {

    var targets :Seq[Constructor] = Seq.empty

    for (ct <- model.structure.keys) {
      if (model.layout.isVisible(ct)) {
        val name = ct match {
          case StockContainer => "Deck"
          case _ => model.structure(ct).head.name
        }

        targets = targets :+ controller(Constructor(name), complete)
      }
    }

//    for (c <- model.containers().asScala) {
//      if (model.isVisible(c)) {
//        for (t <- c.types.asScala) {
//          //val element = map(t)
//          targets = targets :+ controller(Constructor(t), complete)
//        }
//      }
//    }

    targets
  }

  /**
    * Specialized classes get their own targets.
    *
    * @param model   FreeCellDomain Model which contains the information about the solitaire variation
    * @return
    */
  def computeSpecialClasses(model:Solitaire) : Seq[Constructor] = {

    var targets :Seq[Constructor] = Seq.empty

    //for (e <- model.domainElements().asScala) {
    for (e <- model.specializedElements) {
      targets = targets :+ classes(e.name)
      targets = targets :+ classes(e.name + "View")
    }

//    // from specialized element can always get view by appending 'View'
//    for (e <- model.domainViews().asScala) {
//      targets = targets :+ classes (e.name)
//    }

    targets
  }

  /**
    * Given the model produce all test cases
    *
    * @param model  Solitaire FreeCellDomain Model
    * @return       Targets suitable for KombatSolitaire framework
    */
  def generateTestCases(model:Solitaire) :Seq[Constructor] = {
    var targets :Seq[Constructor] = Seq.empty

    targets = targets :+ classes("TestCases")

    targets
  }

  /**
    * Given the domain object, compute the targets for the required moves.
    *
    * @param model  Solitaire FreeCellDomain Model
    * @return       Targets suitable for KombatSolitaire framework
    */
  def computeMovesFromDomain(model:Solitaire) :Seq[Constructor] = {

    var targets:Seq[Constructor] = Seq.empty
    for (m <- model.moves.filter(p => (p.gesture == Press) || (p.gesture == Click))) {
      val sym = Constructor(m.name)
      targets = targets :+ move(sym :&: move.generic, complete)
    }

//    for (mv:Move <- model.getRules.presses.asScala ++ model.getRules.clicks.asScala) {
//      val sym = Constructor(mv.getName)
//      targets = targets :+ move(sym :&: move.generic, complete)
//    }

    // potential moves are derived only from drag moves.
    //for (mv:Move <- model.getRules.drags.asScala) {
    for (m <- model.moves.filter(p => p.gesture == Drag)) {
      val sym = Constructor(m.name)
      targets = targets :+ move(sym :&: move.generic, complete)

      // based on domain model, we know whether potential move is a single-card move or a multiple-card move
      // only do if chosen to be solvable
      if (model.solvable) {
        if (m.isSingleCard) {
          targets = targets :+ move(sym :&: move.potential, complete)
        } else {
          targets = targets :+ move(sym :&: move.potentialMultipleMove, complete)
        }
      }
    }

    targets
  }
}

