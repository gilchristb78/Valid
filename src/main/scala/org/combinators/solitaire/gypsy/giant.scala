package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints

package object giant extends variationPoints {

  override def getDeal: Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau)))
  }

  override def foundationToTableauConstraint:Constraint = AndConstraint(
    IsEmpty(Destination),
    OrConstraint(
    //IsEmpty(Destination),
    AndConstraint(
      OppositeColor(MovingCard, TopCardOf(Destination)),
      NextRank(TopCardOf(Destination), MovingCard))
    ))

  val giant:Solitaire = {
    Solitaire(name = "Giant",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, buildFoundation, flipMove, foundationToTableauMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      customizedSetup = Seq(TableauToEmptyTableau, TableauToNextTableau, TableauToEmptyFoundation, TableauToNextFoundation,
        TableauToTableauMultipleCards, TableauToEmptyTableauMultipleCards)
    )
  }
}