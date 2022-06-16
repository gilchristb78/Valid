package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.milligancell.FreeCell

package object freecell {

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](8)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](4)(FreeCell),
  )
  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    //only first 4 cols get a 7th
    while (colNum < 9)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  2))
      if (colNum < 4) {
        deal = deal :+ DealStep(ElementTarget(Tableau, colNum))
      }
      colNum += 1
    }
    deal
  }
  def buildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card), SameSuit(card, topDestination))
  }

  def buildOnEmptyTableau(card: MovingCard.type): Constraint = {
    IsKing(card)
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(card, topDestination),  SameSuit(card, topDestination))
  }

  def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    IsAce(card)
  }

  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), buildOnTableau(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))

  val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))

  val tableauToFoundationMove:Move = SingleCardMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move)))
  val fromTableauToReserve:Move = SingleCardMove("TableauToReserve", Drag,
    source=(Tableau,Truth), target=Some((Reserve, IsEmpty(Destination))))

  // should be "NotEmpty" not Truth
  val fromReserveToReserve:Move = SingleCardMove("ReserveToReserve", Drag,
    source=(Reserve,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val fromReserveToTableau:Move = SingleCardMove("ReserveToTableau", Drag,
    source=(Reserve,Truth), target=Some((Tableau, tt_move)))

  val fromReserveToFoundation:Move = SingleCardMove("ReserveToFoundation", Drag,
    source=(Reserve,Truth), target=Some((Foundation, tf_move)))

  val freecell:Solitaire = {

    Solitaire(name="Freecell",
      structure = structureMap,
      layout=stockTableauColumnLayout(2),  // HACK
      deal = getDeal,
      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau,fromReserveToFoundation ),
      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      customizedSetup = Seq.empty
    )
  }
}
